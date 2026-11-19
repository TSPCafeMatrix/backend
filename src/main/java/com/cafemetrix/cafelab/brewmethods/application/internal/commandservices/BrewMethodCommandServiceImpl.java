package com.cafemetrix.cafelab.brewmethods.application.internal.commandservices;

import com.cafemetrix.cafelab.brewmethods.application.internal.outboundservices.acl.ExternalGrindCalibrationService;
import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.CreateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.DeleteBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.model.commands.UpdateBrewMethodCommand;
import com.cafemetrix.cafelab.brewmethods.domain.services.BrewMethodCommandService;
import com.cafemetrix.cafelab.brewmethods.infrastructure.persistence.jpa.repositories.BrewMethodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class BrewMethodCommandServiceImpl implements BrewMethodCommandService {
    private final BrewMethodRepository repository;
    private final ExternalGrindCalibrationService calibrations;

    public BrewMethodCommandServiceImpl(BrewMethodRepository repository, ExternalGrindCalibrationService calibrations) {
        this.repository = repository;
        this.calibrations = calibrations;
    }

    private void ensureCalibrationBelongsToUser(Long calibrationId, Long userId) {
        if (calibrationId != null && !calibrations.belongsToUser(calibrationId, userId)) {
            throw new IllegalArgumentException("La calibración de molienda indicada no existe");
        }
    }

    @Override
    @Transactional
    public BrewMethod handle(CreateBrewMethodCommand command) {
        ensureCalibrationBelongsToUser(command.grindCalibrationId(), command.userId());
        return repository.save(new BrewMethod(command));
    }

    @Override
    @Transactional
    public Optional<BrewMethod> handle(UpdateBrewMethodCommand command) {
        var existing = repository.findByIdAndUserId(command.brewMethodId(), command.userId());
        if (existing.isEmpty()) {
            return Optional.empty();
        }
        ensureCalibrationBelongsToUser(command.grindCalibrationId(), command.userId());
        return Optional.of(repository.save(existing.get().update(command)));
    }

    @Override
    @Transactional
    public boolean handle(DeleteBrewMethodCommand command) {
        return repository.findByIdAndUserId(command.brewMethodId(), command.userId())
                .map(method -> {
                    method.softDelete();
                    repository.save(method);
                    return true;
                })
                .orElse(false);
    }
}
