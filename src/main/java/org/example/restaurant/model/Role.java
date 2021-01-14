package org.example.restaurant.model;

import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "roles", schema = "public")
@NoArgsConstructor
public class Role extends AbstractNamedEntity {
}