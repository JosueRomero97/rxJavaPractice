package com.mitocode.springreactore.exception;

import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Order(-1)
//@Order(Ordered.HIGHEST_PRECEDENCE)
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    public WebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
        super(errorAttributes, resources, applicationContext);
        setMessageWriters(configurer.getWriters());//permite sobreescribir a la respuesta del http.
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    Mono<ServerResponse> renderErrorResponse(ServerRequest request){
        Map<String, Object> generalError = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        Map<String, Object> customError = new HashMap<>();

        int statusCode = (int) generalError.get("status");
        //int statusCode = Integer.valueOf(String.valueOf(generalError.get("status")));
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Throwable error = getError(request);

        switch (statusCode){
            case 400,422 ->{
                customError.put("msg",error.getMessage());
                customError.put("status",400);
                httpStatus = HttpStatus.BAD_REQUEST;
            }
            case 404 ->{
                customError.put("msg",error.getMessage());
                customError.put("status",404);
                httpStatus = HttpStatus.NOT_FOUND;
            }
            case 500 ->{
                customError.put("msg",error.getMessage());
                customError.put("status",500);
                //httpStatus = HttpStatus.BAD_REQUEST;
            }
            default -> {
                customError.put("msg",error.getMessage());
                customError.put("status",418);
                httpStatus = HttpStatus.I_AM_A_TEAPOT;
            }
        }

        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(customError));
    }

}
