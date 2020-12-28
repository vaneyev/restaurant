package org.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "dishes", schema = "public")
public class Dish extends AbstractNamedEntity{
    @ManyToOne
    @JoinColumn(name = "menu_id")
    @JsonBackReference
    @NotNull
    private Menu menu;
    @Min(0)
    @NotNull
    private Integer price;

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
