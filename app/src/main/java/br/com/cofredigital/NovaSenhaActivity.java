package br.com.cofredigital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.com.cofredigital.data.RepositorioUsuarios;

public class NovaSenhaActivity extends AppCompatActivity {

    private EditText edtNovaSenha;
    private EditText edtConfirmarSenha;
    private Button btnSalvarNovaSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nova_senha);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        edtNovaSenha = findViewById(R.id.edtNovaSenha);
        edtConfirmarSenha = findViewById(R.id.edtConfirmarSenha);
        btnSalvarNovaSenha = findViewById(R.id.btnSalvarNovaSenha);

        btnSalvarNovaSenha.setOnClickListener(v -> salvarNovaSenha());
    }

    private void salvarNovaSenha() {
        String novaSenha = edtNovaSenha.getText().toString();
        String confirmarSenha = edtConfirmarSenha.getText().toString();

        if (novaSenha.isEmpty()) {
            edtNovaSenha.setError("Digite sua nova senha");
            edtNovaSenha.requestFocus();
            return;
        }

        if (confirmarSenha.isEmpty()) {
            edtConfirmarSenha.setError("Confirme sua nova senha");
            edtConfirmarSenha.requestFocus();
            return;
        }

        if (novaSenha.length() < 6) {
            edtNovaSenha.setError("A senha deve ter pelo menos 6 caracteres");
            edtNovaSenha.requestFocus();
            return;
        }

        if (!novaSenha.equals(confirmarSenha)) {
            edtConfirmarSenha.setError("As senhas não coincidem");
            edtConfirmarSenha.requestFocus();
            return;
        }

        CadastroActivity.senhaDefinitiva = novaSenha;

        if (CadastroActivity.emailCadastrado != null && !CadastroActivity.emailCadastrado.isEmpty()) {
            RepositorioUsuarios.atualizarSenha(CadastroActivity.emailCadastrado, novaSenha);
        }

        Toast.makeText(this, "Nova senha salva com sucesso!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(NovaSenhaActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
