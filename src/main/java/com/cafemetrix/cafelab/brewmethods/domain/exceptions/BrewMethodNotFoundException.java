package com.cafemetrix.cafelab.brewmethods.domain.exceptions;

public class BrewMethodNotFoundException extends RuntimeException {
    public BrewMethodNotFoundException(Long brewMethodId) {
        super("Método de preparación no encontrado");
    }
}
