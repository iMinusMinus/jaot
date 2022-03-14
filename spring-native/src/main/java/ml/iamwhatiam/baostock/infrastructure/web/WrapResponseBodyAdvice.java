package ml.iamwhatiam.baostock.infrastructure.web;

import lombok.Getter;
import lombok.ToString;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.io.Serializable;

@ControllerAdvice
public class WrapResponseBodyAdvice implements ResponseBodyAdvice {

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
        return new Wrapper(-1, t.getMessage(), null);
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
