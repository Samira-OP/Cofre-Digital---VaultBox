package br.com.cofredigital;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.security.SecureRandom;
import java.util.List;

import br.com.cofredigital.data.RepositorioCredenciais;
import br.com.cofredigital.data.RepositorioUsuarios;
import br.com.cofredigital.model.Credencial;
import br.com.cofredigital.model.Usuario;

public class FormCredencialActivity extends AppCompatActivity {

    private TextView txtTituloForm;
    private EditText edtSistema;
    private EditText edtUsuario;
    private EditText edtSenhaForm;
    private TextView btnGerarSenha;
    private EditText edtResponsavel;
    private Spinner spnCategoria;
    private Button btnSalvarCredencial;
    private Button btnCancelarForm;

    private int credencialIdEdicao = -1;
    private Credencial credencialExistente;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Validação de Permissão: Apenas Administradores podem acessar o formulário
        usuarioLogado = RepositorioUsuarios.getUsuarioLogado();
        if (usuarioLogado == null || !usuarioLogado.isAdmin()) {
            Toast.makeText(this, "Acesso negado: Apenas administradores podem gerenciar credenciais.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_credencial);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        // Conectar visões
        txtTituloForm = findViewById(R.id.txtTituloForm);
        edtSistema = findViewById(R.id.edtSistema);
        edtUsuario = findViewById(R.id.edtUsuario);
        edtSenhaForm = findViewById(R.id.edtSenhaForm);
        btnGerarSenha = findViewById(R.id.btnGerarSenha);
        edtResponsavel = findViewById(R.id.edtResponsavel);
        spnCategoria = findViewById(R.id.spnCategoria);
        btnSalvarCredencial = findViewById(R.id.btnSalvarCredencial);
        btnCancelarForm = findViewById(R.id.btnCancelarForm);

        // Configurar Spinner de Departamentos
        configurarSpinnerDepartamentos();

        // Gerador de Senha Forte
        if (btnGerarSenha != null) {
            btnGerarSenha.setOnClickListener(v -> gerarSenhaForte());
        }

        // Verificar se é edição
        if (getIntent().hasExtra("CREDENCIAL_ID")) {
            credencialIdEdicao = getIntent().getIntExtra("CREDENCIAL_ID", -1);
            if (credencialIdEdicao != -1) {
                credencialExistente = RepositorioCredenciais.buscarPorId(credencialIdEdicao);
                if (credencialExistente != null) {
                    if (!usuarioLogado.podeGerenciarCredencialNoDepartamento(credencialExistente.getDepartamento())) {
                        Toast.makeText(this, "Acesso negado: Você não possui permissão para editar credenciais do setor " + credencialExistente.getDepartamento(), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                    carregarDadosParaEdicao();
                }
            }
        }

        btnSalvarCredencial.setOnClickListener(v -> salvarCredencial());
        btnCancelarForm.setOnClickListener(v -> finish());
    }

    private void configurarSpinnerDepartamentos() {
        List<String> departamentos = RepositorioCredenciais.getDepartamentosParaFormulario(usuarioLogado);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, departamentos) {

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.WHITE);
                    ((TextView) view).setTextSize(15);
                }
                return view;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.WHITE);
                    ((TextView) view).setBackgroundColor(Color.parseColor("#241B3B"));
                    ((TextView) view).setTextSize(15);
                    ((TextView) view).setPadding(20, 20, 20, 20);
                }
                return view;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoria.setAdapter(spinnerAdapter);
    }

    private void gerarSenhaForte() {
        String maiusculas = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String minusculas = "abcdefghijklmnopqrstuvwxyz";
        String numeros = "0123456789";
        String especiais = "!@#$%^&*()_+-=";
        String todos = maiusculas + minusculas + numeros + especiais;

        SecureRandom random = new SecureRandom();
        StringBuilder senha = new StringBuilder();

        // Garantir pelo menos um de cada tipo
        senha.append(maiusculas.charAt(random.nextInt(maiusculas.length())));
        senha.append(minusculas.charAt(random.nextInt(minusculas.length())));
        senha.append(numeros.charAt(random.nextInt(numeros.length())));
        senha.append(especiais.charAt(random.nextInt(especiais.length())));

        // Preencher o restante até 12 caracteres
        for (int i = 4; i < 12; i++) {
            senha.append(todos.charAt(random.nextInt(todos.length())));
        }

        String senhaGerada = senha.toString();
        edtSenhaForm.setText(senhaGerada);
        Toast.makeText(this, "Senha forte corporativa gerada com sucesso!", Toast.LENGTH_SHORT).show();
    }

    private void carregarDadosParaEdicao() {
        txtTituloForm.setText("Editar Credencial Corporativa");
        btnSalvarCredencial.setText("ATUALIZAR CREDENCIAL");

        edtSistema.setText(credencialExistente.getSistema());
        edtUsuario.setText(credencialExistente.getUsuario());
        edtSenhaForm.setText(credencialExistente.getSenha());
        edtResponsavel.setText(credencialExistente.getResponsavel());

        List<String> departamentos = RepositorioCredenciais.getDepartamentosParaFormulario(usuarioLogado);
        int posicao = departamentos.indexOf(credencialExistente.getDepartamento());
        if (posicao >= 0) {
            spnCategoria.setSelection(posicao);
        }
    }

    private void salvarCredencial() {
        String sistema = edtSistema.getText().toString().trim();
        String usuario = edtUsuario.getText().toString().trim();
        String senha = edtSenhaForm.getText().toString();
        String responsavel = edtResponsavel.getText().toString().trim();
        String departamento = spnCategoria.getSelectedItem() != null ? spnCategoria.getSelectedItem().toString() : "TI";

        if (sistema.isEmpty()) {
            edtSistema.setError("Informe o nome do sistema ou aplicação");
            edtSistema.requestFocus();
            return;
        }

        if (usuario.isEmpty()) {
            edtUsuario.setError("Informe o usuário ou e-mail corporativo");
            edtUsuario.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            edtSenhaForm.setError("Informe a senha");
            edtSenhaForm.requestFocus();
            return;
        }

        if (responsavel.isEmpty()) {
            edtResponsavel.setError("Informe o responsável");
            edtResponsavel.requestFocus();
            return;
        }

        // Validação estrita de permissão no código Java
        if (!usuarioLogado.podeGerenciarCredencialNoDepartamento(departamento)) {
            Toast.makeText(this, "Acesso negado: Você não possui permissão para gerenciar credenciais do setor " + departamento, Toast.LENGTH_SHORT).show();
            return;
        }

        if (credencialExistente != null) {
            // Edição
            credencialExistente.setSistema(sistema);
            credencialExistente.setUsuario(usuario);
            credencialExistente.setSenha(senha);
            credencialExistente.setResponsavel(responsavel);
            credencialExistente.setDepartamento(departamento);

            RepositorioCredenciais.atualizar(credencialExistente);
            Toast.makeText(this, "Credencial corporativa atualizada!", Toast.LENGTH_SHORT).show();
        } else {
            // Nova Inclusão
            Credencial novaCredencial = new Credencial(0, sistema, usuario, senha, responsavel, departamento);
            RepositorioCredenciais.adicionar(novaCredencial);
            Toast.makeText(this, "Credencial salva no Cofre VaultBox!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
