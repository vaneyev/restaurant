package org.example.restaurant.to;

public class UserTo {
    private Long id;
    private String name;
    private Boolean isAdmin;

    public UserTo(Long id, String name, Boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.isAdmin = isAdmin;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean isAdmin() {
        return isAdmin;
    }

    public void isAdmin(Boolean admin) {
        isAdmin = admin;
    }
}
