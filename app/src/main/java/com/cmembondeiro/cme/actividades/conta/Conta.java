package com.cmembondeiro.cme.actividades.conta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.consulta.Consulta;
import com.cmembondeiro.cme.actividades.consulta.Pendente;
import com.cmembondeiro.cme.actividades.menus.Principal;
import com.cmembondeiro.cme.actividades.sistemas.Entrada;
import com.cmembondeiro.cme.actividades.sistemas.Registo;
import com.cmembondeiro.cme.entidades.Codigos;
import com.cmembondeiro.cme.entidades.Pacientes;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import id.zelory.compressor.Compressor;

public class Conta extends AppCompatActivity {

    private static final int PEGAR_GALERIA = 1;

    private Toolbar toolbar;
    private DatabaseReference referencia, referencia_notificacao;
    private FirebaseAuth autenticacao;
    private StorageReference armazenamento;
    private String atual_id, intent_nome;
    private ProgressDialog dialogo;
    private Pacientes paciente;

    private Spinner lista_provincias;

    private EditText input_nome, input_apelido, input_estado, input_residencia, input_pac, input_bi, input_nascimento;
    private ImageView input_imagem;
    private RadioButton input_sexo_masculino, input_sexo_feminino;
    private String barra_nome, prov;
    private Button btn_terminar_sessao;
    private AlertDialog alerta;

