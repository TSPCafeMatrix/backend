package com.cafemetrix.cafelab.brewmethods.interfaces.rest.transform;

import com.cafemetrix.cafelab.brewmethods.domain.model.commands.CreateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.UpdateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.interfaces.rest.resources.SaveBrewMethodResource;

public class BrewMethodCommandFromResourceAssembler {

    public static CreateBrewMethodCommand toCreateCommand(Long userId, SaveBrewMethodResource r) {
        return new CreateBrewMethodCommand(userId, r.name(), r.method(), r.equipment(), r.doseGrams(), r.waterMl(),
                r.waterTemperatureC(), r.brewTimeSeconds(), r.grindCalibrationId(), r.notes());
    }

    public static UpdateBrewMethodCommand toUpdateCommand(Long brewMethodId, Long userId, SaveBrewMethodResource r) {
        return new UpdateBrewMethodCommand(brewMethodId, userId, r.name(), r.method(), r.equipment(), r.doseGrams(),
                r.waterMl(), r.waterTemperatureC(), r.brewTimeSeconds(), r.grindCalibrationId(), r.notes());
    }
}
