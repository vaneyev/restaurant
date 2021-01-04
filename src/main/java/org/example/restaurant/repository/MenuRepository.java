package org.example.restaurant.repository;

import org.example.restaurant.model.Menu;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu, Long> {
    @EntityGraph(attributePaths = {"dishes", "restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select m from Menu m where m.date=:date")
    List<Menu> findByDate(LocalDate date);

    @EntityGraph(attributePaths = {"dishes", "restaurant"}, type = EntityGraph.EntityGraphType.LOAD)
    @Query("select m from Menu m where m.id=:id")
    Optional<Menu> getById(Long id);

    Optional<Menu> findFirstByRestaurantIdAndDate(Long restaurantId, LocalDate date);
}
