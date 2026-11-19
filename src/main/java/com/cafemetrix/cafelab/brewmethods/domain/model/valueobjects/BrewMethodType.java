package com.cafemetrix.cafelab.brewmethods.domain.model.valueobjects;

import java.util.Arrays;
import java.util.Locale;

/** Catálogo de métodos de extracción soportados (mismos valores que usan las recetas). */
public enum BrewMethodType {
    ESPRESSO("espresso"),
    POUR_OVER("pour-over"),
    FRENCH_PRESS("french-press"),
    COLD_BREW("cold-brew"),
    AEROPRESS("aeropress"),
    CHEMEX("chemex"),
    V60("v60"),
    CLEVER("clever");

    private final String value;

    BrewMethodType(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static BrewMethodType from(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("El tipo de método es obligatorio");
        }
        var normalized = text.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        return Arrays.stream(values())
                .filter(t -> t.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Método de preparación no válido: " + text.trim()));
    }
}
