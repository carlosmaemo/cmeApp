package com.cmembondeiro.cme.actividades.consulta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.componentes.Email;
import com.cmembondeiro.cme.entidades.Agendamentos_Pendente;
import com.cmembondeiro.cme.entidades.Consultas;
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

public class Consulta extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView lista_agendamentos;
    private DatabaseReference referencia;
    private DatabaseReference referencia_usuario_atual, referencia_agendamento, referencia_email, referencia_perfil;
    private FirebaseAuth autenticacao;
    private Query referencia_filtro, referencia_pesquisar;
    private TextView resultado_pesquisa;
    private ProgressDialog dialogo;
    private String atual_id;
    private TextView txt_vazio;

    private TextView input_hora;
    private Button btn_hora;
    private AlertDialog.Builder prompt_hora;
    private AlertDialog dialogo_hora;
    private String assunto, emissor, password, receptor, rodape;

    private Agendamentos_Pendente agendamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        toolbar = findViewById(R.id.consulta_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Agendar consulta");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txt_vazio = findViewById(R.id.frag_notificacao_vazio);
        resultado_pesquisa = findViewById(R.id.consulta_lista_texto);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Agendando consulta");
        dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
        dialogo.setCanceledOnTouchOutside(false);

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        atual_id = usuario.getUid();

        referencia_filtro = referencia.child("consultas").orderByChild("nome");
        referencia_filtro.keepSynced(true);

        referencia_email = FirebaseDatabase.getInstance().getReference().child("informacoes").child("-012020-email");
        referencia_email.keepSynced(true);
        referencia_email.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String not_assunto_consulta = dataSnapshot.child("assunto-consulta").getValue().toString();
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

        lista_agendamentos = findViewById(R.id.consulta_lista);
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
        resultado_pesquisa.setVisibility(View.INVISIBLE);

    }

    private void pesquisar(Query query) {

        FirebaseRecyclerAdapter<Consultas, Consulta.AgendamentoViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Consultas, Consulta.AgendamentoViewHolder>(

                Consultas.class,
                R.layout.layout_consulta,
                Consulta.AgendamentoViewHolder.class,
                query

        ) {

            @Override
            protected void populateViewHolder(final Consulta.AgendamentoViewHolder viewHolder, final Consultas model, int position) {

                viewHolder.setNome(model.getConsulta());
                viewHolder.setMedico("Com: " + model.getConsulta_medico());
                viewHolder.setInf(model.getConsulta_inf());
                viewHolder.setData("Data: " + model.getConsulta_data() + " | " + model.getConsulta_hora_inicial() + " ás " + model.getConsulta_hora_final());

                final String consulta_id = getRef(position).getKey();

                viewHolder.btn_agendar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        LinearLayout layout_prompt = new LinearLayout(Consulta.this);
                        layout_prompt.setOrientation(LinearLayout.VERTICAL);
                        layout_prompt.setGravity(Gravity.CENTER);

                        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        parametros.setMargins(20, 0, 20, 0);

                        LinearLayout.LayoutParams parametros_btn = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        parametros_btn.setMargins(100, 0, 100, 0);

                        input_hora = new TextView(Consulta.this);
                        input_hora.setTextSize(30f);
                        input_hora.setPadding(5,10,5,10);

                        btn_hora = new Button(Consulta.this);
                        btn_hora.setText("Alterar hora");

                        prompt_hora = new AlertDialog.Builder(Consulta.this);
                        prompt_hora.setTitle("Hora da consulta");
                        prompt_hora.setIcon(R.drawable.ic_hora);

                        layout_prompt.addView(input_hora, parametros);
                        layout_prompt.addView(btn_hora, parametros_btn);

                        prompt_hora.setView(layout_prompt);

                        prompt_hora.setPositiveButton("Agendar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                agendar(model.getId(), model.getConsulta(), model.getConsulta_medico(), model.getConsulta_ormm(), model.getConsulta_inf(), model.getConsulta_data(), model.getConsulta_hora_inicial(), model.getConsulta_hora_final(), input_hora.getText().toString());
                            }
                        });

                        prompt_hora.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        prompt_hora.setMessage("A consulta decorrerá a partir das " + model.getConsulta_hora_inicial() + " até as " + model.getConsulta_hora_final() + ". \nEscolha o horário do agendamento.");
                        input_hora.setText(model.getConsulta_hora_inicial());
                        dialogo_hora = prompt_hora.create();
                        dialogo_hora.show();

                        btn_hora.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Calendar calendario = Calendar.getInstance();
                                final int hora_atual = calendario.get(Calendar.HOUR_OF_DAY);
                                final int minuto_atual = calendario.get(Calendar.MINUTE);

                                TimePickerDialog timePickerDialog = new TimePickerDialog(Consulta.this,
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_agendamento, menu);

        final MenuItem item = menu.findItem(R.id.menu_agendamento_btn_pesquisar);

        SearchView pesquisar_view = (SearchView) MenuItemCompat.getActionView(item);

        pesquisar_view.setQueryHint("Pesquisar");

        pesquisar_view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                pesquisar_dados(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                pesquisar_dados(newText);

                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void pesquisar_dados(final String query) {

        if (query.isEmpty()) {
            pesquisar(referencia_filtro);
            resultado_pesquisa.setVisibility(View.INVISIBLE);
            lista_agendamentos.setVisibility(View.VISIBLE);

        } else {

            txt_vazio.setVisibility(View.INVISIBLE);
            referencia_pesquisar = referencia.child("consultas").orderByChild("consulta")
                    .startAt(query)
                    .endAt(query + "\uf8ff");

            referencia_pesquisar.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {

                        pesquisar(referencia_pesquisar);
                        resultado_pesquisa.setVisibility(View.INVISIBLE);
                        lista_agendamentos.setVisibility(View.VISIBLE);

                    } else {

                        resultado_pesquisa.setText("Nenhum resultado para '" + query + "'");
                        lista_agendamentos.setVisibility(View.INVISIBLE);
                        resultado_pesquisa.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return false;

    }

    public static class AgendamentoViewHolder extends RecyclerView.ViewHolder {

        View mView;
        ImageView btn_agendar;

        public AgendamentoViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            btn_agendar = mView.findViewById(R.id.layout_agendar_btn);
        }

        public void setNome(String nome) {

            TextView view_nome = mView.findViewById(R.id.layout_agendar_nome);
            view_nome.setText(nome);

        }

        public void setMedico(String medico) {
            TextView view_medico = mView.findViewById(R.id.layout_agendar_medico);
            view_medico.setText(medico);
        }

        public void setInf(String inf) {
            TextView view_inf = mView.findViewById(R.id.layout_agendar_inf);
            view_inf.setText(inf);
        }

        public void setData(String data) {
            TextView view_data = mView.findViewById(R.id.layout_agendar_data);
            view_data.setText(data);
        }


    }

    private void agendar(final String cons_id, final String cons_consulta, final String cons_medico, final String cons_ormm, final String cons_inf, final String cons_data, final String cons_horaInicial, final String cons_horaFinal, final String cons_hora) {

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

                referencia_agendamento = FirebaseDatabase.getInstance().getReference("agendamentos-pendente").push();

                HashMap<String, String> dados = new HashMap<>();

                dados.put("id", referencia_agendamento.getKey());
                dados.put("consulta_id", agendamento.getConsulta_id());
                dados.put("consulta", agendamento.getConsulta());
                dados.put("consulta_medico", agendamento.getConsulta_medico());
                dados.put("consulta_ormm", agendamento.getConsulta_ormm());
                dados.put("consulta_inf", agendamento.getConsulta_inf());
                dados.put("consulta_data", agendamento.getConsulta_data());
                dados.put("consulta_hora_inicial", agendamento.getConsulta_hora_inicial());
                dados.put("consulta_hora_final", agendamento.getConsulta_hora_final());
                dados.put("consulta_hora_paciente", agendamento.getConsulta_hora_paciente());

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

                            // NOTIFICACAO DE AGENDAMENTO
                            String key = FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).push().getKey();
                            HashMap<String, String> notificacao = new HashMap<>();
                            notificacao.put("id", key);
                            notificacao.put("paciente_id", atual_id);
                            notificacao.put("titulo", "Marcação de consulta");
                            notificacao.put("descricao", "A consulta de " + agendamento.getConsulta() + " foi agendado para " + cons_data + " às " + cons_hora + ", a consulta será confirmado assim que efectuar o pagamento.");

                            // NOTIFICACAO DE PAGAMENTO
                            FirebaseDatabase.getInstance().getReference("notificacoes").child(atual_id).child("-00201f-cons-pagamento").removeValue();
                            HashMap<String, String> dadosNot = new HashMap<>();
                            dadosNot.put("id", "-00201f-cons-pagamento");
                            dadosNot.put("paciente_id", atual_id);
                            dadosNot.put("titulo", "Como efectuar pagamento?");
                            dadosNot.put("descricao", "Para efectuar pagamento de uma consulta encaminhe-se para a instalação do CME.\nO pagamento deve ser efectuado antes do dia da consulta.");

                            FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).child(key).setValue(notificacao);
                            FirebaseDatabase.getInstance().getReference().child("notificacoes").child(atual_id).child("-00201f-cons-pagamento").setValue(dadosNot);

                            Toast.makeText(Consulta.this, "Consulta agendada, aguarde a confirmação", Toast.LENGTH_SHORT).show();

                            final String mensagem = nome + " " + apelido + " agendou uma consulta de " + agendamento.getConsulta() + " para " + cons_data + " às " + cons_hora + ", confirme a consulta assim que o paciente efectuar o pagamento.";

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
                            Toast.makeText(Consulta.this, "Falha: Não foi agendar a consulta, tente novamente!", Toast.LENGTH_SHORT).show();
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
