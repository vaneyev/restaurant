package org.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "menus", schema = "public")
public class Menu {
    @Id
    private Long id;

    private Long restaurantId;

    private LocalDate date;

    @OneToMany(mappedBy = "menu")
    @JsonManagedReference
    private List<Dish> dishes;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
