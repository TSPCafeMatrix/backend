package com.cafemetrix.cafelab.beverages.application.internal.queryservices;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import com.cafemetrix.cafelab.beverages.domain.model.queries.GetBeverageByIdForUserQuery;
import com.cafemetrix.cafelab.beverages.domain.model.queries.GetBeveragesByUserIdQuery;
import com.cafemetrix.cafelab.beverages.domain.services.BeverageQueryService;
import com.cafemetrix.cafelab.beverages.infrastructure.persistence.jpa.repositories.BeverageRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeverageQueryServiceImpl implements BeverageQueryService {
    private final BeverageRepository repository;

    public BeverageQueryServiceImpl(BeverageRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Beverage> handle(GetBeveragesByUserIdQuery query) {
        return repository.findByUserIdOrderByCreatedAtDesc(query.userId());
    }

    @Override
    public Optional<Beverage> handle(GetBeverageByIdForUserQuery query) {
        return repository.findByIdAndUserId(query.beverageId(), query.userId());
    }
}
