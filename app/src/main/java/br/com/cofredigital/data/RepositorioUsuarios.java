package br.com.cofredigital.data;

import java.util.ArrayList;
import java.util.List;

import br.com.cofredigital.model.Usuario;

public class RepositorioUsuarios {

    private static final List<Usuario> listaUsuarios = new ArrayList<>();
    private static Usuario usuarioLogadoAtual = null;

    static {
        // Administrador Geral
        listaUsuarios.add(new Usuario("Carlos Andrade (ADMGERAL)", "admgeral@vaultbox.com", "123456", "Administrador Geral", "GERAL", Usuario.PERFIL_ADMIN_GERAL));
        listaUsuarios.add(new Usuario("Carlos Andrade (Admin)", "admin@vaultbox.com", "123456", "Administrador Geral", "GERAL", Usuario.PERFIL_ADMIN_GERAL));

        // Administradores Setoriais
        listaUsuarios.add(new Usuario("Mariana Souza (ADMTI)", "admti@vaultbox.com", "123456", "Administradora de TI", "TI", Usuario.PERFIL_ADMIN_SETORIAL));
        listaUsuarios.add(new Usuario("Roberto Lima (ADMRH)", "admrh@vaultbox.com", "123456", "Administrador de RH", "RH", Usuario.PERFIL_ADMIN_SETORIAL));
        listaUsuarios.add(new Usuario("Fernanda Costa (ADMFIN)", "admfinanceiro@vaultbox.com", "123456", "Administradora Financeira", "Financeiro", Usuario.PERFIL_ADMIN_SETORIAL));

        // Funcionários Comuns
        listaUsuarios.add(new Usuario("João Silva (TI)", "funcionario.ti@vaultbox.com", "123456", "Analista de Tecnologia", "TI", Usuario.PERFIL_FUNCIONARIO));
        listaUsuarios.add(new Usuario("João Silva (TI)", "ti@vaultbox.com", "123456", "Analista de Tecnologia", "TI", Usuario.PERFIL_FUNCIONARIO));

        listaUsuarios.add(new Usuario("Aline Santos (RH)", "funcionario.rh@vaultbox.com", "123456", "Analista de RH", "RH", Usuario.PERFIL_FUNCIONARIO));
        listaUsuarios.add(new Usuario("Aline Santos (RH)", "rh@vaultbox.com", "123456", "Analista de RH", "RH", Usuario.PERFIL_FUNCIONARIO));

        listaUsuarios.add(new Usuario("Paulo Ribeiro (FIN)", "funcionario.fin@vaultbox.com", "123456", "Analista Financeiro", "Financeiro", Usuario.PERFIL_FUNCIONARIO));
        listaUsuarios.add(new Usuario("Paulo Ribeiro (FIN)", "financeiro@vaultbox.com", "123456", "Analista Financeiro", "Financeiro", Usuario.PERFIL_FUNCIONARIO));

        listaUsuarios.add(new Usuario("Lucas Martins (MKT)", "marketing@vaultbox.com", "123456", "Coordenador de Marketing", "Marketing", Usuario.PERFIL_FUNCIONARIO));
        listaUsuarios.add(new Usuario("Usuário Demonstrativo", "teste@cofre.com", "123456", "Gerente Geral", "GERAL", Usuario.PERFIL_ADMIN_GERAL));
    }

    public static Usuario autenticar(String email, String senha) {
        if (email == null || senha == null) return null;
        for (Usuario u : listaUsuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim()) && u.getSenha().equals(senha)) {
                usuarioLogadoAtual = u;
                return u;
            }
        }
        return null;
    }

    public static Usuario buscarPorEmail(String email) {
        if (email == null) return null;
        for (Usuario u : listaUsuarios) {
            if (u.getEmail().equalsIgnoreCase(email.trim())) {
                return u;
            }
        }
        return null;
    }

    public static void cadastrarNovoUsuario(String email, String senhaProvisoria) {
        Usuario novo = new Usuario("Novo Colaborador", email, senhaProvisoria, "Analista", "TI", Usuario.PERFIL_FUNCIONARIO);
        novo.setSenhaProvisoria(true);
        listaUsuarios.add(novo);
        usuarioLogadoAtual = novo;
    }

    public static void adicionarUsuario(Usuario usuario) {
        if (usuario != null) {
            // Se já existe, atualiza
            Usuario existente = buscarPorEmail(usuario.getEmail());
            if (existente != null) {
                atualizarUsuario(usuario);
            } else {
                listaUsuarios.add(usuario);
            }
        }
    }

    public static void atualizarUsuario(Usuario usuarioAtualizado) {
        for (int i = 0; i < listaUsuarios.size(); i++) {
            if (listaUsuarios.get(i).getEmail().equalsIgnoreCase(usuarioAtualizado.getEmail())) {
                listaUsuarios.set(i, usuarioAtualizado);
                return;
            }
        }
    }

    public static boolean removerUsuario(String email) {
        for (int i = 0; i < listaUsuarios.size(); i++) {
            if (listaUsuarios.get(i).getEmail().equalsIgnoreCase(email)) {
                listaUsuarios.remove(i);
                return true;
            }
        }
        return false;
    }

    public static void atualizarSenha(String email, String novaSenha) {
        Usuario u = buscarPorEmail(email);
        if (u != null) {
            u.setSenha(novaSenha);
            u.setSenhaProvisoria(false);
        }
    }

    public static Usuario getUsuarioLogado() {
        if (usuarioLogadoAtual == null && !listaUsuarios.isEmpty()) {
            usuarioLogadoAtual = listaUsuarios.get(0); // Admin padrão se nulo
        }
        return usuarioLogadoAtual;
    }

    public static void setUsuarioLogado(Usuario u) {
        usuarioLogadoAtual = u;
    }

    public static List<Usuario> getTodosUsuarios() {
        return new ArrayList<>(listaUsuarios);
    }
}
