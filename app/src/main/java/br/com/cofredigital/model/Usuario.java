package br.com.cofredigital.model;

import java.io.Serializable;

public class Usuario implements Serializable {

    public static final String PERFIL_ADMIN_GERAL = "ADMIN_GERAL";
    public static final String PERFIL_ADMIN_SETORIAL = "ADMIN_SETORIAL";
    public static final String PERFIL_FUNCIONARIO = "FUNCIONARIO";

    private String nome;
    private String email;
    private String senha;
    private String cargo;
    private String departamento; // TI, RH, Financeiro, Marketing, GERAL
    private String perfil;       // ADMIN_GERAL, ADMIN_SETORIAL, FUNCIONARIO
    private boolean isSenhaProvisoria;

    public Usuario() {}

    public Usuario(String nome, String email, String senha, String cargo, String departamento, String perfil) {
        this.nome = nome;
        this.email = email;
        this.senha = senha;
        this.cargo = cargo;
        this.departamento = departamento;
        this.perfil = perfil;
        this.isSenhaProvisoria = false;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public boolean isSenhaProvisoria() {
        return isSenhaProvisoria;
    }

    public void setSenhaProvisoria(boolean senhaProvisoria) {
        isSenhaProvisoria = senhaProvisoria;
    }

    public boolean isAdminGeral() {
        return PERFIL_ADMIN_GERAL.equalsIgnoreCase(perfil);
    }

    public boolean isAdminSetorial() {
        return PERFIL_ADMIN_SETORIAL.equalsIgnoreCase(perfil);
    }

    public boolean isFuncionario() {
        return PERFIL_FUNCIONARIO.equalsIgnoreCase(perfil);
    }

    public boolean isAdmin() {
        return isAdminGeral() || isAdminSetorial();
    }

    public boolean podeGerenciarUsuarios() {
        return isAdminGeral();
    }

    public boolean podeVisualizarCredencialNoDepartamento(String deptoAlvo) {
        if (deptoAlvo == null) return false;
        if (isAdminGeral()) return true; // ADMGERAL visualiza todos os setores
        if ("Compartilhado".equalsIgnoreCase(deptoAlvo)) return true; // Todos visualizam Compartilhado
        return departamento != null && departamento.equalsIgnoreCase(deptoAlvo); // Apenas seu próprio setor
    }

    public boolean podeGerenciarCredencialNoDepartamento(String deptoAlvo) {
        if (deptoAlvo == null) return false;
        if (isAdminGeral()) return true; // ADMGERAL gerencia todos os setores e Compartilhado
        if (isAdminSetorial()) {
            // O cofre Compartilhado é SOMENTE LEITURA para Administradores Setoriais
            if ("Compartilhado".equalsIgnoreCase(deptoAlvo)) {
                return false;
            }
            // Admin Setorial gerencia SOMENTE o seu próprio setor
            return departamento != null && departamento.equalsIgnoreCase(deptoAlvo);
        }
        return false; // Funcionários comuns não gerenciam nenhum departamento
    }

    public String getPerfilDescricao() {
        if (isAdminGeral()) {
            return "Administrador Geral";
        } else if (isAdminSetorial()) {
            return "Admin Setorial (" + departamento + ")";
        } else {
            return "Colaborador (" + departamento + ")";
        }
    }
}
