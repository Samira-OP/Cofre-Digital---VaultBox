package br.com.cofredigital.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import br.com.cofredigital.model.Credencial;
import br.com.cofredigital.model.Usuario;

public class RepositorioCredenciais {

    private static final List<Credencial> listaCredenciais = new ArrayList<>();
    private static int proximoId = 1;

    static {
        // Inicializando o repositório corporativo com credenciais por departamento
        adicionar(new Credencial(0, "AWS Cloud Console", "aws.admin@vaultbox.com", "AWS#Cloud2026!", "Carlos Andrade", "TI"));
        adicionar(new Credencial(0, "GitHub Enterprise", "devops@vaultbox.com", "Git#Vault2026!", "Mariana Souza", "TI"));
        adicionar(new Credencial(0, "Servidor Interno Linux", "root.server", "Root#Pass99!", "Carlos Andrade", "TI"));

        adicionar(new Credencial(0, "Sistema Folha de Pagamento Senior", "rh.folha@vaultbox.com", "Folha#RH2026!", "Roberto Lima", "RH"));
        adicionar(new Credencial(0, "Portal Benefícios Flash", "beneficios@vaultbox.com", "Flash#RH2026!", "Roberto Lima", "RH"));

        adicionar(new Credencial(0, "ERP SAP Corporate", "financeiro.sap@vaultbox.com", "SAP#Fin2026!", "Fernanda Costa", "Financeiro"));
        adicionar(new Credencial(0, "Internet Banking PJ Santander", "conta.pj@vaultbox.com", "Bank#Santander2026!", "Fernanda Costa", "Financeiro"));

        adicionar(new Credencial(0, "Google Analytics & Ads", "mkt.ads@vaultbox.com", "Ads#Google2026!", "Lucas Martins", "Marketing"));
        adicionar(new Credencial(0, "Meta Business Suite", "mkt.meta@vaultbox.com", "Meta#Mkt2026!", "Lucas Martins", "Marketing"));

        adicionar(new Credencial(0, "Wi-Fi Visitantes & Corporativo", "wifi.visitante", "VaultBox#WiFi2026!", "Equipe TI", "Compartilhado"));
        adicionar(new Credencial(0, "Impressoras Multifuncionais", "admin.printers", "Print#Vault2026!", "Equipe TI", "Compartilhado"));
        adicionar(new Credencial(0, "Zoom Corporate Reuniões", "zoom.geral@vaultbox.com", "Zoom#Meet2026!", "Equipe TI", "Compartilhado"));
    }

    public static List<Credencial> getTodas() {
        return new ArrayList<>(listaCredenciais);
    }

    public static void adicionar(Credencial credencial) {
        credencial.setId(proximoId++);
        listaCredenciais.add(credencial);
    }

    public static void atualizar(Credencial credencialAtualizada) {
        for (int i = 0; i < listaCredenciais.size(); i++) {
            if (listaCredenciais.get(i).getId() == credencialAtualizada.getId()) {
                listaCredenciais.set(i, credencialAtualizada);
                return;
            }
        }
    }

    public static boolean remover(int id) {
        for (int i = 0; i < listaCredenciais.size(); i++) {
            if (listaCredenciais.get(i).getId() == id) {
                listaCredenciais.remove(i);
                return true;
            }
        }
        return false;
    }

    public static Credencial buscarPorId(int id) {
        for (Credencial c : listaCredenciais) {
            if (c.getId() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * Retorna a lista de credenciais permitidas para o usuário atual, considerando:
     * 1. ADMGERAL: Acessa todos os setores + Compartilhado.
     * 2. ADM Setorial / Funcionário: Acessam apenas seu setor específico + Compartilhado.
     */
    public static List<Credencial> getCredenciaisPermitidas(Usuario usuario, String termo, String filtroDepartamento) {
        List<Credencial> resultado = new ArrayList<>();
        if (usuario == null) {
            usuario = RepositorioUsuarios.getUsuarioLogado();
        }
        if (usuario == null) {
            return resultado;
        }

        String termoBusca = (termo != null) ? termo.toLowerCase().trim() : "";
        boolean filtrarDept = filtroDepartamento != null && !filtroDepartamento.equalsIgnoreCase("Todas");

        for (Credencial c : listaCredenciais) {
            // Regra rigorosa de Permissão de Visualização por Setor
            boolean temPermissao = usuario.podeVisualizarCredencialNoDepartamento(c.getDepartamento());

            if (!temPermissao) {
                continue; // Impede visualização de credenciais não autorizadas
            }

            // Regra de Filtro da Aba de Departamento
            boolean bateFiltro = !filtrarDept || c.getDepartamento().equalsIgnoreCase(filtroDepartamento);

            // Regra de Busca por Texto
            boolean bateTermo = termoBusca.isEmpty() ||
                    c.getSistema().toLowerCase().contains(termoBusca) ||
                    c.getUsuario().toLowerCase().contains(termoBusca) ||
                    c.getResponsavel().toLowerCase().contains(termoBusca);

            if (bateFiltro && bateTermo) {
                resultado.add(c);
            }
        }

        return resultado;
    }

    /**
     * Retorna os departamentos visíveis nas abas do Dashboard conforme o perfil do usuário.
     */
    public static List<String> getDepartamentosVisiveis(Usuario usuario) {
        if (usuario == null) {
            usuario = RepositorioUsuarios.getUsuarioLogado();
        }
        if (usuario != null && usuario.isAdminGeral()) {
            return Arrays.asList("Todas", "TI", "RH", "Financeiro", "Marketing", "Compartilhado");
        } else if (usuario != null) {
            return Arrays.asList("Todas", usuario.getDepartamento(), "Compartilhado");
        }
        return Arrays.asList("Todas", "Compartilhado");
    }

    /**
     * Retorna os departamentos permitidos para cadastro/edição de credencial.
     * - ADMGERAL: Todos os setores + Compartilhado.
     * - ADM SETORIAL: Apenas seu próprio setor (NÃO pode cadastrar no Compartilhado nem em outros setores).
     */
    public static List<String> getDepartamentosParaFormulario(Usuario usuario) {
        if (usuario == null) {
            usuario = RepositorioUsuarios.getUsuarioLogado();
        }
        if (usuario != null && usuario.isAdminGeral()) {
            return Arrays.asList("TI", "RH", "Financeiro", "Marketing", "Compartilhado");
        } else if (usuario != null && usuario.isAdminSetorial()) {
            return Collections.singletonList(usuario.getDepartamento());
        }
        return Collections.emptyList();
    }

    public static int getQuantidadeCofresAcessiveis(Usuario usuario) {
        if (usuario == null) {
            usuario = RepositorioUsuarios.getUsuarioLogado();
        }
        if (usuario != null && usuario.isAdminGeral()) return 5;
        return 2; // Seu setor + Compartilhado
    }
}
