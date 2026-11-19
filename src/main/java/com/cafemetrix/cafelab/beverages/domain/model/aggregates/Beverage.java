package com.cafemetrix.cafelab.beverages.domain.model.aggregates;

import com.cafemetrix.cafelab.beverages.domain.model.commands.CreateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.commands.UpdateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.valueobjects.BeverageSupply;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * US14 - Bebida de la carta: insumos que la componen y precio de venta.
 * Calcula costo total y margen a partir de sus insumos.
 */
@Entity
@Table(name = "beverages")
@SQLRestriction("deleted_at IS NULL")
public class Beverage extends AuditableAbstractAggregateRoot<Beverage> {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    @Getter
    @Column(name = "profile_id", nullable = false)
    private Long userId;

    @Getter
    @Column(nullable = false, length = 100)
    private String name;

    @Getter
    @Column(nullable = false, length = 50)
    private String category;

    @Getter
    @Column(columnDefinition = "TEXT")
    private String description;

    @Getter
    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Getter
    @Column(name = "recipe_id")
    private Long recipeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "beverage_supplies", joinColumns = @JoinColumn(name = "beverage_id"))
    @OrderColumn(name = "position")
    private List<BeverageSupply> supplies = new ArrayList<>();

    protected Beverage() {}

    public Beverage(CreateBeverageCommand c) {
        this.userId = c.userId();
        apply(c.name(), c.category(), c.description(), c.salePrice(), c.recipeId(), c.supplies());
    }

    public Beverage update(UpdateBeverageCommand c) {
        apply(c.name(), c.category(), c.description(), c.salePrice(), c.recipeId(), c.supplies());
        return this;
    }

    private void apply(String name, String category, String description, BigDecimal salePrice,
                       Long recipeId, List<BeverageSupply> supplies) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre de la bebida es obligatorio");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre de la bebida no puede exceder 100 caracteres");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("La categoría de la bebida es obligatoria");
        }
        if (salePrice == null || salePrice.signum() <= 0) {
            throw new IllegalArgumentException("El precio de venta debe ser mayor a 0");
        }
        if (supplies == null || supplies.isEmpty()) {
            throw new IllegalArgumentException("La bebida debe tener al menos un insumo");
        }
        this.name = name.trim();
        this.category = category.trim();
        this.description = description == null || description.isBlank() ? null : description.trim();
        this.salePrice = salePrice.setScale(2, RoundingMode.HALF_UP);
        this.recipeId = recipeId;
        this.supplies.clear();
        this.supplies.addAll(supplies);
    }

    public List<BeverageSupply> getSupplies() {
        return Collections.unmodifiableList(supplies);
    }

    /** Suma del costo de todos los insumos. */
    public BigDecimal getTotalCost() {
        return supplies.stream()
                .map(BeverageSupply::cost)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    /** Ganancia bruta por unidad vendida: precio de venta - costo total. */
    public BigDecimal getMargin() {
        return salePrice.subtract(getTotalCost()).setScale(2, RoundingMode.HALF_UP);
    }

    /** Margen como porcentaje del precio de venta, con un decimal. */
    public BigDecimal getMarginPercentage() {
        return getMargin().multiply(HUNDRED).divide(salePrice, 1, RoundingMode.HALF_UP);
    }
}
