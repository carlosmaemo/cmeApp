package com.cmembondeiro.cme.fragmentos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.componentes.Email;
import com.cmembondeiro.cme.entidades.Mensagens;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Properties;

public class Chat extends Fragment {

    private RecyclerView lista_mensagem;
    private DatabaseReference referencia, referencia_chat, referencia_email, referencia_perfil;
    private Query referencia_filtro;
    private FirebaseAuth autenticacao;
    private ImageView enviar_mensagem;
    private EditText campo_mensagem;
    private TextView txt_vazio;
    private LinearLayoutManager gerenciador_layout;
    private String assunto, emissor, password, receptor, rodape, nome;

    private String paciente_id;

    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragmento_chat, container, false);

        enviar_mensagem = view.findViewById(R.id.frag_chat_btn_enviar);
        campo_mensagem = view.findViewById(R.id.frag_chat_campo_mensagem);

        autenticacao = FirebaseAuth.getInstance();
        referencia = FirebaseDatabase.getInstance().getReference();
        referencia_chat = FirebaseDatabase.getInstance().getReference().child("mensagens");
        referencia_chat.keepSynced(true);

        FirebaseUser usuario = autenticacao.getCurrentUser();
        assert usuario != null;
        paciente_id = usuario.getUid();

        referencia_filtro = referencia.child("mensagens").child(paciente_id).orderByChild("tempo");
        referencia_filtro.keepSynced(true);

        referencia_perfil = FirebaseDatabase.getInstance().getReference().child("usuarios").child("pacientes").child(paciente_id);
        referencia_perfil.keepSynced(true);

        referencia_perfil.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String perfil_nome = dataSnapshot.child("nome").getValue().toString();
                String perfil_apelido = dataSnapshot.child("apelido").getValue().toString();

                nome  = perfil_nome + " " + perfil_apelido;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        referencia_email = FirebaseDatabase.getInstance().getReference().child("informacoes").child("-012020-email");
        referencia_email.keepSynced(true);
        referencia_email.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String not_assunto = dataSnapshot.child("assunto").getValue().toString();
                String not_emissor = dataSnapshot.child("emissor").getValue().toString();
                String not_password = dataSnapshot.child("password").getValue().toString();
                String not_receptor = dataSnapshot.child("receptor").getValue().toString();
                String not_rodape = dataSnapshot.child("rodape").getValue().toString();

                assunto = not_assunto;
                emissor = not_emissor;
                password = not_password;
                receptor = not_receptor;
                rodape = not_rodape;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        gerenciador_layout = new LinearLayoutManager(getContext());
        gerenciador_layout.setReverseLayout(false);
        gerenciador_layout.setStackFromEnd(true);

        txt_vazio = view.findViewById(R.id.frag_chat_vazio);
        lista_mensagem = view.findViewById(R.id.frag_chat_lista);
        lista_mensagem.setHasFixedSize(true);
        lista_mensagem.setLayoutManager(gerenciador_layout);

        enviar_mensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviar_mensagem();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        txt_vazio.setVisibility(View.INVISIBLE);
        lista_mensagem.setVisibility(View.VISIBLE);
        pesquisar(referencia_filtro);

    }

    private void pesquisar(final Query query) {

                FirebaseRecyclerAdapter<Mensagens, Chat.ChatViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Mensagens, Chat.ChatViewHolder>(

                Mensagens.class,
                R.layout.layout_mensagem,
                Chat.ChatViewHolder.class,
                query

        ) {
            @Override
            protected void populateViewHolder(Chat.ChatViewHolder viewHolder, final Mensagens model, int position) {

                viewHolder.setMensagem(model.getMensagem());

                final String mensagem_id = getRef(position).getKey();

                if (model.getDe().equals("cme")) {

                    RelativeLayout.LayoutParams layoutParams_mensagem =
                            (RelativeLayout.LayoutParams) viewHolder.view_mensagem.getLayoutParams();

                    RelativeLayout.MarginLayoutParams layoutParams_layout =
                            (RelativeLayout.MarginLayoutParams) viewHolder.view_layout.getLayoutParams();

                    layoutParams_mensagem.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 9);
                    layoutParams_mensagem.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
                    layoutParams_mensagem.addRule(RelativeLayout.START_OF, 0);
                    layoutParams_mensagem.addRule(RelativeLayout.LEFT_OF, 0);

                    layoutParams_layout.leftMargin = 0;
                    layoutParams_layout.rightMargin = 100;

                    viewHolder.view_mensagem.setLayoutParams(layoutParams_mensagem);
                    viewHolder.view_mensagem.setBackgroundResource(R.drawable.background_chat_mensagem_clinica);

                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                        builder.setTitle("Detalhes");
                        builder.setMessage("Data de envio: " + model.getData());

                        if(model.getDe().equals("cme")) {

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }else {
                            builder.setPositiveButton("Apagar mensagem", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(final DialogInterface dialog, int which) {
                                    referencia.child("mensagens").child(paciente_id).child(mensagem_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                        }
                                    });
                                }
                            });

                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                        }

                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }
                });

            }
        };

        lista_mensagem.setAdapter(firebaseRecyclerAdapter);

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    txt_vazio.setVisibility(View.VISIBLE);
                    lista_mensagem.setVisibility(View.INVISIBLE);
                } else {
                    txt_vazio.setVisibility(View.INVISIBLE);
                    lista_mensagem.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView view_mensagem;
        RelativeLayout view_layout;

        public ChatViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
            view_layout = mView.findViewById(R.id.layout_mensagem_enviado);
        }

        void setMensagem(String mensagem) {
            view_mensagem = mView.findViewById(R.id.layout_mensagem_texto);
            view_mensagem.setText(mensagem);
        }

    }

    private void enviar_mensagem() {

        final String mensagem = campo_mensagem.getText().toString();

        if (!TextUtils.isEmpty(mensagem)) {

            DatabaseReference referencia_chat_push = referencia_chat.child(paciente_id).push();
            String push_chat_id = referencia_chat_push.getKey();

            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = new Date();

            Map dados = new HashMap();
            dados.put("mensagem", mensagem);
            dados.put("tempo", ServerValue.TIMESTAMP);
            dados.put("de", paciente_id);
            dados.put("deId", paciente_id);
            dados.put("id", push_chat_id);
            dados.put("usuario", "paciente");
            dados.put("data", dateFormat.format(date));

            campo_mensagem.setText("");

            referencia_chat_push.updateChildren(dados, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {

                    if(assunto.contains("_$nm")){
                        String novo_texto = assunto.replace("_$nm", nome);
                        assunto = novo_texto;
                    }

                    if(rodape.contains("_n")){
                        String novo_texto = rodape.replace("_n","\n");
                        rodape = novo_texto;
                    }

                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            final Email sender = new Email();
                            try {
                                sender.EnviarEmail(emissor, password, receptor, assunto, mensagem + "\n\n\n" + rodape);
                            } catch (Exception e) {
                            }
                        }

                    }).start();

                }
            });


        }

    }

}
