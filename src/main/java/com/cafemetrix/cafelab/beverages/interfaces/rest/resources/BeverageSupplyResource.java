package com.cafemetrix.cafelab.beverages.interfaces.rest.resources;

import java.math.BigDecimal;

public record BeverageSupplyResource(String name, BigDecimal quantity, String unit, BigDecimal unitCost) {
}
