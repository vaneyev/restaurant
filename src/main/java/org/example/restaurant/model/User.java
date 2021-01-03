package org.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users", schema = "public")
@Getter
@Setter
@NoArgsConstructor
public class User extends AbstractNamedEntity {
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotNull
    private boolean isAdmin;

    public User(Long id, String name, Boolean isAdmin) {
        super.setId(id);
        super.setName(name);
        this.isAdmin = isAdmin;
    }
}
