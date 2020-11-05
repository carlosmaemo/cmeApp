package com.cmembondeiro.cme.componentes;

import android.app.Application;

public class Aplicacao extends Application {

    private static Aplicacao mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized Aplicacao getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(Receptor.ReceptorListener listener) {
        Receptor.receptorListener = listener;
    }

}
