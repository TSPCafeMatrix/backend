package com.cafemetrix.cafelab.brewmethods.interfaces.rest;

import com.cafemetrix.cafelab.brewmethods.domain.exceptions.BrewMethodNotFoundException;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import com.cafemetrix.cafelab.shared.interfaces.rest.support.CafeLabScopedExceptionHandlerSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.cafemetrix.cafelab.brewmethods.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BrewMethodsExceptionHandler extends CafeLabScopedExceptionHandlerSupport {

    @ExceptionHandler(BrewMethodNotFoundException.class)
    public ResponseEntity<MessageResource> handleNotFound(BrewMethodNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResource(ex.getMessage()));
    }
}
