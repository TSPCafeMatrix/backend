package com.cafemetrix.cafelab.brewmethods.interfaces.rest.transform;

import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import com.cafemetrix.cafelab.brewmethods.interfaces.rest.resources.BrewMethodResource;

public class BrewMethodResourceFromEntityAssembler {

    public static BrewMethodResource toResourceFromEntity(BrewMethod e) {
        return new BrewMethodResource(
                e.getId(),
                e.getUserId(),
                e.getName(),
                e.getMethod(),
                e.getEquipment(),
                e.getDoseGrams(),
                e.getWaterMl(),
                e.getWaterTemperatureC(),
                e.getBrewTimeSeconds(),
                e.getGrindCalibrationId(),
                e.getNotes(),
                e.getRatio(),
                e.getRatioLabel());
    }
}
