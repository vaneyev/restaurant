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
@Table(name = "dishes", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Dish extends AbstractNamedEntity {
    @ManyToOne
    @JoinColumn(name = "menu_id")
    @JsonBackReference
    @NotNull
    private Menu menu;
    @Min(0)
    @NotNull
    private Integer price;

    public Dish(Long id, Menu menu, String name, Integer price) {
        this.setId(id);
        this.menu = menu;
        this.setName(name);
        this.price = price;
    }
}
