package org.example.restaurant.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "votes", schema = "public")
public class Vote extends AbstractBaseEntity{
    private Long userId;
    private Long restaurantId;
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