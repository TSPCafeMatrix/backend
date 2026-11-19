package com.cafemetrix.cafelab.brewmethods.domain;

import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.CreateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.UpdateBrewMethodCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * US13 - Registro de métodos de preparación: reglas de negocio del aggregate BrewMethod.
 */
class BrewMethodTest {

    private static CreateBrewMethodCommand v60(String dose, String water, Integer temperature, Integer seconds) {
        return new CreateBrewMethodCommand(1L, "V60 de la casa", "v60", "Hario V60 02",
                dose == null ? null : new BigDecimal(dose),
                water == null ? null : new BigDecimal(water),
                temperature, seconds, null, "Bloom de 30 s con 45 ml");
    }

    @Test
    @DisplayName("Calcula el ratio café:agua a partir de dosis y agua")
    void ratio_isWaterOverDose() {
        var method = new BrewMethod(v60("15", "250", 93, 180));
        assertEquals(new BigDecimal("16.7"), method.getRatio());
        assertEquals("1:16.7", method.getRatioLabel());
    }

    @Test
    @DisplayName("Normaliza el tipo de método y guarda los parámetros")
    void storesParameters() {
        var method = new BrewMethod(new CreateBrewMethodCommand(1L, " Espresso clásico ", "ESPRESSO", null,
                new BigDecimal("18"), new BigDecimal("36"), 92, 28, 5L, null));
        assertEquals("Espresso clásico", method.getName());
        assertEquals("espresso", method.getMethod());
        assertEquals("1:2.0", method.getRatioLabel());
        assertEquals(5L, method.getGrindCalibrationId());
        assertNull(method.getEquipment());
    }

    @Test
    @DisplayName("Rechaza un tipo de método que no está en el catálogo")
    void rejectsUnknownMethod() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new BrewMethod(new CreateBrewMethodCommand(
                1L, "Raro", "microondas", null, BigDecimal.TEN, new BigDecimal("200"), 90, 60, null, null)));
        assertEquals("Método de preparación no válido: microondas", ex.getMessage());
    }

    @Test
    @DisplayName("El agua debe superar la dosis de café")
    void waterMustExceedDose() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new BrewMethod(v60("20", "20", 93, 180)));
        assertEquals("La cantidad de agua debe ser mayor que la dosis de café", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza dosis, agua o tiempo no positivos")
    void rejectsNonPositiveValues() {
        assertThrows(IllegalArgumentException.class, () -> new BrewMethod(v60("0", "250", 93, 180)));
        assertThrows(IllegalArgumentException.class, () -> new BrewMethod(v60("15", null, 93, 180)));
        assertThrows(IllegalArgumentException.class, () -> new BrewMethod(v60("15", "250", 93, 0)));
    }

    @Test
    @DisplayName("La temperatura del agua debe estar entre 0 y 100 °C (cold brew usa agua fría)")
    void temperatureRange() {
        assertThrows(IllegalArgumentException.class, () -> new BrewMethod(v60("15", "250", 101, 180)));
        assertThrows(IllegalArgumentException.class, () -> new BrewMethod(v60("15", "250", -1, 180)));
        assertDoesNotThrow(() -> new BrewMethod(v60("15", "250", 4, 180)));
    }

    @Test
    @DisplayName("Actualizar reemplaza parámetros y recalcula el ratio")
    void update_recalculatesRatio() {
        var method = new BrewMethod(v60("15", "250", 93, 180));
        method.update(new UpdateBrewMethodCommand(9L, 1L, "V60 intenso", "v60", "Hario V60 02",
                new BigDecimal("20"), new BigDecimal("300"), 94, 210, 3L, null));
        assertEquals("V60 intenso", method.getName());
        assertEquals("1:15.0", method.getRatioLabel());
        assertEquals(3L, method.getGrindCalibrationId());
        assertNull(method.getNotes());
    }
}
