package com.cafemetrix.cafelab.beverages.domain.exceptions;

public class BeverageNotFoundException extends RuntimeException {
    public BeverageNotFoundException(Long beverageId) {
        super("Bebida no encontrada");
    }
}
