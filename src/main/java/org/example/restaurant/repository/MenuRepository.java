package org.example.restaurant.repository;

import org.example.restaurant.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    List<Menu> findByDate(LocalDate date);

    Optional<Menu> findFirstByRestaurantIdAndDate(Long restaurantId, LocalDate date);
}
