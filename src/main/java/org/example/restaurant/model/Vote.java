package org.example.restaurant.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "votes", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vote extends AbstractBaseEntity {
    @NotNull
    private Long userId;
    @NotNull
    private Long restaurantId;
    @NotNull
    private LocalDate date;
}
