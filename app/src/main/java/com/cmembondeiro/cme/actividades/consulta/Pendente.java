package com.cmembondeiro.cme.actividades.consulta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.componentes.Email;
import com.cmembondeiro.cme.entidades.Agendamentos_Pendente;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Pendente extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView lista_agendamentos;
    private DatabaseReference referencia;
    private DatabaseReference referencia_usuario_atual, referencia_agendamento, referencia_email, referencia_perfil;
    private FirebaseAuth autenticacao;
    private Query referencia_filtro;
    private TextView txt_vazio;
    private ProgressDialog dialogo;
    private String atual_id;
    private String assunto, assunto2, emissor, password, receptor, rodape, nome;

    private TextView input_hora;
    private Button btn_hora;
    private AlertDialog.Builder prompt_hora;
    private AlertDialog dialogo_hora, alerta;

    private Agendamentos_Pendente agendamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pendente);

        toolbar = findViewById(R.id.pendente_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Agendamentos pendente");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_vazio = findViewById(R.id.pendente_lista_texto);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        atual_id = usuario.getUid();

        referencia_filtro = referencia.child("agendamentos-pendente").orderByChild("paciente_id").equalTo(atual_id);
        referencia_filtro.keepSynced(true);


        referencia_perfil = FirebaseDatabase.getInstance().getReference().child("usuarios").child("pacientes").child(atual_id);
        referencia_perfil.keepSynced(true);

        referencia_perfil.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String perfil_nome = dataSnapshot.child("nome").getValue().toString();
                String perfil_apelido = dataSnapshot.child("apelido").getValue().toString();

                nome  = perfil_nome + " " + perfil_apelido;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        referencia_email = FirebaseDatabase.getInstance().getReference().child("informacoes").child("-012020-email");
        referencia_email.keepSynced(true);
        referencia_email.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String not_assunto_consulta = dataSnapshot.child("assunto-pendente").getValue().toString();
                String not_assunto_consulta2 = dataSnapshot.child("assunto-remarcar").getValue().toString();
                String not_emissor = dataSnapshot.child("emissor").getValue().toString();
                String not_password = dataSnapshot.child("password").getValue().toString();
                String not_receptor = dataSnapshot.child("receptor").getValue().toString();
                String not_rodape = dataSnapshot.child("rodape").getValue().toString();

                assunto = not_assunto_consulta;
                assunto2 = not_assunto_consulta2;
                emissor = not_emissor;
                password = not_password;
                receptor = not_receptor;
                rodape = not_rodape;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        lista_agendamentos = findViewById(R.id.pendente_lista);
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

        FirebaseRecyclerAdapter<Agendamentos_Pendente, Pendente.AgendamentoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Agendamentos_Pendente, Pendente.AgendamentoViewHolder>(

                Agendamentos_Pendente.class,
                R.layout.layout_pendente,
                Pendente.AgendamentoViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(final Pendente.AgendamentoViewHolder viewHolder, final Agendamentos_Pendente model, int position) {

                viewHolder.setNome(model.getConsulta());
                viewHolder.setMedico("Com: " + model.getConsulta_medico());
                viewHolder.setInf(model.getConsulta_inf());
                viewHolder.setData("Data: " + model.getConsulta_data() + " | " + model.getConsulta_hora_paciente());

                final String pendente_id = getRef(position).getKey();

                viewHolder.btn_cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(Pendente.this);

                        builder.setTitle("Cancelar agendamento");
                        builder.setMessage("Pretende mesmo cancelar o agendamento?");

                        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                dialogo = new ProgressDialog(Pendente.this);
                                dialogo.setTitle("Cancelando agendamento");
                                dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
                                dialogo.setCanceledOnTouchOutside(false);
                                dialogo.show();

                                referencia_agendamento = referencia.child("agendamentos-pendente").child(pendente_id);

                                referencia_agendamento.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful()) {
                                            dialogo.dismiss();

                                            String key = FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).push().getKey();
                                            HashMap<String, String> notificacao = new HashMap<>();
                                            notificacao.put("id", key);
                                            notificacao.put("paciente_id", atual_id);
                                            notificacao.put("titulo", "Cancelamento de consulta");
                                            notificacao.put("descricao", "A consulta de " + model.getConsulta() + " marcado para " + model.getConsulta_data() + " às " + model.getConsulta_hora_paciente() + " foi cancelado.");

                                            FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).child(key).setValue(notificacao);

                                            Toast.makeText(Pendente.this, "Consulta cancelada!", Toast.LENGTH_SHORT).show();

                                            final String mensagem = nome + " cancelou a consulta de " + model.getConsulta() + " que estava marcado para " + model.getConsulta_data() + " às " + model.getConsulta_hora_paciente() + ".";

                                            if(assunto.contains("_$cons")){
                                                String novo_texto = assunto.replace("_$cons", model.getConsulta());
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
                                            Toast.makeText(Pendente.this, "Falha: Não foi possível cancelar a consulta, tente novamente!", Toast.LENGTH_SHORT).show();
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

                viewHolder.btn_alterar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LinearLayout layout_prompt = new LinearLayout(Pendente.this);
                        layout_prompt.setOrientation(LinearLayout.VERTICAL);
                        layout_prompt.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        parametros.setMargins(20, 0, 20, 0);

                        LinearLayout.LayoutParams parametros_btn = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        parametros_btn.setMargins(100, 0, 100, 0);

                        input_hora = new TextView(Pendente.this);
                        input_hora.setTextSize(30f);
                        input_hora.setPadding(5,10,5,10);

                        btn_hora = new Button(Pendente.this);
                        btn_hora.setText("Alterar hora");

                        prompt_hora = new AlertDialog.Builder(Pendente.this);
                        prompt_hora.setTitle("Hora da consulta");
                        prompt_hora.setIcon(R.drawable.ic_hora);

                        layout_prompt.addView(input_hora, parametros);
                        layout_prompt.addView(btn_hora, parametros_btn);

                        prompt_hora.setView(layout_prompt);

                        prompt_hora.setPositiveButton("Alterar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                alterar_agenda(pendente_id, model.getConsulta_id(), model.getConsulta(), model.getConsulta_medico(), model.getConsulta_ormm(), model.getConsulta_inf(), model.getConsulta_data(), model.getConsulta_hora_inicial(), model.getConsulta_hora_final(), (String) input_hora.getText());

                            }
                        });

                        prompt_hora.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        prompt_hora.setMessage("A consulta decorrerá a partir das " + model.getConsulta_hora_inicial() + " até as " + model.getConsulta_hora_final() + ". \nEscolha o horário do agendamento.");
                        input_hora.setText(model.getConsulta_hora_paciente());
                        dialogo_hora = prompt_hora.create();
                        dialogo_hora.show();

                        btn_hora.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Calendar calendario = Calendar.getInstance();
                                final int hora_atual = calendario.get(Calendar.HOUR_OF_DAY);
                                final int minuto_atual = calendario.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(Pendente.this,
                                        new TimePickerDialog.OnTimeSetListener() {

                                            @Override
                                            public void onTimeSet(TimePicker view, int hora_dia,
                                                                  int minuto_dia) {

                                                input_hora.setText(hora_dia + ":" + minuto_dia);
                                                input_hora.invalidate();

                                                dialogo_hora.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                                input_hora.setTextColor(Color.GRAY);

                                                String pattern = "HH:mm";
                                                SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                                                try {

                                                    Date data_hora_marcada = sdf.parse(input_hora.getText().toString());
                                                    Date data_hora_inicial = sdf.parse(model.getConsulta_hora_inicial());
                                                    Date data_hora_final = sdf.parse(model.getConsulta_hora_final());

                                                    if(data_hora_marcada.compareTo(data_hora_inicial) < 0) {

                                                        dialogo_hora.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                        input_hora.setTextColor(Color.LTGRAY);
                                                        Toast.makeText(getApplicationContext(), "Horário indisponível", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if(data_hora_marcada.compareTo(data_hora_final) > 0) {

                                                        dialogo_hora.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                                                        input_hora.setTextColor(Color.LTGRAY);
                                                        Toast.makeText(getApplicationContext(), "Horário indisponíve!", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else if(data_hora_marcada.compareTo(data_hora_inicial) > 0 && data_hora_marcada.compareTo(data_hora_final) < 0) {

                                                        dialogo_hora.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                                                        input_hora.setTextColor(Color.GRAY);

                                                    }

                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }

                                            }
                                        }, hora_atual, minuto_atual, true);
                                timePickerDialog.show();

                            }
                        });
                    }
                });

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
        ImageView btn_alterar, btn_cancelar;

        public AgendamentoViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            btn_alterar = mView.findViewById(R.id.layout_pendente_btn_alterar);
            btn_cancelar = mView.findViewById(R.id.layout_pendente_btn_cancelar);
        }

        public void setNome(String nome) {

            TextView view_nome = mView.findViewById(R.id.layout_pendente_nome);
            view_nome.setText(nome);

        }

        public void setMedico(String medico) {
            TextView view_medico = mView.findViewById(R.id.layout_pendente_medico);
            view_medico.setText(medico);
        }

        public void setInf(String inf) {
            TextView view_inf = mView.findViewById(R.id.layout_pendente_inf);
            view_inf.setText(inf);
        }

        public void setData(String data) {
            TextView view_data = mView.findViewById(R.id.layout_pendente_data);
            view_data.setText(data);
        }


    }

    private void alterar_agenda(final String id, final String cons_id, final String cons_consulta, final String cons_medico, final String cons_ormm, final String cons_inf, final String cons_data, final String cons_horaInicial, final String cons_horaFinal, final String cons_hora) {

        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Alterando hora do agendamento");
        dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
        dialogo.setCanceledOnTouchOutside(false);
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

                agendamento = new Agendamentos_Pendente();

                agendamento.setConsulta_id(cons_id);
                agendamento.setConsulta(cons_consulta);
                agendamento.setConsulta_medico(cons_medico);
                agendamento.setConsulta_ormm(cons_ormm);
                agendamento.setConsulta_inf(cons_inf);
                agendamento.setConsulta_data(cons_data);
                agendamento.setConsulta_hora_inicial(cons_horaInicial);
                agendamento.setConsulta_hora_final(cons_horaFinal);
                agendamento.setConsulta_hora_paciente(cons_hora);

                agendamento.setId(id);
                agendamento.setPaciente_id(atual_id);
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

                referencia_agendamento = referencia.child("agendamentos-pendente").child(id);

                referencia_agendamento.setValue(agendamento).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            dialogo.dismiss();

                            String key = FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).push().getKey();
                            HashMap<String, String> notificacao = new HashMap<>();
                            notificacao.put("id", key);
                            notificacao.put("paciente_id", atual_id);
                            notificacao.put("titulo", "Alteração de consulta");
                            notificacao.put("descricao", "A consulta de " + agendamento.getConsulta() + " foi reagendado para " + cons_data + " às " +  cons_hora + ", a consulta será confirmado assim que efectuar o pagamento.");

                            FirebaseDatabase.getInstance().getReference("notificacoes").child(atual_id).child("-00201f-cons-pagamento").removeValue();
                            HashMap<String, String> dadosNot = new HashMap<>();
                            dadosNot.put("id", "-00201f-cons-pagamento");
                            dadosNot.put("paciente_id", atual_id);
                            dadosNot.put("titulo", "Como efectuar pagamento?");
                            dadosNot.put("descricao", "Para efectuar pagamento de uma consulta encaminhe-se para a instalação do CME.\nO pagamento deve ser efectuado antes do dia da consulta.");

                            FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).child("-00201f-cons-pagamento").setValue(dadosNot);
                            FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).child(key).setValue(notificacao);

                            Toast.makeText(Pendente.this, "Consulta reagendado, aguarde a confirmação", Toast.LENGTH_SHORT).show();

                            final String mensagem = nome + " " + apelido + " reagendou a consulta de " + agendamento.getConsulta() + " de " + agendamento.getConsulta_data() + " às " + agendamento.getConsulta_hora_paciente() + " para " + cons_data + " às " + cons_hora + ", confirme a consulta assim que o paciente efectuar o pagamento.";

                            if(assunto2.contains("_$cons")){
                                String novo_texto = assunto2.replace("_$cons", agendamento.getConsulta());
                                assunto2 = novo_texto;
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
                                        sender.EnviarEmail(emissor, password, receptor, assunto2, mensagem + "\n\n\n" + rodape);
                                    } catch (Exception e) {
                                    }
                                }

                            }).start();

                        }
                        else {
                            dialogo.hide();
                            Toast.makeText(Pendente.this, "Falha: Não foi possível remarcar a consulta, tente novamente!", Toast.LENGTH_SHORT).show();
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