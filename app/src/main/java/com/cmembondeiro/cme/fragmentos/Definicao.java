package com.cmembondeiro.cme.fragmentos;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.conta.Conta;
import com.cmembondeiro.cme.actividades.menus.Principal;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Definicao extends Fragment {

    private DatabaseReference referencia;
    private FirebaseAuth autenticacao;

    private Button btn_geral, btn_conta;
    private String nome_completo;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragmento_definicao, container, false);

        autenticacao = FirebaseAuth.getInstance();

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        final String paciente_id = usuario.getUid();

        referencia = FirebaseDatabase.getInstance().getReference().child("usuarios/pacientes").child(paciente_id);

        referencia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nome = dataSnapshot.child("nome").getValue().toString();
                String apelido = dataSnapshot.child("apelido").getValue().toString();
                nome_completo = nome + " " + apelido;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        LinearLayoutManager gerenciador_layout = new LinearLayoutManager(getContext());
        gerenciador_layout.setReverseLayout(true);
        gerenciador_layout.setStackFromEnd(true);

        btn_conta = view.findViewById(R.id.frag_definicao_conta_btn);

//        btn_geral = view.findViewById(R.id.frag_definicao_geral_btn);
//        btn_geral.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        btn_conta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), Conta.class);
                intent.putExtra("atual_nome", nome_completo);
                startActivity(intent);
            }
        });

        return view;
    }
}