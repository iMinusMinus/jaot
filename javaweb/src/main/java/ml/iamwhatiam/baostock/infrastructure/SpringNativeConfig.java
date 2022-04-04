package ml.iamwhatiam.baostock.infrastructure;

import ch.qos.logback.classic.AsyncAppender;
import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.pattern.ThrowableProxyConverter;
import ch.qos.logback.classic.pattern.color.HighlightingCompositeConverter;
import ch.qos.logback.core.hook.DelayingShutdownHook;
import ch.qos.logback.core.pattern.color.CyanCompositeConverter;
import ch.qos.logback.core.pattern.color.GrayCompositeConverter;
import ch.qos.logback.core.pattern.color.MagentaCompositeConverter;
import io.netty.buffer.AbstractByteBufAllocator;
import io.netty.buffer.AbstractReferenceCountedByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.NettyRuntime;
import io.netty.util.ReferenceCountUtil;
import ml.iamwhatiam.baostock.infrastructure.rpc.NettyClient;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusResponse;
import ml.iamwhatiam.baostock.infrastructure.web.StockVO;
import ml.iamwhatiam.baostock.infrastructure.web.WrapResponseBodyAdvice;
import org.fusesource.jansi.WindowsAnsiOutputStream;
import org.fusesource.jansi.internal.Kernel32;
import org.mybatis.spring.nativex.MyBatisResourcesScan;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.nativex.AotOptions;
import org.springframework.nativex.hint.FieldHint;
import org.springframework.nativex.hint.MethodHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Locale;

// use logback-spring style and delete above, refer org.springframework.boot.logging.LogbackHints
@NativeHint(trigger = Level.class, types = {
        @TypeHint(types = {
                GrayCompositeConverter.class,
                CyanCompositeConverter.class,
                HighlightingCompositeConverter.class,
                MagentaCompositeConverter.class,
                ThrowableProxyConverter.class
        }, access = {}, methods = @MethodHint(name = "<init>")),
        @TypeHint(types = {
                AsyncAppender.class,
                DelayingShutdownHook.class
        }, access = {TypeAccess.PUBLIC_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS}),
})
// @see org.springframework.transaction.annotation.TransactionalNativeConfigurationProcessor
//@AotProxyHint(targetClass = StockRepositoryImpl.class, interfaces = {AopInfrastructureBean.class}, proxyFeatures = ProxyBits.IS_STATIC)
// netty, refer io.rsocket.RSocketHints or io.lettuce.LettuceHints
@NativeHint(trigger = NettyClient.class, types = {
        @TypeHint(typeNames = "io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueColdProducerFields",
                fields = @FieldHint(name = "producerLimit", allowUnsafeAccess = true)),
        @TypeHint(typeNames = "io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueProducerFields",
                fields = @FieldHint(name = "producerIndex", allowUnsafeAccess = true)),
        @TypeHint(typeNames = "io.netty.util.internal.shaded.org.jctools.queues.BaseMpscLinkedArrayQueueConsumerFields",
                fields = @FieldHint(name = "consumerIndex", allowUnsafeAccess = true)),
        @TypeHint(typeNames = "io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueConsumerIndexField",
                fields = @FieldHint(name = "consumerIndex", allowUnsafeAccess = true)),
        @TypeHint(typeNames = "io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueProducerIndexField",
                fields = @FieldHint(name = "producerIndex", allowUnsafeAccess = true)),
        @TypeHint(typeNames = "io.netty.util.internal.shaded.org.jctools.queues.MpscArrayQueueProducerLimitField",
                fields = @FieldHint(name = "producerLimit", allowUnsafeAccess = true)),
        @TypeHint(typeNames = {
                "io.netty.channel.DefaultChannelPipeline$HeadContext",
                "io.netty.channel.DefaultChannelPipeline$TailContext",
//                "io.netty.channel.ChannelHandlerMask$Skip"
        }, access = {TypeAccess.QUERY_DECLARED_METHODS}),
//        @TypeHint(typeNames = "io.netty.channel.ChannelHandler$Sharable", access = TypeAccess.QUERY_DECLARED_METHODS),
        @TypeHint(types = ChannelHandlerAdapter.class,
                queriedMethods = @MethodHint(name = "exceptionCaught", parameterTypes = {ChannelHandlerContext.class, Throwable.class})),
        @TypeHint(types = {
                AbstractByteBufAllocator.class,
                ChannelInboundHandlerAdapter.class,
                ChannelInitializer.class,
                ChannelOutboundHandlerAdapter.class,
                ByteToMessageDecoder.class,
                MessageToByteEncoder.class,
                ReferenceCountUtil.class
        }, access = TypeAccess.QUERY_DECLARED_METHODS),
//        @TypeHint(types = AbstractReferenceCountedByteBuf.class, fields = @FieldHint(name = "refCnt")),
        @TypeHint(types = {
                PooledByteBufAllocator.class,
//                DelimiterBasedFrameDecoder.class,
//                NettyRuntime.class,
//                Unpooled.class
        })})
// mybatis. mapper file conversion should change to p/k/g/MapperInterface.xml, and mybatis-spring-native would automatically add mapper xml file as resource file.
@MyBatisResourcesScan(typeAliasesPackages = "ml.iamwhatiam.baostock.infrastructure.dao", mapperLocationPatterns = "mapper/**/*Mapper.xml")
// web
@NativeHint(trigger = ResponseBodyAdvice.class, serializables = {
        @SerializationHint(types = {
                WrapResponseBodyAdvice.Wrapper.class,
                StockVO.class,
                QueryHistoryKDataPlusResponse.class,
                StackTraceElement.class,
                StackTraceElement[].class
        })
})
public class SpringNativeConfig implements NativeConfiguration {

        @Override
        public void computeHints(NativeConfigurationRegistry registry, AotOptions aotOptions) {
                if(System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("win") &&
                        ClassUtils.isPresent("org.fusesource.jansi.WindowsAnsiOutputStream", null)) {
                        NativeConfigurationRegistry.ReflectionConfiguration reflectionConfiguration = registry.reflection();
                        reflectionConfiguration.forType(WindowsAnsiOutputStream.class).withAccess(TypeAccess.DECLARED_CONSTRUCTORS);
                        NativeConfigurationRegistry.ReflectionConfiguration jniConfiguration = registry.jni();
                        jniConfiguration.forType(Kernel32.class).withAccess(TypeAccess.DECLARED_FIELDS);
                        jniConfiguration.forType(Kernel32.CONSOLE_SCREEN_BUFFER_INFO.class).withAccess(TypeAccess.DECLARED_FIELDS);
                        jniConfiguration.forType(Kernel32.COORD.class).withAccess(TypeAccess.DECLARED_FIELDS);
                        jniConfiguration.forType(Kernel32.SMALL_RECT.class).withAccess(TypeAccess.DECLARED_FIELDS);
                }
        }
}
