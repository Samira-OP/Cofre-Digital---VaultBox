package br.com.cofredigital.model;

import java.io.Serializable;

public class Credencial implements Serializable {

    private int id;
    private String sistema;
    private String usuario;
    private String senha;
    private String responsavel;
    private String departamento;
    private boolean senhaVisivel;

    public Credencial() {
        this.senhaVisivel = false;
    }

    public Credencial(int id, String sistema, String usuario, String senha, String responsavel, String departamento) {
        this.id = id;
        this.sistema = sistema;
        this.usuario = usuario;
        this.senha = senha;
        this.responsavel = responsavel;
        this.departamento = departamento;
        this.senhaVisivel = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSistema() {
        return sistema;
    }

    public void setSistema(String sistema) {
        this.sistema = sistema;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    // Compatibilidade com código legado se necessário
    public String getCategoria() {
        return departamento;
    }

    public void setCategoria(String categoria) {
        this.departamento = categoria;
    }

    public boolean isSenhaVisivel() {
        return senhaVisivel;
    }

    public void setSenhaVisivel(boolean senhaVisivel) {
        this.senhaVisivel = senhaVisivel;
    }
}
