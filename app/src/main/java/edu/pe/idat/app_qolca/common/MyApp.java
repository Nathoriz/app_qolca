package edu.pe.idat.app_qolca.common;

import android.app.Application;
import android.content.Context;

public class MyApp extends Application {
    private static MyApp instancia;
    @Override
    public void onCreate(){
        instancia = this;
        super.onCreate();
    }

    public static MyApp getInstance(){
        return instancia;
    }

    public static Context getContext(){
        return instancia;
    }
}

