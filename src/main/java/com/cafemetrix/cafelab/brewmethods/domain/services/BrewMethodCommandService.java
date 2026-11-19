package com.cafemetrix.cafelab.brewmethods.domain.services;

import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.CreateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.DeleteBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.UpdateBrewMethodCommand;

import java.util.Optional;

public interface BrewMethodCommandService {
    BrewMethod handle(CreateBrewMethodCommand command);

    Optional<BrewMethod> handle(UpdateBrewMethodCommand command);

    boolean handle(DeleteBrewMethodCommand command);
}
