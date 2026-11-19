package com.cafemetrix.cafelab.beverages.domain.model.commands;

import com.cafemetrix.cafelab.beverages.domain.model.valueobjects.BeverageSupply;

import java.math.BigDecimal;
import java.util.List;

public record UpdateBeverageCommand(
        Long beverageId,
        Long userId,
        String name,
        String category,
        String description,
        BigDecimal salePrice,
        Long recipeId,
        List<BeverageSupply> supplies) {
}
