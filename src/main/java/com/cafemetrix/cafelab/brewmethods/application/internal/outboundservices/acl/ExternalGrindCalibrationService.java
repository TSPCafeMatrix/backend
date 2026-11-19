package com.cafemetrix.cafelab.brewmethods.application.internal.outboundservices.acl;

import com.cafemetrix.cafelab.calibrations.domain.model.queries.GetGrindCalibrationByIdForUserQuery;
import com.cafemetrix.cafelab.calibrations.domain.services.GrindCalibrationQueryService;
import org.springframework.stereotype.Service;

/**
 * Anti-corruption layer hacia el bounded context de calibraciones (US12).
 * Es el único punto de brewmethods que conoce a calibrations.
 */
@Service
public class ExternalGrindCalibrationService {
    private final GrindCalibrationQueryService grindCalibrationQueryService;

    public ExternalGrindCalibrationService(GrindCalibrationQueryService grindCalibrationQueryService) {
        this.grindCalibrationQueryService = grindCalibrationQueryService;
    }

    /** true si la calibración existe y pertenece al perfil indicado. */
    public boolean belongsToUser(Long calibrationId, Long userId) {
        return grindCalibrationQueryService
                .handle(new GetGrindCalibrationByIdForUserQuery(calibrationId, userId))
                .isPresent();
    }
}
