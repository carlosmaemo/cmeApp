/*
......................................................
Desenvolvido por Eng. Maemo, Carlos
CELL GAME, EI | carlosmaemo@outlook.com | (+258) 840 584 831 | www.cellgame.tk
Centro Médico Embondeiro, Lda. Copyright (C)2019
Gerência de Tecnologias de Informação
......................................................
*/

package com.cmembondeiro.cme.actividades.sistemas;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.menus.Principal;
import com.cmembondeiro.cme.entidades.Pacientes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Verificacao extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia;

    private String verificacao_id, intent_celular, intent_conta, intent_regiao;
    private EditText verificacao_codigo;
    private Button btn_registar;
    private TextView verificacao_texto,verificacao_voltar;

    private ProgressDialog dialogo;
    private ProgressBar progresso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verificacao);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        dialogo = new ProgressDialog(this);

        btn_registar = findViewById(R.id.verificacao_btn_codigo);
        progresso = findViewById(R.id.verificacao_progresso);
        verificacao_codigo = findViewById(R.id.verificacao_input_codigo);
        verificacao_texto = findViewById(R.id.verificacao_txt2);
        verificacao_voltar = findViewById(R.id.verificacao_txt3);

        intent_celular = getIntent().getStringExtra("celular");
        intent_conta = getIntent().getStringExtra("conta");
        intent_regiao = getIntent().getStringExtra("regiao");

        enviar_codigo_verificacao(intent_celular);

        btn_registar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               String codigo = verificacao_codigo.getText().toString().trim();

                if(codigo.isEmpty()) {
                    Toast.makeText(Verificacao.this, "Insira um código!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if(codigo.length() < 6) {
                    Toast.makeText(Verificacao.this, "Formato de código inválido!", Toast.LENGTH_SHORT).show();
                    return;
                }

                verificar_codigo(codigo);
            }
        });

        verificacao_voltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Verificacao.this, Entrada.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void verificar_codigo(String codigo) {
        if(codigo.equals("") && intent_celular.equals(""))
            Toast.makeText(Verificacao.this,"Falha ao verificar a sessão, tente novamento!", Toast.LENGTH_SHORT).show();
        else {
            try {
                PhoneAuthCredential credencial = PhoneAuthProvider.getCredential(verificacao_id, codigo);
                iniciar_sessao(credencial);
            } catch (Exception e) {
                Toast.makeText(Verificacao.this, "Credenciais inválidas", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void iniciar_sessao(PhoneAuthCredential credencial) {

        if(intent_conta.equals("conta_nova")) {

            // Insirindo um dialogo
            dialogo.setTitle("Criando conta");
            dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
            dialogo.setCanceledOnTouchOutside(false);

            // Mostrando o dialogo
            dialogo.show();

            autenticacao.signInWithCredential(credencial)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                // Cadastrando dados do usuario
                                FirebaseUser usuario = autenticacao.getCurrentUser();
                                assert usuario != null;
                                final String paciente_id = usuario.getUid();

                                referencia = FirebaseDatabase.getInstance().getReference("usuarios/pacientes").child(paciente_id);

                                HashMap<String, String> dados = new HashMap<>();

                                dados.put("id", paciente_id);
                                dados.put("nome", "null");
                                dados.put("apelido", "null");
                                dados.put("pac", "null");
                                dados.put("nascimento", "null");
                                dados.put("regiao", intent_regiao);
                                dados.put("provincia", "null");
                                dados.put("residencia", "null");
                                dados.put("bi", "null");
                                dados.put("sexo", "null");
                                dados.put("celular", intent_celular);
                                dados.put("estado", "null");
                                dados.put("imagem", "null");
                                dados.put("miniatura", "null");
                                dados.put("tipo_usuario", "Paciente");

                                referencia.setValue(dados).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            dialogo.dismiss();

                                            Intent intent = new Intent(Verificacao.this, Registo.class);
                                            intent.putExtra("paciente_id", paciente_id);
                                            intent.putExtra("celular", intent_celular);
                                            intent.putExtra("regiao", intent_regiao);
                                            startActivity(intent);
                                            finish();
                                        } else {

                                            dialogo.hide();

                                            Toast.makeText(Verificacao.this, "Falha ao criar a conta, tente novamente!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            } else {
                                dialogo.hide();
                                Toast.makeText(Verificacao.this, "Código inválido!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else if (intent_conta.equals("conta_existente")) {

            // Insirindo um dialogo
            dialogo.setTitle("Iniciando Sessão");
            dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
            dialogo.setCanceledOnTouchOutside(false);

            // Mostrando o dialogo
            dialogo.show();

            autenticacao.signInWithCredential(credencial)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {

                                dialogo.dismiss();

                                Intent intent = new Intent(Verificacao.this, Principal.class);
                                startActivity(intent);
                                finish();

                            } else {
                                dialogo.hide();
                                Toast.makeText(Verificacao.this, "Código inválido!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
    }

    @SuppressLint("SetTextI18n")
    private void enviar_codigo_verificacao(String celular) {
        verificacao_texto.setText("Aguardando a detecção automática de um SMS enviado para " + celular + ".");
        progresso.setVisibility(View.VISIBLE);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                celular,
                60,
                TimeUnit.SECONDS,
                this,
                chamar_novamente
        );
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            chamar_novamente = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificacao_id = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String codigo = phoneAuthCredential.getSmsCode();
            if (codigo != null) {
                verificacao_codigo.setText(codigo);
                iniciar_sessao(phoneAuthCredential);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(Verificacao.this, "Falha ao iniciar a sessão, verifique a sua rede!", Toast.LENGTH_SHORT).show();

        }

    };
}
