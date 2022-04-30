package ml.iamwhatiam.baostock.infrastructure.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Inflater;

@Slf4j
public class NettyClient {

    private final static boolean epoll;

    private final static int DEFAULT_SHARE_CONNECTIONS = 1;

    private final static String SERVER_TIME_OUT = "server timeout";

    private final static String CLIENT_TIME_OUT = "client timeout";

    private final static long DEFAULT_CONNECT_TIMEOUT = 3000L;

    private final static long DEFAULT_WAIT_TIME = 1000L;

    private final static String END_OF_RESPONSE = "<![CDATA[]]>\n";

    private static long defaultReadTimeout;

    private static int defaultShareConnections;

    private final String host;

    private final int port;

    private final boolean secure;

    private Bootstrap bootstrap;

    private final BlockingQueue<Channel> channels;

    static {
        epoll = System.getProperty("os.name").contains("linux") && Epoll.isAvailable();
        try {
            defaultReadTimeout = Long.parseLong(System.getProperty("baostock.socket.readTimeout"));
        } catch (Throwable ignore) {
            defaultReadTimeout = DEFAULT_WAIT_TIME;
        }
        try {
            defaultShareConnections = Integer.parseInt(System.getProperty("baostock.socket.connections"));
        } catch (Exception ignore) {
            defaultShareConnections = DEFAULT_SHARE_CONNECTIONS;
        }
    }

    public NettyClient(String host, int port, boolean secure) {
        this.host = host;
        this.port = port;
        this.secure = secure;
        channels = new LinkedBlockingQueue<>(defaultShareConnections);
        init();
        try {
            if(channels.isEmpty()) {
                connect();
            }
        } catch (Exception e){
            for(Channel channel : channels) {
                if (channel != null) {
                    channel.close();
                }
            }
        }
    }

