package com.cmembondeiro.cme.actividades.sistemas;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Spinner;

import com.cmembondeiro.cme.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class Definicoes extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference referencia, referencia_definicao;
    private FirebaseAuth autenticacao;
    private String atual_id;
    private ProgressDialog dialogo;
    private Spinner lista_provincias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_definicoes);


        toolbar = findViewById(R.id.definicao_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}
