package edu.pe.idat.app_qolca.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.pe.idat.app_qolca.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        final Intent LoginActivityIntent = new Intent(this,LoginActivity.class);
        Thread timer = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3500);
                } catch (InterruptedException ex) {
                } finally {
                    startActivity(LoginActivityIntent);
                    finish();
                }
            }
        };
        timer.start();
    }
}