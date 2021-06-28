package edu.pe.idat.app_qolca.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    public SignUpActivity() {
    }


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnLogin.setOnClickListener(view -> {

            if(registrarUsuario(Constantes.URL_API_USUARIO_CREAR)){
                mostrarAlerta("Registro","Usuario creado exitoso");
            }

        });


        binding.tvSignupLogin.setOnClickListener(view -> {
            startActivity(new Intent(SignUpActivity.this,LoginActivity.class));


        });



    }
    public void mostrarAlerta(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setTitle(titulo);

        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean registrarUsuario(String urlApiUsuarioCrear) {
        RequestQueue colapeticiones = Volley.newRequestQueue(this);
        boolean rsp=false;
        binding.btnLogin.setEnabled(false);

        Map<String,String> parametros= new HashMap<>();
        parametros.put("apellido",binding.etSignupApellido.getText().toString());
        parametros.put("contrasenia",binding.etSignupContrasena.getText().toString());

        parametros.put("email",binding.etSignupEmail.getText().toString());
        parametros.put("nombre",binding.etSignupNombre.getText().toString());

        rsp=true;
        JSONObject jsonObjectParametro = new JSONObject(parametros);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                urlApiUsuarioCrear,
                jsonObjectParametro,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        colapeticiones.add(request);
        return rsp;
    }

    private void mensaje(String m){
        Toast.makeText(getApplicationContext(),
                m,Toast.LENGTH_LONG).show();
    }

}

