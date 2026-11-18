package br.com.cofredigital;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import br.com.cofredigital.data.RepositorioUsuarios;

public class CadastroActivity extends AppCompatActivity {

    private EditText edtEmailCadastro;
    private Button btnCadastrar;
    private TextView txtVoltarLogin;

    public static String emailCadastrado = "";
    public static String senhaProvisoria = "";
    public static String senhaDefinitiva = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        edtEmailCadastro = findViewById(R.id.edtEmailCadastro);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        txtVoltarLogin = findViewById(R.id.txtVoltarLogin);

        btnCadastrar.setOnClickListener(v -> realizarCadastro());
        txtVoltarLogin.setOnClickListener(v -> finish());
    }

    private void realizarCadastro() {
        String email = edtEmailCadastro.getText().toString().trim();

        if (email.isEmpty()) {
            edtEmailCadastro.setError("Informe seu e-mail corporativo");
            edtEmailCadastro.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmailCadastro.setError("Digite um e-mail corporativo válido");
            edtEmailCadastro.requestFocus();
            return;
        }

        int numero = (int) (Math.random() * 900000) + 100000;
        String senha = String.valueOf(numero);

        emailCadastrado = email;
        senhaProvisoria = senha;

        // Registrar no repositório de usuários
        RepositorioUsuarios.cadastrarNovoUsuario(email, senha);

        new AlertDialog.Builder(this)
                .setTitle("Cadastro realizado!")
                .setMessage(
                        "Seu cadastro no VaultBox foi feito com sucesso!\n\n" +
                                "E-MAIL CORPORATIVO:\n" + email + "\n\n" +
                                "SENHA PROVISÓRIA:\n" + senha + "\n\n" +
                                "Utilize estes dados para realizar seu primeiro acesso."
                )
                .setPositiveButton("IR PARA LOGIN", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }
}
