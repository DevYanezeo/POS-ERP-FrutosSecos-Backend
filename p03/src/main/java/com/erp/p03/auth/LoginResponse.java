package com.erp.p03.auth;

public class LoginResponse {
    private String token;
    private Integer idUsuario;
    private String email;
    private String nombre;
    private String rol;

    public LoginResponse() {}

    public LoginResponse(String token, Integer idUsuario, String email, String nombre, String rol) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}