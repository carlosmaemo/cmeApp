package com.cmembondeiro.cme.fragmentos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.consulta.Consulta;
import com.cmembondeiro.cme.actividades.consulta.Pendente;
import com.cmembondeiro.cme.entidades.Agendamentos;
import com.cmembondeiro.cme.entidades.Notificacoes;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Notificacao extends Fragment {

    private RecyclerView lista_notificacao;
    private DatabaseReference referencia, referencia_notificacao;
    private FirebaseAuth autenticacao;
    private Query referencia_filtro, referencia_pesquisar;
    private Button ignorar_btn;
    private ProgressDialog dialogo;
    private TextView txt_vazio;

    private AlertDialog alerta;
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragmento_notificacao, container, false);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        final String paciente_id = usuario.getUid();

        referencia_filtro = referencia.child("notificacoes").child(paciente_id).orderByChild("titulo");
        referencia_filtro.keepSynced(true);

        LinearLayoutManager gerenciador_layout = new LinearLayoutManager(getContext());
        gerenciador_layout.setReverseLayout(true);
        gerenciador_layout.setStackFromEnd(true);

        txt_vazio = view.findViewById(R.id.frag_notificacao_vazio);
        lista_notificacao = view.findViewById(R.id.frag_notificacao_lista);
        lista_notificacao.setHasFixedSize(true);
        lista_notificacao.setLayoutManager(gerenciador_layout);

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        txt_vazio.setVisibility(View.INVISIBLE);
        lista_notificacao.setVisibility(View.VISIBLE);
        pesquisar(referencia_filtro);

    }

    private void pesquisar(Query query) {

        FirebaseRecyclerAdapter<Notificacoes, Notificacao.NotificacaoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notificacoes, Notificacao.NotificacaoViewHolder>(

                Notificacoes.class,
                R.layout.layout_notificacao,
                Notificacao.NotificacaoViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(Notificacao.NotificacaoViewHolder viewHolder, final Notificacoes model, int position) {

                viewHolder.setTitulo(model.getTitulo());
                viewHolder.setDescricao(model.getDescricao());

                final String notificacao_id = getRef(position).getKey();

                viewHolder.btn_ignorar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        referencia_notificacao = referencia.child("notificacoes").child(model.getPaciente_id()).child(notificacao_id);

                        referencia_notificacao.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
                    }
                });
            }
        };

        lista_notificacao.setAdapter(firebaseRecyclerAdapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    txt_vazio.setVisibility(View.VISIBLE);
                    lista_notificacao.setVisibility(View.INVISIBLE);
                } else {
                    txt_vazio.setVisibility(View.INVISIBLE);
                    lista_notificacao.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class NotificacaoViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView btn_ignorar;

        public NotificacaoViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            btn_ignorar = mView.findViewById(R.id.layout_notificacao_ignorar);
        }

        void setTitulo(String titulo) {

            TextView view_titulo = mView.findViewById(R.id.layout_notificacao_titulo);
            view_titulo.setText(titulo);

        }

        void setDescricao(String descricao) {
            TextView view_descricao = mView.findViewById(R.id.layout_notificacao_descricao);
            view_descricao.setText(descricao);
        }

    }
}
