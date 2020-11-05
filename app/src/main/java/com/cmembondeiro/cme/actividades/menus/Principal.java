/*
......................................................
Desenvolvido por Eng. Maemo, Carlos
CELL GAME, EI | cellgame@email.com | www.cellgame.tk
Centro Médico Embondeiro, Lda. Copyright (C)2019
Gerência de Tecnologias de Informação
......................................................
*/

package com.cmembondeiro.cme.actividades.menus;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.cmembondeiro.cme.actividades.consulta.Desmarcado;
import com.cmembondeiro.cme.actividades.consulta.Pendente;
import com.cmembondeiro.cme.actividades.diversos.Sobre;
import com.cmembondeiro.cme.actividades.diversos.Politica;
import com.cmembondeiro.cme.actividades.diversos.Termos;
import com.cmembondeiro.cme.actividades.sistemas.Carregamento;
import com.cmembondeiro.cme.fragmentos.Agendamento;
import com.cmembondeiro.cme.fragmentos.Definicao;
import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.sistemas.Entrada;
import com.cmembondeiro.cme.fragmentos.Chat;
import com.cmembondeiro.cme.fragmentos.Notificacao;
import com.cmembondeiro.cme.fragmentos.Perfil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class Principal extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar barra_ferramenta;
    private ProgressDialog dialogo;
    private DrawerLayout desenho;

    private long tempo = 5000;

    private FirebaseAuth autenticacao;
    private DatabaseReference referencia_usuario_atual;
    private FirebaseUser usuario;

    private String atual_id;
    private TextView barra_nome, barra_inf;
    private ImageView barra_icon;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        barra_ferramenta = findViewById(R.id.principal_toolbar);
        setSupportActionBar(barra_ferramenta);
        getSupportActionBar().setTitle("CME App");

        dialogo = new ProgressDialog(this);
        dialogo.setTitle("Carregando");
        dialogo.setMessage("Porfavor aguarde enquanto a operação é realizada!");
        dialogo.setCanceledOnTouchOutside(false);

        autenticacao = FirebaseAuth.getInstance();

        usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        atual_id = usuario.getUid();

        desenho = findViewById(R.id.principal_layout);
        final NavigationView navegador_view = findViewById(R.id.principal_navegador_view);
        navegador_view.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle alternancia = new ActionBarDrawerToggle(this, desenho, barra_ferramenta, R.string.txt_barra_navegador_aberto, R.string.txt_barra_navegador_fechado);
        desenho.addDrawerListener(alternancia);
        alternancia.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.principal_fragment_recipiente, new Notificacao()).commit();
            navegador_view.setCheckedItem(R.id.menu_navegador_notificacao);
        }

        View desenho_view = navegador_view.getHeaderView(0);
        barra_nome = (TextView) desenho_view.findViewById(R.id.barra_nome);
        barra_icon = (ImageView) desenho_view.findViewById(R.id.barra_foto);
        barra_inf = (TextView) desenho_view.findViewById(R.id.barra_celular);

        referencia_usuario_atual = FirebaseDatabase.getInstance().getReference().child("usuarios/pacientes").child(atual_id);
        referencia_usuario_atual.keepSynced(true);
        referencia_usuario_atual.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nome = dataSnapshot.child("nome").getValue().toString();
                String apelido = dataSnapshot.child("apelido").getValue().toString();
                String celular = dataSnapshot.child("celular").getValue().toString();
                String regiao = dataSnapshot.child("regiao").getValue().toString();
                String residencia = dataSnapshot.child("residencia").getValue().toString();
                String pac = dataSnapshot.child("pac").getValue().toString();
                final String imagem = dataSnapshot.child("imagem").getValue().toString();
                final String miniatura = dataSnapshot.child("miniatura").getValue().toString();
                barra_nome.setText(nome + " " + apelido);

                if (!regiao.equals("null")) {
                    barra_inf.setText(celular + " | " + regiao);
                }
                else {
                    barra_inf.setText(celular);
                }

                if (!miniatura.equals("null")) {

                    Picasso.get().load(miniatura).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(barra_icon, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get().load(miniatura).placeholder(R.drawable.default_avatar).into(barra_icon);
                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_navegador_notificacao:
                getSupportFragmentManager().beginTransaction().replace(R.id.principal_fragment_recipiente, new Notificacao()).commit();
                break;
            case R.id.menu_navegador_chat:
                getSupportFragmentManager().beginTransaction().replace(R.id.principal_fragment_recipiente, new Chat()).commit();
                break;
            case R.id.menu_navegador_consulta:
                getSupportFragmentManager().beginTransaction().replace(R.id.principal_fragment_recipiente, new Agendamento()).commit();
                break;
            case R.id.menu_navegador_perfil:
                getSupportFragmentManager().beginTransaction().replace(R.id.principal_fragment_recipiente, new Perfil()).commit();
                break;
            case R.id.menu_navegador_definicoes:
                getSupportFragmentManager().beginTransaction().replace(R.id.principal_fragment_recipiente, new Definicao()).commit();
                break;
        }

        desenho.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void onBackPressed() {
        if (desenho.isDrawerOpen(GravityCompat.START)) {
            desenho.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_principal, menu);

        MenuItem item01 = menu.findItem(R.id.main_btn_pendente);
        MenuItem item02 = menu.findItem(R.id.main_btn_desmarcado);
        MenuItem item03 = menu.findItem(R.id.main_btn_politica);
        MenuItem item04 = menu.findItem(R.id.main_btn_termos);
        MenuItem item05 = menu.findItem(R.id.main_btn_sobre);

        return true;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (autenticacao.getCurrentUser() != null) {

            //dialogo.show();
            atual_id = autenticacao.getCurrentUser().getUid();

//            Timer timer = new Timer();
//            TimerTask tarefa_timer = new TimerTask() {
//                @Override
//                public void run() {
//                    dialogo.dismiss();
//                }
//            };
//            timer.schedule(tarefa_timer, tempo);


        } else {
            Intent intent = new Intent(Principal.this, Entrada.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_btn_pendente) {

            // Abrir Agendamentos pendente
            Intent intent = new Intent(Principal.this, Pendente.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.main_btn_desmarcado) {

            // Abrir Agendamentos desmarcado
            Intent intent = new Intent(Principal.this, Desmarcado.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.main_btn_politica) {

            // Abrir Politicas
            Intent intent = new Intent(Principal.this, Politica.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.main_btn_termos) {

            // Abrir Termos
            Intent intent = new Intent(Principal.this, Termos.class);
            startActivity(intent);

        } else if (item.getItemId() == R.id.main_btn_sobre) {

            // Abrir Sobre
            Intent intent = new Intent(Principal.this, Sobre.class);
            startActivity(intent);

        }

        return true;
    }
}
