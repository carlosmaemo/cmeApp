package com.cmembondeiro.cme.actividades.consulta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.entidades.Agendamentos_Desmarcado;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Desmarcado extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView lista_agendamentos;
    private DatabaseReference referencia;
    private DatabaseReference referencia_usuario_atual, referencia_agendamento;
    private FirebaseAuth autenticacao;
    private Query referencia_filtro;
    private TextView txt_vazio;
    private ProgressDialog dialogo;
    private String atual_id;

    private Agendamentos_Desmarcado agendamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desmarcado);

        toolbar = findViewById(R.id.desmarcado_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Agendamentos desmarcado");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_vazio = findViewById(R.id.desmarcado_lista_texto);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        atual_id = usuario.getUid();

        referencia_filtro = referencia.child("agendamentos-desmarcado").orderByChild("paciente_id").equalTo(atual_id);
        referencia_filtro.keepSynced(true);

        lista_agendamentos = findViewById(R.id.desmarcado_lista);
        lista_agendamentos.setHasFixedSize(true);
        lista_agendamentos.setLayoutManager(new LinearLayoutManager(this));

        referencia_usuario_atual = FirebaseDatabase.getInstance().getReference().child("usuarios/pacientes").child(atual_id);

    }

    @Override
    protected void onStart() {
        super.onStart();

        txt_vazio.setVisibility(View.INVISIBLE);
        lista_agendamentos.setVisibility(View.VISIBLE);
        pesquisar(referencia_filtro);

    }

    private void pesquisar(Query query) {

        FirebaseRecyclerAdapter<Agendamentos_Desmarcado, Desmarcado.AgendamentoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Agendamentos_Desmarcado, Desmarcado.AgendamentoViewHolder>(

                Agendamentos_Desmarcado.class,
                R.layout.layout_desmarcado,
                Desmarcado.AgendamentoViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final Desmarcado.AgendamentoViewHolder viewHolder, final Agendamentos_Desmarcado model, int position) {

                viewHolder.setNome(model.getConsulta());
                viewHolder.setMedico("Com: " + model.getAgendamento_medico());
                viewHolder.setInf(model.getAgendamento_inf());
                viewHolder.setData("Data: " + model.getAgendamento_data() + " | " + model.getAgendamento_hora_paciente());

                final String pendente_id = getRef(position).getKey();

            }
        };

        lista_agendamentos.setAdapter(firebaseRecyclerAdapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    txt_vazio.setVisibility(View.VISIBLE);
                    lista_agendamentos.setVisibility(View.INVISIBLE);
                } else {
                    txt_vazio.setVisibility(View.INVISIBLE);
                    lista_agendamentos.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class AgendamentoViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public AgendamentoViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

        public void setNome(String nome) {

            TextView view_nome = mView.findViewById(R.id.layout_desmarcado_nome);
            view_nome.setText(nome);

        }

        public void setMedico(String medico) {
            TextView view_medico = mView.findViewById(R.id.layout_desmarcado_medico);
            view_medico.setText(medico);
        }

        public void setInf(String inf) {
            TextView view_inf = mView.findViewById(R.id.layout_desmarcado_inf);
            view_inf.setText(inf);
        }

        public void setData(String data) {
            TextView view_data = mView.findViewById(R.id.layout_desmarcado_data);
            view_data.setText(data);
        }


    }


}