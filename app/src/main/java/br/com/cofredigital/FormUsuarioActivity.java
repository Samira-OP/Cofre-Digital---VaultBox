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

import java.util.Arrays;
import java.util.List;

import br.com.cofredigital.data.RepositorioUsuarios;
import br.com.cofredigital.model.Usuario;

public class FormUsuarioActivity extends AppCompatActivity {

    private TextView txtTituloFormUsuario;
    private EditText edtNomeUsuario;
    private EditText edtEmailUsuario;
    private EditText edtSenhaUsuario;
    private EditText edtCargoUsuario;
    private Spinner spnSetorUsuario;
    private Spinner spnPerfilUsuario;
    private Button btnSalvarUsuario;
    private Button btnCancelarUsuario;

    private String usuarioEmailEdicao = null;
    private Usuario usuarioExistente = null;

    private final List<String> setores = Arrays.asList("TI", "RH", "Financeiro", "Marketing", "GERAL");
    private final List<String> perfisDisplay = Arrays.asList("Funcionário Comum", "Administrador Setorial", "Administrador Geral");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apenas Administrador Geral pode acessar
        Usuario usuarioLogado = RepositorioUsuarios.getUsuarioLogado();
        if (usuarioLogado == null || !usuarioLogado.isAdminGeral()) {
            Toast.makeText(this, "Acesso restrito ao Administrador Geral.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_usuario);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        txtTituloFormUsuario = findViewById(R.id.txtTituloFormUsuario);
        edtNomeUsuario = findViewById(R.id.edtNomeUsuario);
        edtEmailUsuario = findViewById(R.id.edtEmailUsuario);
        edtSenhaUsuario = findViewById(R.id.edtSenhaUsuario);
        edtCargoUsuario = findViewById(R.id.edtCargoUsuario);
        spnSetorUsuario = findViewById(R.id.spnSetorUsuario);
        spnPerfilUsuario = findViewById(R.id.spnPerfilUsuario);
        btnSalvarUsuario = findViewById(R.id.btnSalvarUsuario);
        btnCancelarUsuario = findViewById(R.id.btnCancelarUsuario);

        configurarSpinners();

        if (getIntent().hasExtra("USUARIO_EMAIL")) {
            usuarioEmailEdicao = getIntent().getStringExtra("USUARIO_EMAIL");
            if (usuarioEmailEdicao != null) {
                usuarioExistente = RepositorioUsuarios.buscarPorEmail(usuarioEmailEdicao);
                if (usuarioExistente != null) {
                    carregarDadosUsuario();
                }
            }
        }

        btnSalvarUsuario.setOnClickListener(v -> salvarUsuario());
        btnCancelarUsuario.setOnClickListener(v -> finish());
    }

    private void configurarSpinners() {
        ArrayAdapter<String> adapterSetor = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, setores) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (v instanceof TextView) ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(Color.WHITE);
                    ((TextView) v).setBackgroundColor(Color.parseColor("#241B3B"));
                    ((TextView) v).setPadding(20, 20, 20, 20);
                }
                return v;
            }
        };
        adapterSetor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSetorUsuario.setAdapter(adapterSetor);

        ArrayAdapter<String> adapterPerfil = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, perfisDisplay) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (v instanceof TextView) ((TextView) v).setTextColor(Color.WHITE);
                return v;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                if (v instanceof TextView) {
                    ((TextView) v).setTextColor(Color.WHITE);
                    ((TextView) v).setBackgroundColor(Color.parseColor("#241B3B"));
                    ((TextView) v).setPadding(20, 20, 20, 20);
                }
                return v;
            }
        };
        adapterPerfil.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnPerfilUsuario.setAdapter(adapterPerfil);
    }

    private void carregarDadosUsuario() {
        txtTituloFormUsuario.setText("Editar Colaborador");
        btnSalvarUsuario.setText("ATUALIZAR COLABORADOR");

        edtNomeUsuario.setText(usuarioExistente.getNome());
        edtEmailUsuario.setText(usuarioExistente.getEmail());
        edtEmailUsuario.setEnabled(false); // E-mail é chave única
        edtSenhaUsuario.setText(usuarioExistente.getSenha());
        edtCargoUsuario.setText(usuarioExistente.getCargo());

        int posSetor = setores.indexOf(usuarioExistente.getDepartamento());
        if (posSetor >= 0) spnSetorUsuario.setSelection(posSetor);

        if (usuarioExistente.isAdminGeral()) {
            spnPerfilUsuario.setSelection(2); // Administrador Geral
        } else if (usuarioExistente.isAdminSetorial()) {
            spnPerfilUsuario.setSelection(1); // Administrador Setorial
        } else {
            spnPerfilUsuario.setSelection(0); // Funcionário Comum
        }
    }

    private void salvarUsuario() {
        String nome = edtNomeUsuario.getText().toString().trim();
        String email = edtEmailUsuario.getText().toString().trim();
        String senha = edtSenhaUsuario.getText().toString();
        String cargo = edtCargoUsuario.getText().toString().trim();
        String setor = spnSetorUsuario.getSelectedItem() != null ? spnSetorUsuario.getSelectedItem().toString() : "TI";
        int posPerfil = spnPerfilUsuario.getSelectedItemPosition();

        String perfil;
        if (posPerfil == 2) {
            perfil = Usuario.PERFIL_ADMIN_GERAL;
        } else if (posPerfil == 1) {
            perfil = Usuario.PERFIL_ADMIN_SETORIAL;
        } else {
            perfil = Usuario.PERFIL_FUNCIONARIO;
        }

        if (nome.isEmpty()) {
            edtNomeUsuario.setError("Informe o nome do colaborador");
            edtNomeUsuario.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            edtEmailUsuario.setError("Informe o e-mail corporativo");
            edtEmailUsuario.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            edtSenhaUsuario.setError("Informe a senha inicial");
            edtSenhaUsuario.requestFocus();
            return;
        }

        if (cargo.isEmpty()) {
            edtCargoUsuario.setError("Informe o cargo do colaborador");
            edtCargoUsuario.requestFocus();
            return;
        }

        if (usuarioExistente != null) {
            // Edição
            usuarioExistente.setNome(nome);
            usuarioExistente.setSenha(senha);
            usuarioExistente.setCargo(cargo);
            usuarioExistente.setDepartamento(setor);
            usuarioExistente.setPerfil(perfil);

            RepositorioUsuarios.atualizarUsuario(usuarioExistente);
            Toast.makeText(this, "Colaborador atualizado com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            // Novo cadastro
            if (RepositorioUsuarios.buscarPorEmail(email) != null) {
                edtEmailUsuario.setError("E-mail já cadastrado!");
                edtEmailUsuario.requestFocus();
                return;
            }

            Usuario novo = new Usuario(nome, email, senha, cargo, setor, perfil);
            RepositorioUsuarios.adicionarUsuario(novo);
            Toast.makeText(this, "Novo colaborador cadastrado no VaultBox!", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
