package edu.pe.idat.app_qolca.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import edu.pe.idat.app_qolca.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnLogin.setOnClickListener(view->{
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
        });
        binding.tvLoginRegistrar.setOnClickListener(view->{
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
        });
    }


}