package com.cafemetrix.cafelab.beverages.interfaces.rest.transform;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import com.cafemetrix.cafelab.beverages.interfaces.rest.resources.BeverageResource;
import com.cafemetrix.cafelab.beverages.interfaces.rest.resources.BeverageSupplyResource;

public class BeverageResourceFromEntityAssembler {

    public static BeverageResource toResourceFromEntity(Beverage e) {
        var supplies = e.getSupplies().stream()
                .map(s -> new BeverageSupplyResource(s.getName(), s.getQuantity(), s.getUnit(), s.getUnitCost()))
                .toList();
        return new BeverageResource(
                e.getId(),
                e.getUserId(),
                e.getName(),
                e.getCategory(),
                e.getDescription(),
                e.getSalePrice(),
                e.getRecipeId(),
                supplies,
                e.getTotalCost(),
                e.getMargin(),
                e.getMarginPercentage());
    }
}
