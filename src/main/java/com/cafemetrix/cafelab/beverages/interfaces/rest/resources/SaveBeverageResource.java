package com.cafemetrix.cafelab.beverages.interfaces.rest.resources;

import java.math.BigDecimal;
import java.util.List;

/** Cuerpo para crear o actualizar una bebida. Las reglas de negocio se validan en el dominio. */
public record SaveBeverageResource(
        String name,
        String category,
        String description,
        BigDecimal salePrice,
        Long recipeId,
        List<BeverageSupplyResource> supplies) {
}
