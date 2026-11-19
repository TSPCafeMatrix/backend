package com.cafemetrix.cafelab.brewmethods.application.internal.queryservices;

import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import com.cafemetrix.cafelab.brewmethods.domain.model.queries.GetBrewMethodByIdForUserQuery;
import com.cafemetrix.cafelab.brewmethods.domain.model.queries.GetBrewMethodsByUserIdQuery;
import com.cafemetrix.cafelab.brewmethods.domain.services.BrewMethodQueryService;
import com.cafemetrix.cafelab.brewmethods.infrastructure.persistence.jpa.repositories.BrewMethodRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BrewMethodQueryServiceImpl implements BrewMethodQueryService {
    private final BrewMethodRepository repository;

    public BrewMethodQueryServiceImpl(BrewMethodRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<BrewMethod> handle(GetBrewMethodsByUserIdQuery query) {
        return repository.findByUserIdOrderByCreatedAtDesc(query.userId());
    }

    @Override
    public Optional<BrewMethod> handle(GetBrewMethodByIdForUserQuery query) {
        return repository.findByIdAndUserId(query.brewMethodId(), query.userId());
    }
}
