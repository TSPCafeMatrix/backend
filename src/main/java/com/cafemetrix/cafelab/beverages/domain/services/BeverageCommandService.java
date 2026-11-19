package com.cafemetrix.cafelab.beverages.domain.services;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import com.cafemetrix.cafelab.beverages.domain.model.commands.CreateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.commands.DeleteBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.commands.UpdateBeverageCommand;

import java.util.Optional;

public interface BeverageCommandService {
    Beverage handle(CreateBeverageCommand command);

    Optional<Beverage> handle(UpdateBeverageCommand command);

    boolean handle(DeleteBeverageCommand command);
}
