package com.cafemetrix.cafelab.brewmethods.domain.model.aggregates;

import com.cafemetrix.cafelab.brewmethods.domain.model.commands.CreateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.UpdateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.valueobjects.BrewMethodType;
import com.cafemetrix.cafelab.shared.domain.model.aggregates.AuditableAbstractAggregateRoot;
import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * US13 - Método de preparación estándar del barista: parámetros reutilizables de extracción
 * (dosis, agua, temperatura, tiempo) y, opcionalmente, la calibración de molienda a usar (US12).
 */
@Entity
@Table(name = "brew_methods")
@SQLRestriction("deleted_at IS NULL")
public class BrewMethod extends AuditableAbstractAggregateRoot<BrewMethod> {

    @Getter
    @Column(name = "profile_id", nullable = false)
    private Long userId;

    @Getter
    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "method_type", nullable = false, length = 20)
    private BrewMethodType methodType;

    @Getter
    @Column(length = 100)
    private String equipment;

    @Getter
    @Column(name = "dose_grams", nullable = false, precision = 8, scale = 2)
    private BigDecimal doseGrams;

    @Getter
    @Column(name = "water_ml", nullable = false, precision = 8, scale = 2)
    private BigDecimal waterMl;

    @Getter
    @Column(name = "water_temperature_c", nullable = false)
    private Integer waterTemperatureC;

    @Getter
    @Column(name = "brew_time_seconds", nullable = false)
    private Integer brewTimeSeconds;

    @Getter
    @Column(name = "grind_calibration_id")
    private Long grindCalibrationId;

    @Getter
    @Column(columnDefinition = "TEXT")
    private String notes;

    protected BrewMethod() {}

    public BrewMethod(CreateBrewMethodCommand c) {
        this.userId = c.userId();
        apply(c.name(), c.method(), c.equipment(), c.doseGrams(), c.waterMl(),
                c.waterTemperatureC(), c.brewTimeSeconds(), c.grindCalibrationId(), c.notes());
    }

    public BrewMethod update(UpdateBrewMethodCommand c) {
        apply(c.name(), c.method(), c.equipment(), c.doseGrams(), c.waterMl(),
                c.waterTemperatureC(), c.brewTimeSeconds(), c.grindCalibrationId(), c.notes());
        return this;
    }

    private void apply(String name, String method, String equipment, BigDecimal doseGrams, BigDecimal waterMl,
                       Integer waterTemperatureC, Integer brewTimeSeconds, Long grindCalibrationId, String notes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("El nombre del método es obligatorio");
        }
        if (name.trim().length() > 100) {
            throw new IllegalArgumentException("El nombre del método no puede exceder 100 caracteres");
        }
        var type = BrewMethodType.from(method);
        if (doseGrams == null || doseGrams.signum() <= 0) {
            throw new IllegalArgumentException("La dosis de café debe ser mayor a 0");
        }
        if (waterMl == null || waterMl.signum() <= 0) {
            throw new IllegalArgumentException("La cantidad de agua debe ser mayor a 0");
        }
        if (waterMl.compareTo(doseGrams) <= 0) {
            throw new IllegalArgumentException("La cantidad de agua debe ser mayor que la dosis de café");
        }
        if (waterTemperatureC == null || waterTemperatureC < 0 || waterTemperatureC > 100) {
            throw new IllegalArgumentException("La temperatura del agua debe estar entre 0 y 100 °C");
        }
        if (brewTimeSeconds == null || brewTimeSeconds <= 0) {
            throw new IllegalArgumentException("El tiempo de extracción debe ser mayor a 0 segundos");
        }
        this.name = name.trim();
        this.methodType = type;
        this.equipment = blankToNull(equipment);
        this.doseGrams = doseGrams;
        this.waterMl = waterMl;
        this.waterTemperatureC = waterTemperatureC;
        this.brewTimeSeconds = brewTimeSeconds;
        this.grindCalibrationId = grindCalibrationId;
        this.notes = blankToNull(notes);
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    /** Valor del catálogo (ej. "v60"). */
    public String getMethod() {
        return methodType.value();
    }

    /** Partes de agua por cada parte de café, con un decimal. */
    public BigDecimal getRatio() {
        return waterMl.divide(doseGrams, 1, RoundingMode.HALF_UP);
    }

    /** Ratio en notación de barista, ej. "1:16.7". */
    public String getRatioLabel() {
        return "1:" + getRatio().toPlainString();
    }
}
