package br.com.cofredigital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.com.cofredigital.data.RepositorioUsuarios;
import br.com.cofredigital.model.Usuario;

public class PerfilActivity extends AppCompatActivity {

    private ImageButton btnVoltarPerfil;
    private TextView txtPerfilNome;
    private TextView txtPerfilFuncaoTag;
    private TextView txtPerfilEmail;
    private TextView txtPerfilCargo;
    private TextView txtPerfilDepartamento;
    private TextView txtPerfilNivelAcesso;
    private Button btnGestaoUsuarios;
    private Button btnSairPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        btnVoltarPerfil = findViewById(R.id.btnVoltarPerfil);
        txtPerfilNome = findViewById(R.id.txtPerfilNome);
        txtPerfilFuncaoTag = findViewById(R.id.txtPerfilFuncaoTag);
        txtPerfilEmail = findViewById(R.id.txtPerfilEmail);
        txtPerfilCargo = findViewById(R.id.txtPerfilCargo);
        txtPerfilDepartamento = findViewById(R.id.txtPerfilDepartamento);
        txtPerfilNivelAcesso = findViewById(R.id.txtPerfilNivelAcesso);
        btnGestaoUsuarios = findViewById(R.id.btnGestaoUsuarios);
        btnSairPerfil = findViewById(R.id.btnSairPerfil);

        Usuario usuario = RepositorioUsuarios.getUsuarioLogado();

        if (usuario != null) {
            txtPerfilNome.setText(usuario.getNome());
            txtPerfilEmail.setText(usuario.getEmail());
            txtPerfilCargo.setText(usuario.getCargo());
            txtPerfilDepartamento.setText(usuario.getDepartamento());

            if (usuario.isAdminGeral()) {
                txtPerfilFuncaoTag.setText("ADMINISTRADOR GERAL");
                txtPerfilNivelAcesso.setText("Administrador Geral (Controle Total + Gestão de Usuários)");
                if (btnGestaoUsuarios != null) btnGestaoUsuarios.setVisibility(View.VISIBLE);
            } else if (usuario.isAdminSetorial()) {
                txtPerfilFuncaoTag.setText("ADMIN SETORIAL (" + usuario.getDepartamento().toUpperCase() + ")");
                txtPerfilNivelAcesso.setText("Admin Setorial (" + usuario.getDepartamento() + " + Compartilhado)");
                if (btnGestaoUsuarios != null) btnGestaoUsuarios.setVisibility(View.GONE);
            } else {
                txtPerfilFuncaoTag.setText("FUNCIONÁRIO (" + usuario.getDepartamento().toUpperCase() + ")");
                txtPerfilNivelAcesso.setText("Funcionário (" + usuario.getDepartamento() + " + Compartilhado)");
                if (btnGestaoUsuarios != null) btnGestaoUsuarios.setVisibility(View.GONE);
            }
        }

        btnVoltarPerfil.setOnClickListener(v -> finish());

        if (btnGestaoUsuarios != null) {
            btnGestaoUsuarios.setOnClickListener(v -> {
                Intent intent = new Intent(PerfilActivity.this, GestaoUsuariosActivity.class);
                startActivity(intent);
            });
        }

        btnSairPerfil.setOnClickListener(v -> {
            Toast.makeText(this, "Sessão encerrada.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(PerfilActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}
