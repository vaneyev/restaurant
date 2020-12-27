package org.example.restaurant.model;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "restaurants", schema = "public")
public class Restaurant extends AbstractNamedEntity{
}
