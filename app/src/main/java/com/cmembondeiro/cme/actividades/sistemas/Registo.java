/*
......................................................
Desenvolvido por Eng. Maemo, Carlos
CELL GAME, EI | carlosmaemo@outlook.com | (+258) 840 584 831 | www.cellgame.tk
Centro Médico Embondeiro, Lda. Copyright (C)2019
Gerência de Tecnologias de Informação
......................................................
*/

package com.cmembondeiro.cme.actividades.sistemas;

import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.conta.Conta;
import com.cmembondeiro.cme.actividades.menus.Principal;
import com.cmembondeiro.cme.entidades.Pacientes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;


public class Registo extends AppCompatActivity {

    private String intent_paciente_id, intent_celular, intent_regiao;
    private Button btn_continuar;
    private EditText input_nome, input_apelido;
    private ProgressDialog dialogo;
    private Pacientes paciente;
    private DatabaseReference referencia, referencia_notificacoes, referencia_notificacoes2, referencia_notificacoes3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registo);

        intent_paciente_id = getIntent().getStringExtra("paciente_id");
        intent_celular = getIntent().getStringExtra("celular");
        intent_regiao = getIntent().getStringExtra("regiao");

        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Aplicando alterações");
        dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
        dialogo.setCanceledOnTouchOutside(false);

        referencia = FirebaseDatabase.getInstance().getReference().child("usuarios/pacientes").child(intent_paciente_id);

        btn_continuar = findViewById(R.id.registo_btn_registo);
        input_nome = findViewById(R.id.registo_input_nome);
        input_apelido = findViewById(R.id.registo_input_apelido);

        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!input_nome.getText().toString().isEmpty()
                        || !input_apelido.getText().toString().isEmpty()) {

                    paciente = new Pacientes();
                    paciente.setNome(input_nome.getText().toString());
                    paciente.setApelido(input_apelido.getText().toString());
                    paciente.setId(intent_paciente_id);
                    paciente.setCelular(intent_celular);
                    paciente.setRegiao(intent_regiao);
                    paciente.setPac("null");
                    paciente.setNascimento("null");
                    paciente.setResidencia("null");
                    paciente.setProvincia("null");
                    paciente.setBi("null");
                    paciente.setSexo("null");
                    paciente.setEstado("null");
                    paciente.setImagem("null");
                    paciente.setMiniatura("null");
                    paciente.setTipo_usuario("Paciente");

                    aplicar_alteracao();

                } else {

                    Toast.makeText(Registo.this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void aplicar_alteracao() {

        dialogo.show();

        referencia.setValue(paciente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {

                    String key1 = FirebaseDatabase.getInstance().getReference().child("notificacoes").child(intent_paciente_id).push().getKey();
                    String key2 = FirebaseDatabase.getInstance().getReference().child("notificacoes").child(intent_paciente_id).push().getKey();
                    String key3 = FirebaseDatabase.getInstance().getReference().child("notificacoes").child(intent_paciente_id).push().getKey();

                    HashMap<String, String> dados1 = new HashMap<>();
                    dados1.put("id", key1);
                    dados1.put("paciente_id", paciente.getId());
                    dados1.put("titulo", "Você sabia?");
                    dados1.put("descricao", "O CME App permite que você marque consultas e comunique-se com a equipe do CME.");

                    HashMap<String, String> dados2 = new HashMap<>();
                    dados2.put("id", key1);
                    dados2.put("paciente_id", paciente.getId());
                    dados2.put("titulo", "Noticia");
                    dados2.put("descricao", "Você pode a qualquer momento modificar os dados da sua conta em definições.");

                    HashMap<String, String> dados3 = new HashMap<>();
                    dados2.put("id", key3);
                    dados2.put("paciente_id", paciente.getId());
                    dados2.put("titulo", "Noticia");
                    dados2.put("descricao", "Olá " + paciente.getNome() + ", a sua conta foi criada com sucesso!");

                    FirebaseDatabase.getInstance().getReference().child("notificacoes").child(intent_paciente_id).child(key1).setValue(dados1);
                    FirebaseDatabase.getInstance().getReference().child("notificacoes").child(intent_paciente_id).child(key2).setValue(dados2);
                    FirebaseDatabase.getInstance().getReference().child("notificacoes").child(intent_paciente_id).child(key3).setValue(dados3);

                    dialogo.dismiss();
                    Intent intent = new Intent(Registo.this, Principal.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

    }
}