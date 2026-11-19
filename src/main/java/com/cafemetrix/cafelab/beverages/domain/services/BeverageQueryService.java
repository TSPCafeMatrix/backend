package com.cafemetrix.cafelab.beverages.domain.services;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import com.cafemetrix.cafelab.beverages.domain.model.queries.GetBeverageByIdForUserQuery;
import com.cafemetrix.cafelab.beverages.domain.model.queries.GetBeveragesByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface BeverageQueryService {
    List<Beverage> handle(GetBeveragesByUserIdQuery query);

    Optional<Beverage> handle(GetBeverageByIdForUserQuery query);
}
