package org.example.restaurant.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "votes", schema = "public")
public class Vote extends AbstractBaseEntity{
    @NotNull
    private Long userId;
    @NotNull
    private Long restaurantId;
    @NotNull
    private LocalDate date;

    public Vote(Long userId, Long restaurantId, LocalDate date) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.date = date;
    }

    public Vote() {

    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
}
