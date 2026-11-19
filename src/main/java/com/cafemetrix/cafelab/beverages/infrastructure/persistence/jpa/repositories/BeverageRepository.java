package com.cafemetrix.cafelab.beverages.infrastructure.persistence.jpa.repositories;

import com.cafemetrix.cafelab.beverages.domain.model.aggregates.Beverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeverageRepository extends JpaRepository<Beverage, Long> {
    List<Beverage> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Beverage> findByIdAndUserId(Long id, Long userId);
}
