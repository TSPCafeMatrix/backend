package com.cafemetrix.cafelab.beverages.interfaces.rest.transform;

import com.cafemetrix.cafelab.beverages.domain.model.commands.CreateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.commands.UpdateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.valueobjects.BeverageSupply;
import com.cafemetrix.cafelab.beverages.interfaces.rest.resources.SaveBeverageResource;

import java.util.List;

public class BeverageCommandFromResourceAssembler {

    public static CreateBeverageCommand toCreateCommand(Long userId, SaveBeverageResource r) {
        return new CreateBeverageCommand(
                userId, r.name(), r.category(), r.description(), r.salePrice(), r.recipeId(), toSupplies(r));
    }

    public static UpdateBeverageCommand toUpdateCommand(Long beverageId, Long userId, SaveBeverageResource r) {
        return new UpdateBeverageCommand(
                beverageId, userId, r.name(), r.category(), r.description(), r.salePrice(), r.recipeId(), toSupplies(r));
    }

    private static List<BeverageSupply> toSupplies(SaveBeverageResource r) {
        if (r.supplies() == null) {
            return List.of();
        }
        return r.supplies().stream()
                .map(s -> new BeverageSupply(s.name(), s.quantity(), s.unit(), s.unitCost()))
                .toList();
    }
}
