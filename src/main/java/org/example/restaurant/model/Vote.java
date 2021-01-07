package org.example.restaurant.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "votes", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class Vote extends AbstractBaseEntity {
    @NotNull
    private Long userId;
    @NotNull
    private Long restaurantId;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull
    private LocalDate date;

    public Vote(Long userId, LocalDate date) {
        this.userId = userId;
        this.date = date;
    }

    public Vote(Long id, Long userId, Long restaurantId, LocalDate date) {
        this.setId(id);
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.date = date;
    }
}
