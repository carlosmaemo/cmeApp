package com.cmembondeiro.cme.actividades.diversos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.cmembondeiro.cme.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Sobre extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference referencia;
    private TextView sobre_data, sobre_inf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sobre);

        toolbar = findViewById(R.id.sobre_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sobre");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sobre_data = findViewById(R.id.sobre_data);
        sobre_inf = findViewById(R.id.sobre_inf);

        referencia = FirebaseDatabase.getInstance().getReference().child("informacoes").child("-112019-sobre");
        referencia.keepSynced(true);
        referencia.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String data = dataSnapshot.child("data").getValue().toString();
                String informacao = dataSnapshot.child("informacao").getValue().toString();

                if(informacao.contains("_n")){
                    String novo_texto = informacao.replace("_n","\n");
                    sobre_inf.setText(novo_texto);
                }
                else {
                    sobre_inf.setText(informacao);
                }

                sobre_data.setText("Última modificação: " + data);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
