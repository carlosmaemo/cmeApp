package com.cmembondeiro.cme.actividades.sistemas;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cmembondeiro.cme.R;
import com.cmembondeiro.cme.actividades.menus.Principal;
import com.cmembondeiro.cme.componentes.Aplicacao;
import com.cmembondeiro.cme.componentes.Receptor;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Timer;
import java.util.TimerTask;

public class Carregamento extends AppCompatActivity implements Receptor.ReceptorListener {

    private long tempo = 10000;
    private FirebaseAuth autenticacao;
    private Intent intent;
    private TextView falhaTxt1, falhaTxt2;
    private Timer timer = new Timer();
    private TimerTask tarefa_timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_carregamento);

        falhaTxt1 = findViewById(R.id.carregamento_txt1);
        falhaTxt2 = findViewById(R.id.carregamento_txt2);

    }

    private void verificarConexao() {
        boolean conectado = Receptor.isConnected();
        informar(conectado);
    }

    private void informar(boolean conectado) {
        if (conectado) {

            falhaTxt1.setVisibility(View.INVISIBLE);
            falhaTxt2.setVisibility(View.INVISIBLE);

            autenticacao = FirebaseAuth.getInstance();
            FirebaseUser usuario_logado = autenticacao.getCurrentUser();

            if (usuario_logado != null) {
                Intent intent = new Intent(Carregamento.this, Principal.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
            else {

                if(timer != null) {

                    tarefa_timer = new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                            Intent intent = new Intent(Carregamento.this, Entrada.class);
                            startActivity(intent);
                            finish();
                        }
                    };
                    timer.schedule(tarefa_timer, tempo);
                }
            }

        } else {

            if(tarefa_timer != null) {
                tarefa_timer.cancel();
            }

            falhaTxt1.setVisibility(View.VISIBLE);
            falhaTxt2.setVisibility(View.VISIBLE);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Aplicacao.getInstance().setConnectivityListener(this);
    }

    @Override
    public void onNetworkConnectionChanged(boolean conectado) {
        informar(conectado);
    }

    @Override
    protected void onStart() {
        super.onStart();

        falhaTxt1.setVisibility(View.INVISIBLE);
        falhaTxt2.setVisibility(View.INVISIBLE);
        verificarConexao();

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
