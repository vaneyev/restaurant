package org.example.restaurant.repository;

import org.example.restaurant.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

public interface VoteRepository extends JpaRepository<Vote, Long> {
    Long countByRestaurantIdAndDate(@NotNull Long restaurantId, @NotNull LocalDate date);
}
