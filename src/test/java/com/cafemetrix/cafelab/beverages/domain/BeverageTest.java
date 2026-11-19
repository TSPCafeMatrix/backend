package com.cafemetrix.cafelab.beverages.domain;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import com.cafemetrix.cafelab.beverages.domain.model.commands.CreateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.commands.UpdateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.valueobjects.BeverageSupply;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * US14 - Registro de bebidas: reglas de negocio del aggregate Beverage.
 */
class BeverageTest {

    private static BeverageSupply supply(String name, String qty, String unit, String unitCost) {
        return new BeverageSupply(name, new BigDecimal(qty), unit, new BigDecimal(unitCost));
    }

    private static CreateBeverageCommand latte(String price, List<BeverageSupply> supplies) {
        return new CreateBeverageCommand(1L, "Latte", "Caliente", "Espresso con leche", new BigDecimal(price), null, supplies);
    }

    private static final List<BeverageSupply> LATTE_SUPPLIES = List.of(
            supply("Café molido", "18", "g", "0.10"),   // 1.80
            supply("Leche entera", "200", "ml", "0.005"), // 1.00
            supply("Vaso 12 oz", "1", "unidad", "0.50")); // 0.50

    @Test
    @DisplayName("Calcula el costo total sumando cantidad x costo unitario de cada insumo")
    void totalCost_sumsSupplyCosts() {
        var beverage = new Beverage(latte("12.00", LATTE_SUPPLIES));
        assertEquals(new BigDecimal("3.30"), beverage.getTotalCost());
    }

    @Test
    @DisplayName("Calcula margen y porcentaje de margen sobre el precio de venta")
    void margin_isPriceMinusCost() {
        var beverage = new Beverage(latte("12.00", LATTE_SUPPLIES));
        assertEquals(new BigDecimal("8.70"), beverage.getMargin());
        assertEquals(new BigDecimal("72.5"), beverage.getMarginPercentage());
    }

    @Test
    @DisplayName("Margen negativo cuando el costo supera el precio")
    void margin_canBeNegative() {
        var beverage = new Beverage(latte("3.00", LATTE_SUPPLIES));
        assertEquals(new BigDecimal("-0.30"), beverage.getMargin());
        assertEquals(new BigDecimal("-10.0"), beverage.getMarginPercentage());
    }

    @Test
    @DisplayName("Rechaza bebida sin insumos")
    void rejectsEmptySupplies() {
        var ex = assertThrows(IllegalArgumentException.class, () -> new Beverage(latte("12.00", List.of())));
        assertEquals("La bebida debe tener al menos un insumo", ex.getMessage());
    }

    @Test
    @DisplayName("Rechaza precio de venta cero o negativo")
    void rejectsNonPositivePrice() {
        assertThrows(IllegalArgumentException.class, () -> new Beverage(latte("0", LATTE_SUPPLIES)));
        assertThrows(IllegalArgumentException.class, () -> new Beverage(latte("-1", LATTE_SUPPLIES)));
    }

    @Test
    @DisplayName("Rechaza nombre o categoría vacíos")
    void rejectsBlankNameOrCategory() {
        assertThrows(IllegalArgumentException.class, () -> new Beverage(
                new CreateBeverageCommand(1L, " ", "Caliente", null, BigDecimal.TEN, null, LATTE_SUPPLIES)));
        assertThrows(IllegalArgumentException.class, () -> new Beverage(
                new CreateBeverageCommand(1L, "Latte", "", null, BigDecimal.TEN, null, LATTE_SUPPLIES)));
    }

    @Test
    @DisplayName("Insumo con cantidad cero o costo negativo es inválido")
    void supplyValidation() {
        assertThrows(IllegalArgumentException.class, () -> supply("Café", "0", "g", "0.10"));
        assertThrows(IllegalArgumentException.class, () -> supply("Café", "18", "g", "-0.10"));
        assertThrows(IllegalArgumentException.class, () -> supply(" ", "18", "g", "0.10"));
        assertThrows(IllegalArgumentException.class, () -> supply("Café", "18", " ", "0.10"));
    }

    @Test
    @DisplayName("Actualizar reemplaza datos e insumos y recalcula el costo")
    void update_replacesSuppliesAndRecalculates() {
        var beverage = new Beverage(latte("12.00", LATTE_SUPPLIES));
        beverage.update(new UpdateBeverageCommand(10L, 1L, "Latte avena", "Caliente", null, new BigDecimal("14.00"), null,
                List.of(supply("Café molido", "18", "g", "0.10"), supply("Bebida de avena", "200", "ml", "0.012"))));

        assertEquals("Latte avena", beverage.getName());
        assertEquals(2, beverage.getSupplies().size());
        assertEquals(new BigDecimal("4.20"), beverage.getTotalCost());
        assertEquals(new BigDecimal("9.80"), beverage.getMargin());
    }
}
