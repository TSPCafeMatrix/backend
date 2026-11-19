package com.cafemetrix.cafelab.brewmethods.interfaces.rest.resources;

import java.math.BigDecimal;

public record BrewMethodResource(
        Long id,
        Long userId,
        String name,
        String method,
        String equipment,
        BigDecimal doseGrams,
        BigDecimal waterMl,
        Integer waterTemperatureC,
        Integer brewTimeSeconds,
        Long grindCalibrationId,
        String notes,
        BigDecimal ratio,
        String ratioLabel) {
}
