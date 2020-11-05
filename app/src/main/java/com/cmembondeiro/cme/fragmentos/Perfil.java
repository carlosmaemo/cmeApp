package com.cmembondeiro.cme.fragmentos;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class Perfil extends Fragment {

    private DatabaseReference referencia;
    private FirebaseAuth autenticacao;

    private View view;
    private String atual_id;

    private TextView perfil_nome, perfil_celular, perfil_estado, perfil_localizacao, perfil_pac, perfil_nascimento;
    private ImageView perfil_imagem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragmento_perfil, container, false);

        perfil_imagem = view.findViewById(R.id.frag_perfil_imagem);
        perfil_nome = view.findViewById(R.id.frag_perfil_nome);
        perfil_celular = view.findViewById(R.id.frag_perfil_celular);
        perfil_estado = view.findViewById(R.id.frag_perfil_estado);
        perfil_localizacao = view.findViewById(R.id.frag_perfil_localizacao);
        perfil_pac = view.findViewById(R.id.frag_perfil_pac);
        perfil_nascimento = view.findViewById(R.id.frag_perfil_nascimento);
        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        atual_id = usuario.getUid();

        referencia = FirebaseDatabase.getInstance().getReference().child("usuarios").child("pacientes").child(atual_id);
        referencia.keepSynced(true);

        referencia.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String nome = dataSnapshot.child("nome").getValue().toString();
                String apelido = dataSnapshot.child("apelido").getValue().toString();
                final String miniatura = dataSnapshot.child("miniatura").getValue().toString();
                String celular = dataSnapshot.child("celular").getValue().toString();
                String estado = dataSnapshot.child("estado").getValue().toString();
                String provincia = dataSnapshot.child("provincia").getValue().toString();
                String regiao = dataSnapshot.child("regiao").getValue().toString();
                String residencia = dataSnapshot.child("residencia").getValue().toString();
                String pac = dataSnapshot.child("pac").getValue().toString();
                String nascimento = dataSnapshot.child("nascimento").getValue().toString();
                String sexo = dataSnapshot.child("sexo").getValue().toString();

                perfil_nome.setText(nome + " " + apelido);
                perfil_celular.setText(celular);

                if(estado.equals("null")) {
                    perfil_estado.setText("");
                }
                else {
                    perfil_estado.setText(estado);
                }

                if(regiao.equals("null") || provincia.equals("null") || residencia.equals("null")) {
                    perfil_localizacao.setText("");
                }
                else {
                    perfil_localizacao.setText(regiao + ", " + provincia + ", " + residencia);
                    perfil_localizacao.setPaintFlags(perfil_localizacao.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                }

                if(pac.equals("null")) {
                    perfil_pac.setText("");
                }
                else {
                    perfil_pac.setText(pac + " | " + sexo);
                }

                if(nascimento.equals("null")) {
                    perfil_nascimento.setText("");
                }
                else {
                    perfil_nascimento.setText(nascimento);
                }

                //Picasso.get().load(miniatura).placeholder(R.drawable.default_avatar).into(perfil_imagem);
                if (!miniatura.equals("null")) {

                    Picasso.get().load(miniatura).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.default_avatar).into(perfil_imagem, new Callback() {
                        @Override
                        public void onSuccess() {


                        }

                        @Override
                        public void onError(Exception e) {

                            Picasso.get().load(miniatura).placeholder(R.drawable.default_avatar).into(perfil_imagem);

                        }
                    });
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;

    }
}
