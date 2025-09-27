package com.erp.p03.auth;

public class RegisterRequest {
    private String nombre;
    private String email;
    private String password;
    private String rol;
    private String rut;
    private String telefono;

    public RegisterRequest() {}

    public RegisterRequest(String nombre, String email, String password, String rol, String rut, String telefono) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.rut = rut;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}