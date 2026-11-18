package br.com.cofredigital;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.cofredigital.adapter.UsuarioAdapter;
import br.com.cofredigital.data.RepositorioUsuarios;
import br.com.cofredigital.model.Usuario;

public class GestaoUsuariosActivity extends AppCompatActivity implements UsuarioAdapter.OnUsuarioActionListener {

    private ImageButton btnVoltarGestao;
    private Button btnAdicionarUsuario;
    private RecyclerView recyclerUsuarios;
    private TextView txtListaUsuariosVazia;

    private UsuarioAdapter adapter;
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Restrição de Acesso: Apenas Administrador Geral pode acessar
        usuarioLogado = RepositorioUsuarios.getUsuarioLogado();
        if (usuarioLogado == null || !usuarioLogado.isAdminGeral()) {
            Toast.makeText(this, "Acesso restrito ao Administrador Geral (ADMGERAL).", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_gestao_usuarios);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        btnVoltarGestao = findViewById(R.id.btnVoltarGestao);
        btnAdicionarUsuario = findViewById(R.id.btnAdicionarUsuario);
        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);
        txtListaUsuariosVazia = findViewById(R.id.txtListaUsuariosVazia);

        recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsuarioAdapter(new ArrayList<>(), this);
        recyclerUsuarios.setAdapter(adapter);

        btnVoltarGestao.setOnClickListener(v -> finish());

        btnAdicionarUsuario.setOnClickListener(v -> {
            Intent intent = new Intent(GestaoUsuariosActivity.this, FormUsuarioActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarUsuarios();
    }

    private void carregarUsuarios() {
        List<Usuario> lista = RepositorioUsuarios.getTodosUsuarios();
        adapter.atualizarLista(lista);

        if (lista.isEmpty()) {
            txtListaUsuariosVazia.setVisibility(View.VISIBLE);
            recyclerUsuarios.setVisibility(View.GONE);
        } else {
            txtListaUsuariosVazia.setVisibility(View.GONE);
            recyclerUsuarios.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditar(Usuario usuario) {
        Intent intent = new Intent(GestaoUsuariosActivity.this, FormUsuarioActivity.class);
        intent.putExtra("USUARIO_EMAIL", usuario.getEmail());
        startActivity(intent);
    }

    @Override
    public void onExcluir(Usuario usuario) {
        if (usuario.getEmail().equalsIgnoreCase(usuarioLogado.getEmail())) {
            Toast.makeText(this, "Você não pode excluir sua própria conta!", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Excluir Colaborador")
                .setMessage("Deseja realmente remover o acesso de \"" + usuario.getNome() + "\"?")
                .setPositiveButton("EXCLUIR", (dialog, which) -> {
                    boolean removido = RepositorioUsuarios.removerUsuario(usuario.getEmail());
                    if (removido) {
                        Toast.makeText(this, "Colaborador removido com sucesso!", Toast.LENGTH_SHORT).show();
                        carregarUsuarios();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }
}