    private void init() {
        EventLoopGroup eventExecutors = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        Class<? extends Channel> klazz = epoll ? EpollSocketChannel.class : NioSocketChannel.class;
        bootstrap = new Bootstrap();
        bootstrap.group(eventExecutors)
                .channel(klazz)
                .option(ChannelOption.SO_RCVBUF, 8196)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ByteBuf delimiter = Unpooled.copiedBuffer(END_OF_RESPONSE, StandardCharsets.UTF_8);
                        socketChannel.pipeline()
                                .addLast("stockDecoder", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter))
                                .addLast("decoder", new BaoStockByteToMessageDecoder())
                                .addLast("encoder", new BaoStockMessageToByteEncoder());
                    }
                });
    }

    private void connect() {
        connect(host, port, secure);
    }

    private void connect(String host, int port, boolean secure) {
        for(int num = channels.size(); num < defaultShareConnections; num++) {
            ChannelFuture channelFuture = bootstrap.connect(host, port);
//          boolean connected = channelFuture.awaitUninterruptibly(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
//          if(!connected || !channelFuture.isSuccess()) {
//              throw new RuntimeException("connect fail", channelFuture.cause());
//          }
            Channel newChannel = channelFuture.syncUninterruptibly().channel();
            channels.offer(newChannel);
        }
    }

    public <T extends BaoStockResponse> T request(BaoStockRequest msg) {
        return request(msg, defaultReadTimeout);
    }

    public <T extends BaoStockResponse> T request(BaoStockRequest request, long awaitMs) {
        long beforeBorrow = System.currentTimeMillis();
        Channel channel = borrowChannel(beforeBorrow, beforeBorrow + awaitMs);
        long left = System.currentTimeMillis() - beforeBorrow;
        log.debug("borrow channel[{}] cost {}ms", channel.id().asLongText(), left);
        DefaultFuture defaultFuture = DefaultFuture.newFuture(left < awaitMs ? awaitMs - left : awaitMs, channel, request);
        ChannelFuture future = channel.writeAndFlush(request);
        future.addListener( t -> {
            if(t.isSuccess()) {
                DefaultFuture.sent(request);
            }
        });
        if(future.cause() != null) {
            returnChannel(channel, awaitMs - (System.currentTimeMillis() - beforeBorrow));
            throw new RuntimeException(future.cause());
        }
        T result = null;
        try {
            result = (T) defaultFuture.get(awaitMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("elapse {}ms", System.currentTimeMillis() - beforeBorrow, e);
        }
        returnChannel(channel, awaitMs - (System.currentTimeMillis() - beforeBorrow));
        return result;
    }

    private Channel borrowChannel(long effective, long invalid) {
        Channel channel;
        try {
            if(channels.isEmpty()) {
                invalid = invalid + DEFAULT_CONNECT_TIMEOUT;
                connect();
            }
            channel = channels.poll(invalid - effective, TimeUnit.MILLISECONDS);
            if(channel != null && !channel.isActive()) {
                channels.remove(channel);
                return borrowChannel(effective, invalid);
            }
            return channel;
        } catch (InterruptedException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void returnChannel(Channel channel, long waitTime) {
        if(!channel.isActive()) {
            channels.remove(channel);
        } else {
            try {
                channels.offer(channel, waitTime, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static class DefaultFuture extends CompletableFuture<BaoStockResponse> {

        private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

        public static final Timer TIME_OUT_TIMER = new Timer();

        private TimerTask task;

        private volatile long sent;

        private final String msgType;

        private DefaultFuture(BaoStockRequest request) {
            this.msgType = request.getRequestCode();
            FUTURES.put(request.currentId(), this);
        }

        public static DefaultFuture newFuture(long timeout, Channel channel, BaoStockRequest request) {
            DefaultFuture future = new DefaultFuture(request);
            future.task = new TimeoutCheckTask(request.currentId());
            TIME_OUT_TIMER.schedule(future.task, timeout, 30);
            return future;
        }

        public static DefaultFuture getFuture(long id) {
            return FUTURES.get(id);
        }

        public static void sent(BaoStockRequest request) {
            DefaultFuture future = FUTURES.get(request.currentId());
            if(future != null) {
                future.sent = System.currentTimeMillis();
            }
        }

        public static void received(BaoStockResponse response) {
            received(response, false);
        }

        public static void received(BaoStockResponse response, boolean timeout) {
            DefaultFuture futureResult = FUTURES.remove(response.id);
            if (futureResult != null) {
                if (!timeout && futureResult.task != null) {
                    futureResult.task.cancel();
                }
                futureResult.doReceived(response);
            }
        }

        private void doReceived(BaoStockResponse response) {
            if(Constants.BSERR_RECVSOCK_TIMEOUT.equals(response.getErrorCode())
                    || Constants.BSERR_SENDSOCK_TIMEOUT.equals(response.getErrorCode())
                    || Constants.BSERR_SYSTEM_ERROR.equals(response.getErrorCode())) {
                completeExceptionally(new RuntimeException(response.getErrorMsg()));
            }
            complete(response);
        }

    }

    private static class TimeoutCheckTask extends TimerTask {

        private final Long id;

        TimeoutCheckTask(Long id) {
            this.id = id;
        }

        @Override
        public void run() {
            DefaultFuture future = DefaultFuture.getFuture(id);
            if (future == null || future.isDone()) {
                return;
            }
            String errorCode = future.sent > 0 ? Constants.BSERR_RECVSOCK_TIMEOUT : Constants.BSERR_SENDSOCK_TIMEOUT;
            String errorMessage = future.sent > 0 ? SERVER_TIME_OUT : CLIENT_TIME_OUT;
            String[] headArray = {Constants.BAOSTOCK_CLIENT_VERSION, requestCodeToResponseCode(future.msgType), String.valueOf(errorMessage.length())};
            String[] bodyArray = {errorCode, errorMessage};
            BaoStockResponse result = ResultFactory.newInstance(headArray, bodyArray);
            result.id = id;
            DefaultFuture.received(result, true);
        }
    }

    public final static class BaoStockMessageToByteEncoder extends MessageToByteEncoder<BaoStockRequest> {

        private static final int PADDING_LENGTH = 10;

        @Override
        protected void encode(ChannelHandlerContext ctx, BaoStockRequest request, ByteBuf byteBuf) throws Exception {
            ctx.channel().attr(AttributeKey.valueOf("id")).set(request.getId());
            ctx.channel().attr(AttributeKey.valueOf("type")).set(request.getRequestCode());
            String body = request.encode();
            String[] array = {Constants.BAOSTOCK_CLIENT_VERSION, request.getRequestCode(), zerofill(body.length(), PADDING_LENGTH)};
            String msgHeader = StringUtils.arrayToDelimitedString(array, Constants.MESSAGE_SPLIT);
            String msgHeaderBody = msgHeader + body;
            long crc32 = crc32(msgHeaderBody.getBytes(StandardCharsets.UTF_8));
            String msg = msgHeaderBody + Constants.MESSAGE_SPLIT + crc32 + "\n";
            byteBuf.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        }

    }

    private static String requestCodeToResponseCode(String requestCode) {
        int requestType;
        try {
            requestType = Integer.parseInt(requestCode);
            return zerofill(requestType + 1, 2);
        } catch (Exception ignore) {
            log.error("internal error, request '{}' cannot convert to response code as rule: responseCode=requestCode+1", requestCode);
        }
        return null;
    }

    private static String zerofill(int num, int length) {
        String number = String.valueOf(num);
        if(number.length() >= length) {
            return number;
        }
        StringBuilder sb = new StringBuilder(length);
        for(int i = number.length(); i < length; i++) {
            sb.append(0);
        }
        sb.append(number);
        return sb.toString();
    }

    public final static class BaoStockByteToMessageDecoder extends ByteToMessageDecoder {

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List out) throws Exception {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(byteBuf.readableBytes());
            byteBuf.readBytes(baos, byteBuf.readableBytes());
            byte[] raw = baos.toByteArray();
            String msgHeader = new String(raw, 0, Constants.MESSAGE_HEADER_LENGTH, StandardCharsets.UTF_8);
            String[] headerArray = msgHeader.split(Constants.MESSAGE_SPLIT);
            byte[] bodyRaw = raw;
            int offset = Constants.MESSAGE_HEADER_LENGTH;
            int length = raw.length - Constants.MESSAGE_HEADER_LENGTH;
            if(Constants.COMPRESSED_MESSAGE_TYPE_TUPLE.contains(headerArray[1])) {
                int headInnerLength = Integer.parseInt(headerArray[2]);
                byte[] dst = new byte[headInnerLength];
                System.arraycopy(raw, Constants.MESSAGE_HEADER_LENGTH, dst, 0, headInnerLength);
                bodyRaw = decompress(dst);
                offset = 0;
                length = bodyRaw.length;
            }
            String msgBody = new String(bodyRaw, offset, length, StandardCharsets.UTF_8);
            String[] bodyArray = msgBody.split(Constants.MESSAGE_SPLIT);
            if(Constants.MESSAGE_TYPE_EXCEPTIONS.equals(headerArray[1])) {
                log.info("replace type from '{}' to '{}' avoid class cast exception", headerArray[1], ctx.channel().attr(AttributeKey.valueOf("type")).get());
                headerArray[1] = requestCodeToResponseCode((String) ctx.channel().attr(AttributeKey.valueOf("type")).get());
            }
            BaoStockResponse result = ResultFactory.newInstance(headerArray, bodyArray);
            result.id = (Long) ctx.channel().attr(AttributeKey.valueOf("id")).get();
            out.add(result);
            DefaultFuture.received(result);
        }

    }

    static byte[] decompress(byte[] data) {
        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buffer = new byte[8192];
            while (!decompresser.finished()) {
                int index = decompresser.inflate(buffer);
                baos.write(buffer, 0, index);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                baos.close();
            } catch (Exception swallow) {

            }
        }
        decompresser.end();
        return baos.toByteArray();
    }

    static long crc32(byte[] data) {
        CRC32 compress = new CRC32();
        compress.reset();
        compress.update(data);
        return compress.getValue();
    }
}
