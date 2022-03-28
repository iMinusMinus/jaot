package ml.iamwhatiam.baostock.infrastructure.web;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.Serializable;

@ControllerAdvice
@Slf4j
public class WrapResponseBodyAdvice implements ResponseBodyAdvice {

    @Value("${" + AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME +":}")
    private String activeProfiles;

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                             Class selectedConverterType,
                             ServerHttpRequest request, ServerHttpResponse response) {
        if(!MediaType.APPLICATION_JSON.includes(selectedContentType) && !MediaType.APPLICATION_XML.includes(selectedContentType)) {
            return body;
        }
        if(body instanceof Wrapper) {
            return body;
        }
        return new Wrapper(0, body);
    }

    @ExceptionHandler
    @ResponseBody
    public Wrapper handleUnknownException(Throwable t) {
        log.error(t.getMessage(), t);
        return activeProfiles.contains("prod") ?
                new Wrapper(-1, "server error", null) :
                new Wrapper(-1, t.getMessage(), t);
    }

    @Getter
    @ToString
    public static class Wrapper implements Serializable {

        private final int code;

        private final String message;

        private final Object data;

        public Wrapper(int code, Object data) {
            this(code, null, data);
        }

        public Wrapper(int code, String message ,Object data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
    }
}
