package com.veracore.opensourcescannerapi.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;


@RestControllerAdvice
public class ControllerExceptionHandler extends ResponseEntityExceptionHandler {

    // TODO: BETTER EXCEPTION HANDLING
    // Outdated: https://github.com/Ahmed3lmallah/Cloud-Native-Game-Store-API/blob/master/Retail-API/src/main/java/com/company/RetailAPI/controller/ControllerExceptionHandler.java
    // https://www.javadevjournal.com/spring/exception-handling-for-rest-with-spring/
    // https://www.baeldung.com/global-error-handler-in-a-spring-rest-api

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        BindingResult result = ex.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors().stream()
                .map(f -> new FieldError(f.getObjectName(), f.getField(), f.getCode()))
                .collect(Collectors.toList());

        return handleExceptionInternal(ex, "Method argument not valid, fieldErrors:" + fieldErrors ,new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

}
