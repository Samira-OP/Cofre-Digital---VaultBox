package br.com.cofredigital;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.cofredigital.adapter.CredencialAdapter;
import br.com.cofredigital.data.RepositorioCredenciais;
import br.com.cofredigital.data.RepositorioUsuarios;
import br.com.cofredigital.model.Credencial;
import br.com.cofredigital.model.Usuario;

public class DashboardActivity extends AppCompatActivity implements CredencialAdapter.OnCredencialActionListener {

    private EditText edtPesquisa;
    private LinearLayout layoutCategorias;
    private Button btnAdicionarCredencial;
    private ImageButton btnPerfil;
    private ImageButton btnSair;
    private RecyclerView recyclerCredenciais;
    private TextView txtListaVazia;
    private TextView txtBoasVindas;

    private TextView txtTotalCofres;
    private TextView txtTotalCredenciais;

    private CredencialAdapter adapter;
    private String categoriaSelecionada = "Todas";
    private String termoPesquisaAtual = "";
    private final List<Button> botoesCategoria = new ArrayList<>();
    private Usuario usuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        // Configuração das margens das barras do sistema
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        // Inicialização dos componentes
        edtPesquisa = findViewById(R.id.edtPesquisa);
        layoutCategorias = findViewById(R.id.layoutCategorias);
        btnAdicionarCredencial = findViewById(R.id.btnAdicionarCredencial);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnSair = findViewById(R.id.btnSair);
        recyclerCredenciais = findViewById(R.id.recyclerCredenciais);
        txtListaVazia = findViewById(R.id.txtListaVazia);
        txtBoasVindas = findViewById(R.id.txtBoasVindas);
        txtTotalCofres = findViewById(R.id.txtTotalCofres);
        txtTotalCredenciais = findViewById(R.id.txtTotalCredenciais);

        usuarioLogado = RepositorioUsuarios.getUsuarioLogado();

        // Configurar permissão do botão Adicionar
        if (usuarioLogado != null && usuarioLogado.isAdmin()) {
            btnAdicionarCredencial.setVisibility(View.VISIBLE);
        } else {
            btnAdicionarCredencial.setVisibility(View.GONE);
        }

        // Configuração da mensagem de boas-vindas do usuário
        if (usuarioLogado != null) {
            txtBoasVindas.setText(usuarioLogado.getNome() + " • " + usuarioLogado.getPerfilDescricao());
            txtTotalCofres.setText(RepositorioCredenciais.getQuantidadeCofresAcessiveis(usuarioLogado) + " Departamentos");
        }

        // Configuração da RecyclerView
        recyclerCredenciais.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CredencialAdapter(new ArrayList<>(), usuarioLogado, this);
        recyclerCredenciais.setAdapter(adapter);

        // Renderização dos botões de departamentos visíveis
        montarBotoesCategoria();

        // Listener da pesquisa por texto
        edtPesquisa.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                termoPesquisaAtual = s.toString();
                filtrarEAtualizarLista();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Botão para adicionar credencial (Apenas Admin)
        btnAdicionarCredencial.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardActivity.this, FormCredencialActivity.class);
            startActivity(intent);
        });

        // Botão de Perfil
        if (btnPerfil != null) {
            btnPerfil.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, PerfilActivity.class);
                startActivity(intent);
            });
        }

        // Botão de Sair (Logout)
        btnSair.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Encerrar Sessão VaultBox")
                    .setMessage("Deseja realmente sair da sua conta?")
                    .setPositiveButton("SAIR", (dialog, which) -> {
                        Toast.makeText(DashboardActivity.this, "Sessão encerrada.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("CANCELAR", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        usuarioLogado = RepositorioUsuarios.getUsuarioLogado();
        if (usuarioLogado != null) {
            txtBoasVindas.setText(usuarioLogado.getNome() + " • " + usuarioLogado.getPerfilDescricao());
            txtTotalCofres.setText(RepositorioCredenciais.getQuantidadeCofresAcessiveis(usuarioLogado) + " Departamentos");
            if (usuarioLogado.isAdmin()) {
                btnAdicionarCredencial.setVisibility(View.VISIBLE);
            } else {
                btnAdicionarCredencial.setVisibility(View.GONE);
            }
        }
        if (adapter != null) {
            adapter.setUsuarioLogado(usuarioLogado);
        }
        montarBotoesCategoria();
        filtrarEAtualizarLista();
    }

    private void montarBotoesCategoria() {
        layoutCategorias.removeAllViews();
        botoesCategoria.clear();

        List<String> departamentos = RepositorioCredenciais.getDepartamentosVisiveis(usuarioLogado);

        // Se a categoria selecionada anteriormente não estiver visível para este usuário, reseta para "Todas"
        if (!departamentos.contains(categoriaSelecionada)) {
            categoriaSelecionada = "Todas";
        }

        for (String cat : departamentos) {
            Button btnCat = new Button(this);
            btnCat.setText(cat);
            btnCat.setTextSize(12);
            btnCat.setAllCaps(false);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 14, 0);
            btnCat.setLayoutParams(params);

            btnCat.setOnClickListener(v -> {
                categoriaSelecionada = cat;
                atualizarEstiloBotoesCategoria();
                filtrarEAtualizarLista();
            });

            botoesCategoria.add(btnCat);
            layoutCategorias.addView(btnCat);
        }

        atualizarEstiloBotoesCategoria();
    }

    private void atualizarEstiloBotoesCategoria() {
        for (Button btn : botoesCategoria) {
            String nomeCat = btn.getText().toString();
            if (nomeCat.equalsIgnoreCase(categoriaSelecionada)) {
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.purple_primary));
                btn.setTextColor(Color.WHITE);
            } else {
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.background_card));
                btn.setTextColor(ContextCompat.getColor(this, R.color.text_secondary));
            }
        }
    }

    private void filtrarEAtualizarLista() {
        List<Credencial> resultado = RepositorioCredenciais.getCredenciaisPermitidas(usuarioLogado, termoPesquisaAtual, categoriaSelecionada);
        adapter.atualizarLista(resultado);

        List<Credencial> totalPermitidasSemFiltro = RepositorioCredenciais.getCredenciaisPermitidas(usuarioLogado, "", "Todas");
        txtTotalCredenciais.setText(totalPermitidasSemFiltro.size() + " Permitidas");

        if (resultado.isEmpty()) {
            txtListaVazia.setVisibility(View.VISIBLE);
            recyclerCredenciais.setVisibility(View.GONE);
        } else {
            txtListaVazia.setVisibility(View.GONE);
            recyclerCredenciais.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onEditar(Credencial credencial) {
        if (usuarioLogado != null && !usuarioLogado.isAdmin()) {
            Toast.makeText(this, "Apenas administradores podem editar credenciais.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(DashboardActivity.this, FormCredencialActivity.class);
        intent.putExtra("CREDENCIAL_ID", credencial.getId());
        startActivity(intent);
    }

    @Override
    public void onExcluir(Credencial credencial) {
        if (usuarioLogado != null && !usuarioLogado.isAdmin()) {
            Toast.makeText(this, "Apenas administradores podem excluir credenciais.", Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle("Excluir Credencial")
                .setMessage("Deseja realmente excluir a credencial corporativa de \"" + credencial.getSistema() + "\"?")
                .setPositiveButton("EXCLUIR", (dialog, which) -> {
                    boolean removido = RepositorioCredenciais.remover(credencial.getId());
                    if (removido) {
                        Toast.makeText(this, "Credencial excluída com sucesso!", Toast.LENGTH_SHORT).show();
                        filtrarEAtualizarLista();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();
    }
}
