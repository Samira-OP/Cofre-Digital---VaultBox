package br.com.cofredigital.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.cofredigital.R;
import br.com.cofredigital.model.Credencial;
import br.com.cofredigital.model.Usuario;

public class CredencialAdapter extends RecyclerView.Adapter<CredencialAdapter.CredencialViewHolder> {

    public interface OnCredencialActionListener {
        void onEditar(Credencial credencial);
        void onExcluir(Credencial credencial);
    }

    private List<Credencial> listaCredenciais;
    private final OnCredencialActionListener listener;
    private Usuario usuarioLogado;

    public CredencialAdapter(List<Credencial> listaCredenciais, Usuario usuarioLogado, OnCredencialActionListener listener) {
        this.listaCredenciais = listaCredenciais;
        this.usuarioLogado = usuarioLogado;
        this.listener = listener;
    }

    public void setUsuarioLogado(Usuario usuario) {
        this.usuarioLogado = usuario;
        notifyDataSetChanged();
    }

    public void atualizarLista(List<Credencial> novaLista) {
        this.listaCredenciais = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CredencialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_credencial, parent, false);
        return new CredencialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CredencialViewHolder holder, int position) {
        Credencial credencial = listaCredenciais.get(position);

        holder.txtSistema.setText(credencial.getSistema());
        holder.txtCategoria.setText(credencial.getDepartamento().toUpperCase());
        holder.txtUsuario.setText(credencial.getUsuario());
        holder.txtResponsavel.setText(credencial.getResponsavel());

        // Lógica de Ocultar/Revelar senha
        if (credencial.isSenhaVisivel()) {
            holder.txtSenha.setText(credencial.getSenha());
            holder.btnOcultarRevelarSenha.setImageResource(R.drawable.ic_visibility_off);
        } else {
            holder.txtSenha.setText("••••••••");
            holder.btnOcultarRevelarSenha.setImageResource(R.drawable.ic_visibility);
        }

        holder.btnOcultarRevelarSenha.setOnClickListener(v -> {
            credencial.setSenhaVisivel(!credencial.isSenhaVisivel());
            notifyItemChanged(position);
        });

        // Lógica de Copiar Senha para a Área de Transferência
        holder.btnCopiarSenha.setOnClickListener(v -> {
            Context context = v.getContext();
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Senha VaultBox", credencial.getSenha());
            if (clipboard != null) {
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context, "Senha de \"" + credencial.getSistema() + "\" copiada!", Toast.LENGTH_SHORT).show();
            }
        });

        // Permissão de Edição e Exclusão baseada nas regras de permissão do usuário
        boolean podeGerenciar = usuarioLogado != null && usuarioLogado.podeGerenciarCredencialNoDepartamento(credencial.getDepartamento());
        if (podeGerenciar) {
            holder.btnEditar.setVisibility(View.VISIBLE);
            holder.btnExcluir.setVisibility(View.VISIBLE);
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.btnExcluir.setVisibility(View.GONE);
        }

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditar(credencial);
            }
        });

        holder.btnExcluir.setOnClickListener(v -> {
            if (listener != null) {
                listener.onExcluir(credencial);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaCredenciais != null ? listaCredenciais.size() : 0;
    }

    static class CredencialViewHolder extends RecyclerView.ViewHolder {

        TextView txtSistema;
        TextView txtCategoria;
        TextView txtUsuario;
        TextView txtResponsavel;
        TextView txtSenha;
        ImageButton btnCopiarSenha;
        ImageButton btnOcultarRevelarSenha;
        ImageButton btnEditar;
        ImageButton btnExcluir;

        public CredencialViewHolder(@NonNull View itemView) {
            super(itemView);
            txtSistema = itemView.findViewById(R.id.txtSistema);
            txtCategoria = itemView.findViewById(R.id.txtCategoria);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);
            txtResponsavel = itemView.findViewById(R.id.txtResponsavel);
            txtSenha = itemView.findViewById(R.id.txtSenha);
            btnCopiarSenha = itemView.findViewById(R.id.btnCopiarSenha);
            btnOcultarRevelarSenha = itemView.findViewById(R.id.btnOcultarRevelarSenha);
            btnEditar = itemView.findViewById(R.id.btnEditar);
            btnExcluir = itemView.findViewById(R.id.btnExcluir);
        }
    }
}
