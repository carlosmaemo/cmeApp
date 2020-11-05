/*
......................................................
Desenvolvido por Eng. Maemo, Carlos
CELL GAME, EI | carlosmaemo@outlook.com | (+258) 840 584 831 | www.cellgame.tk
Centro Médico Embondeiro, Lda. Copyright (C)2019
Gerência de Tecnologias de Informação
......................................................
*/

package com.cmembondeiro.cme.actividades.sistemas;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.menus.Principal;
import com.cmembondeiro.cme.entidades.Codigos;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Entrada extends AppCompatActivity {

    private EditText input_celular, input_senha;
    private Spinner lista_codigos;
    private Button btn_continuar;
    private ProgressDialog dialogo;
    //private AlertDialog.Builder prompt_senha;
    //private AlertDialog dialogo_senha;
    private String celular;

    private DatabaseReference referencia;

    private boolean ctrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entrada);

        referencia = FirebaseDatabase.getInstance().getReference();

        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Iniciando sessão");
        dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
        dialogo.setCanceledOnTouchOutside(false);

//        prompt_senha = new AlertDialog.Builder(this);
//        prompt_senha.setTitle("Senha");
//        prompt_senha.setIcon(R.drawable.ic_lock_preto);
//        prompt_senha.setMessage("Conta criptografada, insira sua senha para continuar");

        LinearLayout layout_prompt = new LinearLayout(this);
        layout_prompt.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams parametros = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        parametros.setMargins(300, 0, 300, 0);

        input_senha = new EditText(this);
        input_senha.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
        input_senha.setGravity(Gravity.CENTER);
        input_senha.setInputType(InputType.TYPE_CLASS_NUMBER |
                InputType.TYPE_NUMBER_FLAG_DECIMAL);

        layout_prompt.addView(input_senha, parametros);

//        prompt_senha.setView(layout_prompt);
//
//        prompt_senha.setPositiveButton("Login", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                Intent intent = new Intent(Entrada.this, Verificacao.class);
//                intent.putExtra("celular", celular);
//                startActivity(intent);
//                finish();
//
//            }
//        });
//
//        prompt_senha.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialogo.dismiss();
//            }
//        });
//
//        dialogo_senha = prompt_senha.create();

        btn_continuar = findViewById(R.id.login_btn_continuar);
        lista_codigos = findViewById(R.id.login_spinner_codigos);
        input_celular = findViewById(R.id.login_input_celular);

        lista_codigos.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Codigos.nomes_regiao));

        lista_codigos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String codigo = Codigos.codigos_regiao[lista_codigos.getSelectedItemPosition()];

                if(codigo.equals("258")) {
                    input_celular.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
                }
                else if(codigo.equals("351")) {
                    input_celular.setFilters(new InputFilter[] { new InputFilter.LengthFilter(9) });
                }
                else if(codigo.equals("55")) {
                    input_celular.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10) });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Adicionando acção no botão para iniciar a sessão
        btn_continuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verificando se todos campos estão preenchidos
                if (!input_celular.getText().toString().isEmpty()) {

                    final String codigo = Codigos.codigos_regiao[lista_codigos.getSelectedItemPosition()];
                    final String regiao = Codigos.nomes_regiao[lista_codigos.getSelectedItemPosition()];
                    celular = "+" + codigo + input_celular.getText().toString();


                    referencia.child("usuarios").child("pacientes").orderByChild("celular").equalTo(celular).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists() == false ) {
                                ctrl = true;
                                Intent intent = new Intent(Entrada.this, Verificacao.class);
                                intent.putExtra("celular", celular);
                                intent.putExtra("regiao", regiao);
                                intent.putExtra("codigo", codigo);
                                intent.putExtra("conta", "conta_nova");
                                startActivity(intent);
                                finish();
                            }
                            else if(dataSnapshot.exists() == true && ctrl == false) {

                                // Chamar dialogo de senha
                                //dialogo_senha.show();

                                Intent intent = new Intent(Entrada.this, Verificacao.class);
                                intent.putExtra("celular", celular);
                                intent.putExtra("regiao", regiao);
                                intent.putExtra("codigo", codigo);
                                intent.putExtra("conta", "conta_existente");
                                startActivity(intent);
                                finish();

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(Entrada.this, error.toException().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(Entrada.this, "Insira o número de celular para continuar!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}