    private String db_id, db_nome, db_apelido, db_pac, db_nascimento, db_regiao, db_provincia, db_residencia, db_bi, db_sexo, db_celular, db_estado, db_imagem, db_miniatura, db_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conta);

        toolbar = findViewById(R.id.conta_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        input_nome = findViewById(R.id.conta_input_nome);
        input_apelido = findViewById(R.id.conta_input_apelido);
        input_estado = findViewById(R.id.conta_input_estado);
        input_residencia = findViewById(R.id.conta_input_residencia);
        input_pac = findViewById(R.id.conta_input_pac);
        input_bi = findViewById(R.id.conta_input_bi);
        input_nascimento = findViewById(R.id.conta_input_nascimento);
        input_imagem = findViewById(R.id.conta_imagem);
        input_sexo_feminino = findViewById(R.id.conta_btn_feminino);
        input_sexo_masculino = findViewById(R.id.conta_btn_masculino);
        lista_provincias = findViewById(R.id.conta_spinner_provincia);
        btn_terminar_sessao = findViewById(R.id.conta_sessao_terminar);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();
        armazenamento = FirebaseStorage.getInstance().getReference();

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        atual_id = usuario.getUid();

        referencia = FirebaseDatabase.getInstance().getReference().child("usuarios").child("pacientes").child(atual_id);
        referencia.keepSynced(true);
        referencia.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String id = dataSnapshot.child("id").getValue().toString();
                String nome = dataSnapshot.child("nome").getValue().toString();
                String apelido = dataSnapshot.child("apelido").getValue().toString();
                final String imagem = dataSnapshot.child("imagem").getValue().toString();
                final String miniatura = dataSnapshot.child("miniatura").getValue().toString();
                String estado = dataSnapshot.child("estado").getValue().toString();
                String regiao = dataSnapshot.child("regiao").getValue().toString();
                String provincia = dataSnapshot.child("provincia").getValue().toString();
                String residencia = dataSnapshot.child("residencia").getValue().toString();
                String pac = dataSnapshot.child("pac").getValue().toString();
                String bi = dataSnapshot.child("bi").getValue().toString();
                String celular = dataSnapshot.child("celular").getValue().toString();
                String nascimento = dataSnapshot.child("nascimento").getValue().toString();
                String sexo = dataSnapshot.child("sexo").getValue().toString();
                String usuario = dataSnapshot.child("tipo_usuario").getValue().toString();

                if(!provincia.equals("null")) {
                    selecionar_provincias(regiao, provincia);
                }

                getSupportActionBar().setTitle(nome + " " + apelido);

                if (regiao.equals("Moçambique")) {
                    lista_provincias.setAdapter(new ArrayAdapter<String>(Conta.this, android.R.layout.simple_spinner_dropdown_item, Codigos.nomes_provincia_moz));
                    lista_provincias.setSelection(((ArrayAdapter<String>) lista_provincias.getAdapter()).getPosition(provincia));
                    lista_provincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            prov = Codigos.nomes_provincia_moz[lista_provincias.getSelectedItemPosition()];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if (regiao.equals("Portugal")) {
                    lista_provincias.setAdapter(new ArrayAdapter<String>(Conta.this, android.R.layout.simple_spinner_dropdown_item, Codigos.nomes_provincia_por));
                    lista_provincias.setSelection(((ArrayAdapter<String>) lista_provincias.getAdapter()).getPosition(provincia));
                    lista_provincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            prov = Codigos.nomes_provincia_por[lista_provincias.getSelectedItemPosition()];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } else if (regiao.equals("Brasil")) {
                    lista_provincias.setAdapter(new ArrayAdapter<String>(Conta.this, android.R.layout.simple_spinner_dropdown_item, Codigos.nomes_provincia_bra));
                    lista_provincias.setSelection(((ArrayAdapter<String>) lista_provincias.getAdapter()).getPosition(provincia));
                    lista_provincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            prov = Codigos.nomes_provincia_bra[lista_provincias.getSelectedItemPosition()];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                }

                db_id = id;
                db_nome = nome;
                db_apelido = apelido;
                db_imagem = imagem;
                db_miniatura = miniatura;
                db_estado = estado;
                db_regiao = regiao;
                db_provincia = provincia;
                db_residencia = residencia;
                db_pac = pac;
                db_bi = bi;
                db_celular = celular;
                db_nascimento = nascimento;
                db_sexo = sexo;
                db_usuario = usuario;

                if (sexo.equals("Feminino")) {
                    input_sexo_feminino.setChecked(true);
                    input_sexo_masculino.setChecked(false);
                } else {
                    input_sexo_feminino.setChecked(false);
                    input_sexo_masculino.setChecked(true);
                }

                if (nome.equals("null")) {
                    input_nome.setText("");
                } else {
                    input_nome.setText(nome);
                }

                if (apelido.equals("null")) {
                    input_apelido.setText("");
                } else {
                    input_apelido.setText(apelido);
                }

                if (estado.equals("null")) {
                    input_estado.setText("");
                } else {
                    input_estado.setText(estado);
                }

                if (residencia.equals("null")) {
                    input_residencia.setText("");
                } else {
                    input_residencia.setText(residencia);
                }

                if (pac.equals("null")) {
                    input_pac.setText("");
                } else {
                    input_pac.setText(pac);
                }

                if (bi.equals("null")) {
                    input_bi.setText("");
                } else {
                    input_bi.setText(bi);
                }

                if (nascimento.equals("null")) {
                    input_nascimento.setText("");
                } else {
                    input_nascimento.setText(nascimento);
                }

                // Picasso.get().load(miniatura).placeholder(R.drawable.default_avatar).into(perfil_imagem);

                if (!miniatura.equals("null")) {

                    Picasso.get().load(miniatura).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(input_imagem, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(miniatura).placeholder(R.drawable.default_avatar).into(input_imagem);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btn_terminar_sessao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Conta.this);

                builder.setTitle("Terminar sessão");
                builder.setMessage("Pretende mesmo terminar a sessão neste dispositivo?");

                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {

                        dialogo = new ProgressDialog(Conta.this);
                        dialogo.setTitle("Finalizando sessão");
                        dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
                        dialogo.setCanceledOnTouchOutside(false);
                        dialogo.show();

                        FirebaseDatabase.getInstance().getReference("notificacoes").child(atual_id).child("-00291f-bem-vindo").removeValue();

                        referencia_notificacao = FirebaseDatabase.getInstance().getReference("notificacoes").child(atual_id).child("-00291f-bem-vindo");

                        HashMap<String, String> dados = new HashMap<>();
                        dados.put("id", "-00291f-bem-vindo");
                        dados.put("paciente_id", atual_id);

                        if (db_sexo.equals("Feminino")) {
                            dados.put("titulo", "Bem-vinda de volta!");
                        } else {
                            dados.put("titulo", "Bem-vindo de volta!");
                        }

                        dados.put("descricao", "Olá " + db_nome + ", estávamos esperando por você.");


                        referencia_notificacao.setValue(dados).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    autenticacao.signOut();
                                    dialogo.dismiss();
                                    Intent intent = new Intent(Conta.this, Entrada.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else {
                                    autenticacao.signOut();
                                    dialogo.hide();
                                    Intent intent = new Intent(Conta.this, Entrada.class);
                                    startActivity(intent);
                                    finish();
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

    private void selecionar_provincias(String regiao, String provincia) {

        if (regiao.equals("Moçambique")) {
            lista_provincias.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Codigos.nomes_provincia_moz));
            lista_provincias.setSelection(((ArrayAdapter<String>) lista_provincias.getAdapter()).getPosition(provincia));
            lista_provincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    prov = Codigos.nomes_provincia_moz[lista_provincias.getSelectedItemPosition()];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else if (regiao.equals("Portugal")) {
            lista_provincias.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Codigos.nomes_provincia_por));
            lista_provincias.setSelection(((ArrayAdapter<String>) lista_provincias.getAdapter()).getPosition(provincia));
            lista_provincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    prov = Codigos.nomes_provincia_por[lista_provincias.getSelectedItemPosition()];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else if (regiao.equals("Brasil")) {
            lista_provincias.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, Codigos.nomes_provincia_bra));
            lista_provincias.setSelection(((ArrayAdapter<String>) lista_provincias.getAdapter()).getPosition(provincia));
            lista_provincias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    prov = Codigos.nomes_provincia_bra[lista_provincias.getSelectedItemPosition()];
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        // Alterar a foto de perfil
        if (item.getItemId() == R.id.menu_conta_btn_img) {

            Intent galeria = new Intent();
            galeria.setType("image/*");
            galeria.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(galeria, "SELECIONE UMA IMAGEM"), PEGAR_GALERIA);

        } else if (item.getItemId() == R.id.menu_conta_btn_salvar) {

            if (input_nome.getText().toString().isEmpty() || input_apelido.getText().toString().isEmpty()) {
                Toast.makeText(Conta.this, "Preencha todos os campos obrigatórios!", Toast.LENGTH_SHORT).show();
            } else {

                barra_nome = input_nome + " " + input_apelido;

                paciente = new Pacientes();

                paciente.setId(db_id);
                paciente.setImagem(db_imagem);
                paciente.setMiniatura(db_miniatura);
                paciente.setRegiao(db_regiao);
                paciente.setCelular(db_celular);
                paciente.setTipo_usuario(db_usuario);

                paciente.setNome(input_nome.getText().toString());
                paciente.setApelido(input_apelido.getText().toString());

                paciente.setProvincia(prov);

                if (input_pac.getText().toString().equals("")) {
                    paciente.setPac("null");
                } else {
                    paciente.setPac(input_pac.getText().toString());
                }

                if (input_nascimento.getText().toString().equals("")) {
                    paciente.setNascimento("null");
                } else {
                    paciente.setNascimento(input_nascimento.getText().toString());
                }

                if (input_residencia.getText().toString().equals("")) {
                    paciente.setResidencia("null");
                } else {
                    paciente.setResidencia(input_residencia.getText().toString());
                }


                if (input_bi.getText().toString().equals("")) {
                    paciente.setBi("null");
                } else {
                    paciente.setBi(input_bi.getText().toString());
                }
                if (input_estado.getText().toString().equals("")) {
                    paciente.setEstado("null");
                } else {
                    paciente.setEstado(input_estado.getText().toString());
                }

                if (input_sexo_feminino.isChecked()) {
                    paciente.setSexo("Feminino");
                } else {
                    paciente.setSexo("Masculino");
                }

                alterar();

            }

        } else if (item.getItemId() == android.R.id.home) {
            finish();

            return true;
        }

        return false;
    }

    private void alterar() {

        //referencia.setValue(paciente);

        dialogo = new ProgressDialog(Conta.this);
        dialogo.setTitle("Alterando");
        dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
        dialogo.setCanceledOnTouchOutside(false);
        dialogo.show();

        referencia.setValue(paciente).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    dialogo.dismiss();
                } else {

                    dialogo.hide();

                    Toast.makeText(Conta.this, "Falha na alteração dos dados!", Toast.LENGTH_SHORT).show();

                }
            }
        });

        dialogo.dismiss();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_conta, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PEGAR_GALERIA && resultCode == RESULT_OK) {

            Uri imagemUri = data.getData();

            CropImage.activity(imagemUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                // Progresso
                dialogo = new ProgressDialog(Conta.this);
                dialogo.setTitle("Carregando");
                dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
                dialogo.setCanceledOnTouchOutside(false);
                dialogo.show();

                Uri resultUri = result.getUri();

                File miniatura_caminho = new File(resultUri.getPath());

                Bitmap miniatura_bitmap = null;

                try {

                    miniatura_bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(miniatura_caminho);

                } catch (IOException e) {

                    e.printStackTrace();

                }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                miniatura_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                final byte[] miniatura_byte = baos.toByteArray();

                final StorageReference caminho_imagem = armazenamento.child("fotografias").child(atual_id).child("original/perfil").child(atual_id + ".jpg");
                final StorageReference caminho_miniatura = armazenamento.child("fotografias").child(atual_id).child("miniatura/perfil").child(atual_id + ".jpg");

                UploadTask uploadTask1 = caminho_miniatura.putFile(resultUri);

                Task<Uri> urlTask1 = uploadTask1.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return caminho_miniatura.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            final Uri downloadUri1 = task.getResult();

                            final UploadTask uploadTask2 = caminho_imagem.putBytes(miniatura_byte);

                            Task<Uri> urlTask2 = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return caminho_imagem.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {

                                        Uri downloadUri2 = task.getResult();

                                        Map actualizar_hashMap = new HashMap<>();

                                        actualizar_hashMap.put("imagem", downloadUri1.toString());
                                        actualizar_hashMap.put("miniatura", downloadUri2.toString());
                                        referencia.updateChildren(actualizar_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                dialogo.dismiss();

                                            }
                                        });

                                    } else {
                                        Toast.makeText(Conta.this, "Falha no carregamento, tente novamente!", Toast.LENGTH_SHORT).show();
                                        dialogo.dismiss();
                                    }
                                }
                            });


                        } else {
                            Toast.makeText(Conta.this, "Falha no carregamento, tente novamente!", Toast.LENGTH_SHORT).show();
                            dialogo.dismiss();
                        }
                    }
                });


            }

        }

    }

    public static String aleatorio() {

        Random gerador = new Random();

        StringBuilder stringBuilder_aleatorio = new StringBuilder();

        int tamanho_aleatorio = gerador.nextInt(20);

        char tempChar;

        for (int i = 0; i < tamanho_aleatorio; i++) {

            tempChar = (char) (gerador.nextInt(96) + 32);

            stringBuilder_aleatorio.append(tempChar);

        }

        return stringBuilder_aleatorio.toString();
    }


}
