package com.cmembondeiro.cme.actividades.diversos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.TextView;

import com.cmembondeiro.cme.R;
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

public class Politica extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference referencia;
    private TextView politica_data, politica_inf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_politica);

        toolbar = findViewById(R.id.politica_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Política de qualidade");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        politica_data = findViewById(R.id.politica_data);
        politica_inf = findViewById(R.id.politica_inf);

        referencia = FirebaseDatabase.getInstance().getReference().child("informacoes").child("-112019-politica");
        referencia.keepSynced(true);
        referencia.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String data = dataSnapshot.child("data").getValue().toString();
                String informacao = dataSnapshot.child("informacao").getValue().toString();

                if(informacao.contains("_n")){
                    String novo_texto = informacao.replace("_n","\n");
                    politica_inf.setText(novo_texto);
                }
                else {
                    politica_inf.setText(informacao);
                }

                politica_data.setText("Última modificação: " + data);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
