package com.cafemetrix.cafelab.brewmethods.domain.model.commands;

import java.math.BigDecimal;

public record UpdateBrewMethodCommand(
        Long brewMethodId,
        Long userId,
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
