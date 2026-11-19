package com.cafemetrix.cafelab.brewmethods.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.brewmethods.domain.model.aggregates.BrewMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrewMethodRepository extends JpaRepository<BrewMethod, Long> {
    List<BrewMethod> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<BrewMethod> findByIdAndUserId(Long id, Long userId);
}
