package org.example.restaurant.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "users", schema = "public")
public class User extends AbstractNamedEntity{
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @NotNull
    private Boolean isAdmin;

    public User(Long id, String name, Boolean isAdmin) {
        super.setId(id);
        super.setName(name);
        this.isAdmin = isAdmin;
    }

    public User() {

    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void isAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
