package com.cmembondeiro.cme.fragmentos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.consulta.Consulta;
import com.cmembondeiro.cme.actividades.consulta.Pendente;
import com.cmembondeiro.cme.componentes.Email;
import com.cmembondeiro.cme.entidades.Agendamentos_Desmarcado;
import com.cmembondeiro.cme.entidades.Agendamentos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class Agendamento extends Fragment {

    private RecyclerView lista_consultas;
    private DatabaseReference referencia, referencia_agendamento, referencia_usuario_atual, referencia_email, referencia_perfil;
    private FirebaseAuth autenticacao;
    private Query referencia_filtro, referencia_pesquisar;
    private TextView txt_vazio;
    private Button agendar_btn;
    private ProgressDialog dialogo;

    private AlertDialog alerta;
    private View view;

    private Agendamentos_Desmarcado agendamento;
    private String paciente_id;
    private String assunto, emissor, password, receptor, rodape;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragmento_agendamento, container, false);

        txt_vazio = view.findViewById(R.id.frag_consulta_vazio);
        agendar_btn = view.findViewById(R.id.frag_consulta_agendamento_btn);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        paciente_id = usuario.getUid();

        referencia_usuario_atual = FirebaseDatabase.getInstance().getReference().child("usuarios/pacientes").child(paciente_id);

        referencia_filtro = referencia.child("agendamentos").orderByChild("paciente_id").equalTo(paciente_id);
        referencia_filtro.keepSynced(true);

        referencia_email = FirebaseDatabase.getInstance().getReference().child("informacoes").child("-012020-email");
        referencia_email.keepSynced(true);
        referencia_email.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String not_assunto_consulta = dataSnapshot.child("assunto-agendamento").getValue().toString();
                String not_emissor = dataSnapshot.child("emissor").getValue().toString();
                String not_password = dataSnapshot.child("password").getValue().toString();
                String not_receptor = dataSnapshot.child("receptor").getValue().toString();
                String not_rodape = dataSnapshot.child("rodape").getValue().toString();

                assunto = not_assunto_consulta;
                emissor = not_emissor;
                password = not_password;
                receptor = not_receptor;
                rodape = not_rodape;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager gerenciador_layout = new LinearLayoutManager(getContext());
        gerenciador_layout.setReverseLayout(true);
        gerenciador_layout.setStackFromEnd(true);

        lista_consultas = view.findViewById(R.id.frag_consulta_lista);
        lista_consultas.setHasFixedSize(true);
        lista_consultas.setLayoutManager(gerenciador_layout);

        agendar_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getContext(), Consulta.class);
                startActivity(intent);

            }
        });

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();

        lista_consultas.setVisibility(View.VISIBLE);
        txt_vazio.setVisibility(View.INVISIBLE);
        pesquisar(referencia_filtro);

    }

    private void pesquisar(Query query) {

        FirebaseRecyclerAdapter<Agendamentos, Agendamento.ConsultaViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Agendamentos, Agendamento.ConsultaViewHolder>(

                Agendamentos.class,
                R.layout.layout_agendamento,
                Agendamento.ConsultaViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(Agendamento.ConsultaViewHolder viewHolder, final Agendamentos model, int position) {

                viewHolder.setNome(model.getConsulta());
                viewHolder.setMedico("Com: " + model.getAgendamento_medico());
                viewHolder.setInf(model.getAgendamento_inf());
                viewHolder.setData("Data: " + model.getAgendamento_data() + " | " + model.getAgendamento_hora_paciente());

                final String agendamento_id = getRef(position).getKey();

                viewHolder.btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("Desmarcar consulta");
                        builder.setMessage("Pretende mesmo desmarcar a consulta?\n\nPagamento já efectuado, pode remarcar a consulta a qualquer momento.");

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                dialogo = new ProgressDialog(getContext());
                                dialogo.setTitle("Desmarcando a consulta");
                                dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
                                dialogo.setCanceledOnTouchOutside(false);
                                dialogo.show();

                                referencia_agendamento = referencia.child("agendamentos").child(agendamento_id);

                                referencia_agendamento.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()) {
                                            dialogo.dismiss();

                                            desmarcar(agendamento_id, model.getConsulta(), model.getAgendamento_medico(), model.getAgendamento_ormm(), model.getAgendamento_inf(), model.getAgendamento_data(), model.getAgendamento_hora_inicial(), model.getAgendamento_hora_final(), model.getAgendamento_hora_paciente());

                                        }
                                        else {
                                            dialogo.hide();
                                            Toast.makeText(getContext(), "Falha: Não foi possível desmarcar a consulta, tente novamente!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        });

                        builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        alerta = builder.create();

                        alerta.show();

                    }
                });

            }
        };

        lista_consultas.setAdapter(firebaseRecyclerAdapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    txt_vazio.setVisibility(View.VISIBLE);
                    lista_consultas.setVisibility(View.INVISIBLE);
                } else {
                    txt_vazio.setVisibility(View.INVISIBLE);
                    lista_consultas.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public static class ConsultaViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView btn_alterar, btn_cancelar;

        public ConsultaViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            btn_cancelar = mView.findViewById(R.id.layout_consulta_cancelar);
        }

        void setNome(String nome) {

            TextView view_nome = mView.findViewById(R.id.layout_consulta_nome);
            view_nome.setText(nome);

        }

        void setMedico(String medico) {
            TextView view_medico = mView.findViewById(R.id.layout_consulta_medico);
            view_medico.setText(medico);
        }

        void setInf(String inf) {
            TextView view_inf = mView.findViewById(R.id.layout_consulta_inf);
            view_inf.setText(inf);
        }

        void setData(String data) {
            TextView view_data = mView.findViewById(R.id.layout_consulta_data);
            view_data.setText(data);
        }

    }

    private void desmarcar(final String cons_id, final String cons_consulta, final String cons_medico, final String cons_ormm, final String cons_inf, final String cons_data, final String cons_horaInicial, final String cons_horaFinal, final String cons_hora) {

        dialogo.show();

        referencia_usuario_atual.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final String nome = dataSnapshot.child("nome").getValue().toString();
                final String apelido = dataSnapshot.child("apelido").getValue().toString();
                String celular = dataSnapshot.child("celular").getValue().toString();
                String regiao = dataSnapshot.child("regiao").getValue().toString();
                String provincia = dataSnapshot.child("provincia").getValue().toString();
                String residencia = dataSnapshot.child("residencia").getValue().toString();
                String pac = dataSnapshot.child("pac").getValue().toString();
                String bi = dataSnapshot.child("bi").getValue().toString();
                String nascimento = dataSnapshot.child("nascimento").getValue().toString();
                String sexo = dataSnapshot.child("sexo").getValue().toString();

                agendamento = new Agendamentos_Desmarcado();

                agendamento.setAgendamento_id(cons_id);
                agendamento.setConsulta(cons_consulta);
                agendamento.setAgendamento_medico(cons_medico);
                agendamento.setAgendamento_ormm(cons_ormm);
                agendamento.setAgendamento_inf(cons_inf);
                agendamento.setAgendamento_data(cons_data);
                agendamento.setAgendamento_hora_inicial(cons_horaInicial);
                agendamento.setAgendamento_hora_final(cons_horaFinal);
                agendamento.setAgendamento_hora_paciente(cons_hora);

                agendamento.setPaciente_id(paciente_id);
                agendamento.setPaciente_nome(nome);
                agendamento.setPaciente_apelido(apelido);
                agendamento.setPaciente_bi(bi);
                agendamento.setPaciente_celular(celular);
                agendamento.setPaciente_nascimento(nascimento);
                agendamento.setPaciente_pac(pac);
                agendamento.setPaciente_provincia(provincia);
                agendamento.setPaciente_regiao(regiao);
                agendamento.setPaciente_residencia(residencia);
                agendamento.setPaciente_sexo(sexo);

                referencia_agendamento = FirebaseDatabase.getInstance().getReference("agendamentos-desmarcado").push();

                HashMap<String, String> dados = new HashMap<>();

                dados.put("id", referencia_agendamento.getKey());
                dados.put("agendamento_id", agendamento.getAgendamento_id());
                dados.put("consulta", agendamento.getConsulta());
                dados.put("agendamento_medico", agendamento.getAgendamento_medico());
                dados.put("agendamento_ormm", agendamento.getAgendamento_ormm());
                dados.put("agendamento_inf", agendamento.getAgendamento_inf());
                dados.put("agendamento_data", agendamento.getAgendamento_data());
                dados.put("agendamento_hora_inicial", agendamento.getAgendamento_hora_inicial());
                dados.put("agendamento_hora_final", agendamento.getAgendamento_hora_final());
                dados.put("agendamento_hora_paciente", agendamento.getAgendamento_hora_paciente());

                dados.put("paciente_id", agendamento.getPaciente_id());
                dados.put("paciente_nome", agendamento.getPaciente_nome());
                dados.put("paciente_apelido", agendamento.getPaciente_apelido());
                dados.put("paciente_bi", agendamento.getPaciente_bi());
                dados.put("paciente_celular", agendamento.getPaciente_celular());
                dados.put("paciente_nascimento", agendamento.getPaciente_nascimento());
                dados.put("paciente_pac", agendamento.getPaciente_pac());
                dados.put("paciente_provincia", agendamento.getPaciente_provincia());
                dados.put("paciente_regiao", agendamento.getPaciente_regiao());
                dados.put("paciente_residencia", agendamento.getPaciente_residencia());
                dados.put("paciente_sexo", agendamento.getPaciente_sexo());

                referencia_agendamento.setValue(dados).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialogo.dismiss();

                            String key = FirebaseDatabase.getInstance().getReference().child("notificacoes").child(paciente_id).push().getKey();
                            HashMap<String, String> notificacao = new HashMap<>();
                            notificacao.put("id", key);
                            notificacao.put("paciente_id", paciente_id);
                            notificacao.put("titulo", "Desmarcação de consulta");
                            notificacao.put("descricao", "A consulta de " + agendamento.getConsulta() + " agendado para " + agendamento.getAgendamento_data() + " às " + agendamento.getAgendamento_hora_paciente() + " foi desmarcado, contacte o CME para remarcar novamente.");

                            // NOTIFICACAO DE PAGAMENTO
                            FirebaseDatabase.getInstance().getReference("notificacoes").child(paciente_id).child("-00211f-cons-remarcar").removeValue();
                            HashMap<String, String> dadosNot = new HashMap<>();
                            dadosNot.put("id", "-00211f-cons-remarcar");
                            dadosNot.put("paciente_id", paciente_id);
                            dadosNot.put("titulo", "Consultas desmarcada");
                            dadosNot.put("descricao", "Após desmarcar uma consulta com pagamento já efectuado é possivel remarcar novamente.\n\nPara remarcar uma consulta envie uma mensagem através do Chat ou encaminhe-se para a instalação do CME.");

                            FirebaseDatabase.getInstance().getReference().child("notificacoes").child(paciente_id).child(key).setValue(notificacao);
                            FirebaseDatabase.getInstance().getReference().child("notificacoes").child(paciente_id).child("-00211f-cons-remarcar").setValue(dadosNot);

                            Toast.makeText(getContext(), "Agendamento desmarcado!", Toast.LENGTH_SHORT).show();

                            final String mensagem = nome + " " + apelido + " desmarcou a consulta de " + agendamento.getConsulta() + " que estava agendado para " +  agendamento.getAgendamento_data() + " às " + agendamento.getAgendamento_hora_paciente() + ", o paciente poderá remarcar a consulta novamente porque o pagamento já foi efectuado.";

                            if(assunto.contains("_$cons")){
                                String novo_texto = assunto.replace("_$cons", agendamento.getConsulta());
                                assunto = novo_texto;
                            }

                            if(rodape.contains("_n")){
                                String novo_texto = rodape.replace("_n","\n");
                                rodape = novo_texto;
                            }

                            new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    final Email sender = new Email();
                                    try {
                                        sender.EnviarEmail(emissor, password, receptor, assunto, mensagem + "\n\n\n" + rodape);
                                    } catch (Exception e) {
                                    }
                                }

                            }).start();

                        }
                        else {
                            dialogo.hide();
                            Toast.makeText(getContext(), "Falha: Não foi desmarcar o agendamento, tente novamente!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}