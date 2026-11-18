package br.com.cofredigital.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.cofredigital.R;
import br.com.cofredigital.model.Usuario;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    public interface OnUsuarioActionListener {
        void onEditar(Usuario usuario);
        void onExcluir(Usuario usuario);
    }

    private List<Usuario> listaUsuarios;
    private final OnUsuarioActionListener listener;

    public UsuarioAdapter(List<Usuario> listaUsuarios, OnUsuarioActionListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    public void atualizarLista(List<Usuario> novaLista) {
        this.listaUsuarios = novaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);

        holder.txtUsuarioNome.setText(usuario.getNome());
        holder.txtUsuarioEmail.setText(usuario.getEmail());
        holder.txtUsuarioSetorCargo.setText("Setor: " + usuario.getDepartamento() + " • Cargo: " + usuario.getCargo());

        if (usuario.isAdminGeral()) {
            holder.txtUsuarioPerfilBadge.setText("ADM GERAL");
        } else if (usuario.isAdminSetorial()) {
            holder.txtUsuarioPerfilBadge.setText("ADM SETORIAL");
        } else {
            holder.txtUsuarioPerfilBadge.setText("FUNCIONÁRIO");
        }

        holder.btnEditarUsuario.setOnClickListener(v -> {
            if (listener != null) listener.onEditar(usuario);
        });

        holder.btnExcluirUsuario.setOnClickListener(v -> {
            if (listener != null) listener.onExcluir(usuario);
        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios != null ? listaUsuarios.size() : 0;
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {

        TextView txtUsuarioNome;
        TextView txtUsuarioPerfilBadge;
        TextView txtUsuarioEmail;
        TextView txtUsuarioSetorCargo;
        ImageButton btnEditarUsuario;
        ImageButton btnExcluirUsuario;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsuarioNome = itemView.findViewById(R.id.txtUsuarioNome);
            txtUsuarioPerfilBadge = itemView.findViewById(R.id.txtUsuarioPerfilBadge);
            txtUsuarioEmail = itemView.findViewById(R.id.txtUsuarioEmail);
            txtUsuarioSetorCargo = itemView.findViewById(R.id.txtUsuarioSetorCargo);
            btnEditarUsuario = itemView.findViewById(R.id.btnEditarUsuario);
            btnExcluirUsuario = itemView.findViewById(R.id.btnExcluirUsuario);
        }
    }
}
