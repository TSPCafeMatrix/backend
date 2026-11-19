package com.cafemetrix.cafelab.brewmethods.interfaces.rest.resources;

import java.math.BigDecimal;

/** Cuerpo para crear o actualizar un método. Las reglas de negocio se validan en el dominio. */
public record SaveBrewMethodResource(
        String name,
        String method,
        String equipment,
        BigDecimal doseGrams,
        BigDecimal waterMl,
        Integer waterTemperatureC,
        Integer brewTimeSeconds,
        Long grindCalibrationId,
        String notes) {
}
