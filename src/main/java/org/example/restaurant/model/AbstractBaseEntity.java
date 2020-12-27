package org.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
