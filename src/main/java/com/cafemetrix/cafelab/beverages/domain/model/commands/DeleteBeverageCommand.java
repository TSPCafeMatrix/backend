package com.cafemetrix.cafelab.beverages.domain.model.commands;

public record DeleteBeverageCommand(Long beverageId, Long userId) {
}
