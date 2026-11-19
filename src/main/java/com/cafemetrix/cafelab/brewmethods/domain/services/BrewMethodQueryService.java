package com.cafemetrix.cafelab.brewmethods.domain.services;

import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import com.cafemetrix.cafelab.brewmethods.domain.model.queries.GetBrewMethodByIdForUserQuery;
import com.cafemetrix.cafelab.brewmethods.domain.model.queries.GetBrewMethodsByUserIdQuery;

import java.util.List;
import java.util.Optional;

public interface BrewMethodQueryService {
    List<BrewMethod> handle(GetBrewMethodsByUserIdQuery query);

    Optional<BrewMethod> handle(GetBrewMethodByIdForUserQuery query);
}
