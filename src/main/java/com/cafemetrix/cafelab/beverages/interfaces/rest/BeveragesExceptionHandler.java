package com.cafemetrix.cafelab.beverages.interfaces.rest;

import com.cafemetrix.cafelab.beverages.domain.exceptions.BeverageNotFoundException;
import com.cafemetrix.cafelab.shared.interfaces.rest.resources.MessageResource;
import com.cafemetrix.cafelab.shared.interfaces.rest.support.CafeLabScopedExceptionHandlerSupport;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.cafemetrix.cafelab.beverages.interfaces.rest")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BeveragesExceptionHandler extends CafeLabScopedExceptionHandlerSupport {

    @ExceptionHandler(BeverageNotFoundException.class)
    public ResponseEntity<MessageResource> handleNotFound(BeverageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new MessageResource(ex.getMessage()));
    }

}
