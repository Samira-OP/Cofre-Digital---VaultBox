package br.com.cofredigital;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.com.cofredigital.data.RepositorioUsuarios;
import br.com.cofredigital.model.Usuario;

public class MainActivity extends AppCompatActivity {

    private EditText edtEmail;
    private EditText edtSenha;
    private Button btnEntrar;
    private TextView txtRecuperarSenha;
    private TextView txtCadastrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        btnEntrar = findViewById(R.id.btnEntrar);
        txtRecuperarSenha = findViewById(R.id.txtRecuperarSenha);
        txtCadastrar = findViewById(R.id.txtCadastrar);

        btnEntrar.setOnClickListener(v -> realizarLogin());

        txtRecuperarSenha.setOnClickListener(v ->
                Toast.makeText(this, "Redefinição de senha disponível via Administrador do VaultBox.", Toast.LENGTH_SHORT).show()
        );

        txtCadastrar.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CadastroActivity.class);
            startActivity(intent);
        });
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString();

        if (email.isEmpty()) {
            edtEmail.setError("Informe seu e-mail corporativo");
            edtEmail.requestFocus();
            return;
        }

        if (senha.isEmpty()) {
            edtSenha.setError("Informe sua senha");
            edtSenha.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Digite um e-mail válido");
            edtEmail.requestFocus();
            return;
        }

        // Autenticar pelo repositório
        Usuario usuario = RepositorioUsuarios.autenticar(email, senha);

        if (usuario != null) {
            if (usuario.isSenhaProvisoria()) {
                Toast.makeText(this, "Crie sua senha definitiva para continuar.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, NovaSenhaActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Bem-vindo ao VaultBox, " + usuario.getNome() + "!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Toast.makeText(this, "E-mail ou senha incorretos.", Toast.LENGTH_LONG).show();
        }
    }
}
