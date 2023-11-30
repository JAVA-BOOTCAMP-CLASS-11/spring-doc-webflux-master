/*
 * Copyright (c) 2022. Dandelion tutorials
 */

package com.klindziuk.flux.springdoc.api.handler.error;

import com.klindziuk.flux.springdoc.exception.EntityMappingException;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.error.ErrorAttributeOptions.Include;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ErrorHandler extends AbstractErrorWebExceptionHandler {

  public ErrorHandler(DefaultErrorAttributes defaultErrorAttributes, ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(defaultErrorAttributes, new WebProperties.Resources(), applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {

    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

    final Map<String, Object> errorPropertiesMap = getErrorAttributes(request,
        ErrorAttributeOptions.of(Include.EXCEPTION, Include.MESSAGE));

    final HttpStatus status = getStatus(errorPropertiesMap);
    errorPropertiesMap.put("status", status.value());
    errorPropertiesMap.put("error", status.getReasonPhrase());

    return ServerResponse
        .status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(errorPropertiesMap));
  }

  private HttpStatus getStatus(Map<String, Object> errorPropertiesMap){
    String exception = String.valueOf(errorPropertiesMap.get("exception"));
    if(NumberFormatException.class.getName().equalsIgnoreCase(exception)){
      return HttpStatus.BAD_REQUEST;
    }
    if(EntityMappingException.class.getName().equalsIgnoreCase(exception)){
      return HttpStatus.NOT_FOUND;
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
