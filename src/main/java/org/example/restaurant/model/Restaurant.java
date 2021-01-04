package org.example.restaurant.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "restaurants", schema = "public")
@NoArgsConstructor
public class Restaurant extends AbstractNamedEntity {
    public Restaurant(Long id, String name) {
        this.setId(id);
        this.setName(name);
    }
}
