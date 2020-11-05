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

public class Termos extends AppCompatActivity {

    private Toolbar toolbar;
    private DatabaseReference referencia;
    private TextView termos_data, termos_inf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termos);

        toolbar = findViewById(R.id.termos_barra);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Termos de uso");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        termos_data = findViewById(R.id.termos_data);
        termos_inf = findViewById(R.id.termos_inf);

        referencia = FirebaseDatabase.getInstance().getReference().child("informacoes").child("-112019-termos");
        referencia.keepSynced(true);
        referencia.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String data = dataSnapshot.child("data").getValue().toString();
                String informacao = dataSnapshot.child("informacao").getValue().toString();

                if(informacao.contains("_n")){
                    String novo_texto = informacao.replace("_n","\n");
                    termos_inf.setText(novo_texto);
                }
                else {
                    termos_inf.setText(informacao);
                }

                termos_data.setText("Última modificação: " + data);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
