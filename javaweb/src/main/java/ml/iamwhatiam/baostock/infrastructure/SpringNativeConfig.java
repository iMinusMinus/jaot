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
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.ReferenceCountUtil;
import ml.iamwhatiam.baostock.infrastructure.dao.StockRepositoryImpl;
import ml.iamwhatiam.baostock.infrastructure.rpc.BaoStockResponse;
import ml.iamwhatiam.baostock.infrastructure.rpc.NettyClient;
import ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusResponse;
import ml.iamwhatiam.baostock.infrastructure.web.StockVO;
import oracle.ucp.jdbc.PoolDataSource;
import oracle.ucp.jdbc.PoolDataSourceImpl;
import org.fusesource.jansi.WindowsAnsiOutputStream;
import org.fusesource.jansi.internal.Kernel32;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeResourcesEntry;
import org.springframework.nativex.AotOptions;
import org.springframework.nativex.hint.AotProxyHint;
import org.springframework.nativex.hint.FieldHint;
import org.springframework.nativex.hint.MethodHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.SerializationHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.nativex.type.NativeConfiguration;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
@NativeHint(trigger = PoolDataSource.class, types = {
        @TypeHint(types = {
                PoolDataSource.class,
                PoolDataSourceImpl.class,
        }, access = {TypeAccess.DECLARED_METHODS, TypeAccess.PUBLIC_METHODS, TypeAccess.DECLARED_FIELDS})
})
// @see org.springframework.transaction.annotation.TransactionalNativeConfigurationProcessor
@AotProxyHint(targetClass = StockRepositoryImpl.class)
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
        }, access = {TypeAccess.QUERY_DECLARED_METHODS}),
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
        @TypeHint(types = {
                PooledByteBufAllocator.class,
        })}, options = {"-Dbaostock.socket.readTimeout"})
// mybatis. mapper file conversion should change to p/k/g/MapperInterface.xml, and mybatis-spring-native would automatically add mapper xml file as resource file.
//@MyBatisResourcesScan(mapperLocationPatterns = "mapper/**/*Mapper.xml")
// web
@NativeHint(trigger = ResponseBodyAdvice.class, serializables = {
        @SerializationHint(types = {
                Integer.class,
                String.class,
                LocalDate.class,
                LocalDateTime.class,
                BigDecimal.class,
                StockVO.class,
                BaoStockResponse.class,
                QueryHistoryKDataPlusResponse.class,
        }, typeNames = { // nested types must be specific using a $ separator
                "ml.iamwhatiam.baostock.infrastructure.web.WrapResponseBodyAdvice$Wrapper",
                "ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusResponse$Quotation",
                "ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusResponse$Frequency"
        })}, types = {
        @TypeHint(typeNames = {
                "ml.iamwhatiam.baostock.infrastructure.rpc.QueryHistoryKDataPlusResponse$Quotation",
        }, access = {TypeAccess.DECLARED_METHODS, TypeAccess.PUBLIC_METHODS})
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
                // h2-console support
                if(ClassUtils.isPresent("org.h2.Driver", null)) {
                        registry.resources().add(NativeResourcesEntry.of("org/h2/util/data.zip"));
                }
                // h2 jdk8 datetime support
                if(ClassUtils.isPresent("org.h2.util.LocalDateTimeUtils", null)) {
                        Method ofNanoOfDay = ReflectionUtils.findMethod(LocalTime.class, "ofNanoOfDay", long.class);
                        Method toNanoOfDay = ReflectionUtils.findMethod(LocalTime.class, "toNanoOfDay");
                        Method parse = ReflectionUtils.findMethod(LocalTime.class, "parse", CharSequence.class);
                        registry.reflection().forType(LocalTime.class).withQueriedExecutables(ofNanoOfDay).withExecutables(ofNanoOfDay);
                        registry.reflection().forType(LocalTime.class).withQueriedExecutables(toNanoOfDay).withExecutables(toNanoOfDay);
                        registry.reflection().forType(LocalTime.class).withQueriedExecutables(parse).withExecutables(parse);

                        registry.reflection().forType(LocalDate.class).withAccess(TypeAccess.QUERY_DECLARED_METHODS, TypeAccess.DECLARED_METHODS);

                        Method getEpochSecond = ReflectionUtils.findMethod(Instant.class, "getEpochSecond");
                        Method getNano = ReflectionUtils.findMethod(Instant.class, "getNano");
                        registry.reflection().forType(Instant.class).withQueriedExecutables(getEpochSecond).withExecutables(getEpochSecond);
                        registry.reflection().forType(Instant.class).withQueriedExecutables(getNano).withExecutables(getNano);

                        Method plusNanos = ReflectionUtils.findMethod(LocalDateTime.class, "plusNanos", long.class);
                        Method toLocalDate = ReflectionUtils.findMethod(LocalDateTime.class, "toLocalDate");
                        Method toLocalTime = ReflectionUtils.findMethod(LocalDateTime.class, "toLocalTime");
                        Method parseOfDateTime = ReflectionUtils.findMethod(LocalDateTime.class, "parse", CharSequence.class);
                        registry.reflection().forType(LocalDateTime.class).withQueriedExecutables(plusNanos).withExecutables(plusNanos);
                        registry.reflection().forType(LocalDateTime.class).withQueriedExecutables(toLocalDate).withExecutables(toLocalDate);
                        registry.reflection().forType(LocalDateTime.class).withQueriedExecutables(toLocalTime).withExecutables(toLocalTime);
                        registry.reflection().forType(LocalDateTime.class).withQueriedExecutables(parseOfDateTime).withExecutables(parseOfDateTime);

                        Method ofTotalSeconds = ReflectionUtils.findMethod(ZoneOffset.class, "ofTotalSeconds", int.class);
                        Method getTotalSeconds = ReflectionUtils.findMethod(ZoneOffset.class, "getTotalSeconds");
                        registry.reflection().forType(ZoneOffset.class).withQueriedExecutables(ofTotalSeconds).withExecutables(ofTotalSeconds);
                        registry.reflection().forType(ZoneOffset.class).withQueriedExecutables(getTotalSeconds).withExecutables(getTotalSeconds);

                        Method toLocalDateTime = ReflectionUtils.findMethod(OffsetDateTime.class, "toLocalDateTime");
                        Method getOffset = ReflectionUtils.findMethod(OffsetDateTime.class, "getOffset");
                        Method of = ReflectionUtils.findMethod(OffsetDateTime.class, "of", LocalDateTime.class, ZoneOffset.class);
                        Method parseOffsetDateTime = ReflectionUtils.findMethod(OffsetDateTime.class, "parse", CharSequence.class);
                        registry.reflection().forType(OffsetDateTime.class).withQueriedExecutables(toLocalDateTime).withExecutables(toLocalDateTime);
                        registry.reflection().forType(OffsetDateTime.class).withQueriedExecutables(getOffset).withExecutables(getOffset);
                        registry.reflection().forType(OffsetDateTime.class).withQueriedExecutables(of).withExecutables(of);
                        registry.reflection().forType(OffsetDateTime.class).withQueriedExecutables(parseOffsetDateTime).withExecutables(parseOffsetDateTime);

                        Method toInstant = ReflectionUtils.findMethod(Timestamp.class, "toInstant");
                        registry.reflection().forType(Timestamp.class).withQueriedExecutables(toInstant).withExecutables(toInstant);
                }
        }
}
