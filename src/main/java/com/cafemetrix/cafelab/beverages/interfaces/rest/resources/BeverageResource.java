package com.cafemetrix.cafelab.beverages.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

public record BeverageResource(
        Long id,
        Long userId,
        String name,
        String category,
        String description,
        BigDecimal salePrice,
        Long recipeId,
        List<BeverageSupplyResource> supplies,
        BigDecimal totalCost,
        BigDecimal margin,
        BigDecimal marginPercentage) {
}
