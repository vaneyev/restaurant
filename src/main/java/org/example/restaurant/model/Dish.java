package org.example.restaurant.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "dishes", schema = "public")
@NoArgsConstructor
public class Dish extends AbstractNamedEntity{

    public Dish(Long id, String name) {
        this.setId(id);
        this.setName(name);
    }
}
