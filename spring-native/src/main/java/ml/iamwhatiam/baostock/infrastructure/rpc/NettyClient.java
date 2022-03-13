package ml.iamwhatiam.baostock.infrastructure.rpc;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Inflater;

@Slf4j
public class NettyClient {

    private final static boolean epoll;

    private final static String SERVER_TIME_OUT = "server timeout";

    private final static String CLIENT_TIME_OUT = "client timeout";

    private final static String UNKNOWN_ERROR = "unknown error";

    private final static long DEFAULT_CONNECT_TIMEOUT = 3000L;

    private final static long DEFAULT_WAIT_TIME = 1000L;

    private final static String END_OF_RESPONSE = "<![CDATA[]]>\n";

    private final String host;

    private final int port;

    private final boolean secure;

    private Bootstrap bootstrap;

    private Channel channel;

    static {
        epoll = System.getProperty("os.name").contains("linux") && Epoll.isAvailable();
    }

    public NettyClient(String host, int port, boolean secure) {
        this.host = host;
        this.port = port;
        this.secure = secure;
        init();
        try {
            if(channel == null || !channel.isActive()) {
                connect();
            }
        } catch (Exception e){
            if (channel != null) {
                channel.close();
            }
        }
    }

    private void init() {
        EventLoopGroup eventExecutors = epoll ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        Class<? extends Channel> klazz = epoll ? EpollSocketChannel.class : NioSocketChannel.class;
        bootstrap = new Bootstrap();
        ByteBuf delimiter = Unpooled.copiedBuffer(END_OF_RESPONSE, StandardCharsets.UTF_8);
        bootstrap.group(eventExecutors)
                .channel(klazz)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast("stickDecoder", new DelimiterBasedFrameDecoder(Integer.MAX_VALUE, delimiter)) // TODO
                                .addLast("decoder", new BaoStockByteToMessageDecoder())
                                .addLast("encoder", new BaoStockMessageToByteEncoder());
                    }
                });
    }

    private void connect() {
        connect(host, port, secure);
    }

    private void connect(String host, int port, boolean secure) {
        ChannelFuture channelFuture = bootstrap.connect(host, port);
//        boolean connected = channelFuture.awaitUninterruptibly(DEFAULT_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS);
//        if(!connected || !channelFuture.isSuccess()) {
//            throw new RuntimeException("connect fail", channelFuture.cause());
//        }
        Channel newChannel = channelFuture.syncUninterruptibly().channel();
        if(channel != null) {
            channel.close();
        }
        channel = newChannel;
    }

    public <T extends BaoStockResponse> T request(BaoStockRequest msg) {
        return request(msg, DEFAULT_WAIT_TIME);
    }

    public <T extends BaoStockResponse> T request(BaoStockRequest request, long awaitMs) {
        DefaultFuture defaultFuture = DefaultFuture.newFuture(channel, request);
        long beforeWrite = System.currentTimeMillis();
        ChannelFuture future = channel.writeAndFlush(request);
        future.addListener( t -> {
            if(t.isSuccess()) {
                DefaultFuture.sent(request);
            }
        });
        if(future.cause() != null) {
            throw new RuntimeException(future.cause());
        }
        try {
            return (T) defaultFuture.get(awaitMs, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("elapse {}ms", System.currentTimeMillis() - beforeWrite, e);
        }
        return null;
    }

    public static class DefaultFuture extends CompletableFuture<BaoStockResponse> {

        private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<>();

        private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

        public static final Timer TIME_OUT_TIMER = new Timer();

        private final Long id;

        private final Channel channel;

        private final BaoStockRequest request;

        private TimerTask task;

        private volatile long sent;

        private DefaultFuture(long id, Channel channel, BaoStockRequest request) {
            this.id = id;
            this.channel = channel;
            this.request = request;
            FUTURES.put(id, this);
            CHANNELS.put(id, channel);
        }

        public static DefaultFuture newFuture(Channel channel, BaoStockRequest request) {
            DefaultFuture future = new DefaultFuture(request.currentId(), channel, request);
//            future.task = new TimeoutCheckTask(request.getId());
//            TIME_OUT_TIMER.schedule(future.task, 1015L, 30L);
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
            try {
                DefaultFuture futureResult = FUTURES.remove(response.id);
                if(futureResult != null) {
                    if(!timeout && futureResult.task != null) {
                        futureResult.task.cancel();
                    }
                    futureResult.doReceived(response);
                }
            } finally {
                CHANNELS.remove(response.id);
            }
        }

        private void doReceived(BaoStockResponse response) {
            if(SERVER_TIME_OUT.equals(response.getErrorCode())
                    || CLIENT_TIME_OUT.equals(response.getErrorCode())
                    || UNKNOWN_ERROR.equals(response.getErrorCode())) {
                completeExceptionally(new RuntimeException(response.getErrorCode()));
            }
            complete(response);
        }

    }

    private static class TimeoutCheckTask extends TimerTask {

        private final List<Long> idsToCheck = new LinkedList<>();

        TimeoutCheckTask(Long requestID) {
            idsToCheck.add(requestID);
        }

        @Override
        public void run() {
            Iterator<Long> it = idsToCheck.iterator();
            while (it.hasNext()) {
                Long requestID = it.next();
                DefaultFuture future = DefaultFuture.getFuture(requestID);
                if (future == null || future.isDone()) {
                    continue;
                }
                String errorCode = future.sent > 0 ? SERVER_TIME_OUT : CLIENT_TIME_OUT;
                String[] headArray = {Constants.BAOSTOCK_CLIENT_VERSION, null, String.valueOf(errorCode.length())};
                String[] bodyArray = {errorCode, null};
                BaoStockResponse result = new BaoStockResponse(headArray, bodyArray);
                result.id = requestID;
                DefaultFuture.received(result, true);
                it.remove();
            }
        }
    }

    public final static class BaoStockMessageToByteEncoder extends MessageToByteEncoder<BaoStockRequest> {

        private static final int PADDING_LENGTH = 10;

        private static final char ZERO = '0';

        @Override
        protected void encode(ChannelHandlerContext ctx, BaoStockRequest request, ByteBuf byteBuf) throws Exception {
            ctx.channel().attr(AttributeKey.valueOf("id")).set(request.getId());
            String body = request.encode();
            String[] array = {Constants.BAOSTOCK_CLIENT_VERSION, request.getRequestCode(), zerofill(body.length(), PADDING_LENGTH)};
            String msgHeader = StringUtils.arrayToDelimitedString(array, Constants.MESSAGE_SPLIT);
            String msgHeaderBody = msgHeader + body;
            long crc32 = crc32(msgHeaderBody.getBytes(StandardCharsets.UTF_8));
            String msg = msgHeaderBody + Constants.MESSAGE_SPLIT + crc32 + "\n";
            byteBuf.writeBytes(msg.getBytes(StandardCharsets.UTF_8));
        }

        private String zerofill(int num, int length) {
            String number = String.valueOf(num);
            if(number.length() >= length) {
                return number;
            }
            StringBuilder sb = new StringBuilder(length);
            for(int i = number.length(); i < length; i++) {
                sb.append(ZERO);
            }
            sb.append(number);
            return sb.toString();
        }
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
                copy(raw, Constants.MESSAGE_HEADER_LENGTH, headInnerLength, dst, 0);
                bodyRaw = decompress(dst);
                offset = 0;
                length = bodyRaw.length;
            }
            String msgBody = new String(bodyRaw, offset, length, StandardCharsets.UTF_8);
            String[] bodyArray = msgBody.split(Constants.MESSAGE_SPLIT);
            BaoStockResponse result = ResultFactory.newInstance(headerArray, bodyArray);
            result.id = (Long) ctx.channel().attr(AttributeKey.valueOf("id")).get();
            out.add(result);
            DefaultFuture.received(result);
        }

        private void copy(byte[] src, int offset, int length, byte[] dst, int from) {
            for(int index = 0; index < length; index++) {
                dst[from + index] = src[offset + index];
            }
        }
    }

    static byte[] decompress(byte[] data) {
        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buffer = new byte[1024];
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
        compress.update(data);
        return compress.getValue();
    }
}
