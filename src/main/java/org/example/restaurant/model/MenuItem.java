package org.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "menu_items", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class MenuItem extends AbstractBaseEntity {
    @ManyToOne
    @JoinColumn(name = "menu_id")
    @JsonBackReference
    @NotNull
    private Menu menu;
    @ManyToOne
    @JoinColumn(name = "dish_id")
    @NotNull
    private Dish dish;
    @Min(0)
    @NotNull
    private Integer price;

    public MenuItem(Long id, Menu menu, Dish dish, Integer price) {
        this.setId(id);
        this.menu = menu;
        this.dish = dish;
        this.price = price;
    }
}
