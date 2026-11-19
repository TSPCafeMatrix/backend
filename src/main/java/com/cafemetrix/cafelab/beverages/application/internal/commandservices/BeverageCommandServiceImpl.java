package com.cafemetrix.cafelab.beverages.application.internal.commandservices;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import com.cafemetrix.cafelab.beverages.domain.model.commands.CreateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.commands.DeleteBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.model.commands.UpdateBeverageCommand;
import com.cafemetrix.cafelab.beverages.domain.services.BeverageCommandService;
import com.cafemetrix.cafelab.beverages.infrastructure.persistence.jpa.repositories.BeverageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BeverageCommandServiceImpl implements BeverageCommandService {
    private final BeverageRepository repository;

    public BeverageCommandServiceImpl(BeverageRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public Beverage handle(CreateBeverageCommand command) {
        return repository.save(new Beverage(command));
    }

    @Override
    @Transactional
    public Optional<Beverage> handle(UpdateBeverageCommand command) {
        return repository.findByIdAndUserId(command.beverageId(), command.userId())
                .map(beverage -> repository.save(beverage.update(command)));
    }

    @Override
    @Transactional
    public boolean handle(DeleteBeverageCommand command) {
        return repository.findByIdAndUserId(command.beverageId(), command.userId())
                .map(beverage -> {
                    beverage.softDelete();
                    repository.save(beverage);
                    return true;
                })
                .orElse(false);
    }
}
