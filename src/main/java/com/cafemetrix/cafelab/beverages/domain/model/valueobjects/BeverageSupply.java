package com.cafemetrix.cafelab.beverages.domain.model.valueobjects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Insumo utilizado en una bebida (café, leche, vaso, jarabe...).
 * Value object: no tiene identidad propia, vive dentro del aggregate Beverage.
 */
@Embeddable
public class BeverageSupply {

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name = "unit", nullable = false, length = 20)
    private String unit;

    @Column(name = "unit_cost", nullable = false, precision = 12, scale = 4)
    private BigDecimal unitCost;

    protected BeverageSupply() {}

    public BeverageSupply(String name, BigDecimal quantity, String unit, BigDecimal unitCost) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del insumo es obligatorio");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del insumo no puede exceder 100 caracteres");
        }
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad del insumo debe ser mayor a 0");
        }
        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("La unidad del insumo es obligatoria");
        }
        if (unitCost == null || unitCost.signum() < 0) {
            throw new IllegalArgumentException("El costo unitario del insumo no puede ser negativo");
        }
        this.name = name.trim();
        this.quantity = quantity;
        this.unit = unit.trim();
        this.unitCost = unitCost;
    }

    /** Costo del insumo dentro de la bebida: cantidad x costo unitario. */
    public BigDecimal cost() {
        return quantity.multiply(unitCost).setScale(2, RoundingMode.HALF_UP);
    }

    public String getName() { return name; }

    public BigDecimal getQuantity() { return quantity; }

    public String getUnit() { return unit; }

    public BigDecimal getUnitCost() { return unitCost; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BeverageSupply that)) return false;
        return name.equals(that.name)
                && quantity.compareTo(that.quantity) == 0
                && unit.equals(that.unit)
                && unitCost.compareTo(that.unitCost) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity.stripTrailingZeros(), unit, unitCost.stripTrailingZeros());
    }
}
