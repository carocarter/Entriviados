package com.example.entriviados10;

public class User {

    private String email;
    private String name;

    public String getNombreUsuario() {
        return name;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.name = nombreUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public User(String nombreUsuario, String email) {
        this.name = nombreUsuario;
        this.email = email;
    }
}

