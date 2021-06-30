package edu.pe.idat.app_qolca.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
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
import java.util.regex.Pattern;

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

        binding.btnRcrear.setOnClickListener(view -> {


            if(registrarUsuario(Constantes.URL_API_USUARIO_CREAR)){
                mostrarAlerta("Registro","Usuario creado exitoso");
            }

                String msj = "";
                boolean respuesta = true;
                if (binding.etSignupNombre.getText().toString().length() == 0) {
                    msj = "Ingrese el nombre";
                    respuesta = false;
                    mostrarAlerta("Nombre", msj);

                } else if (binding.etSignupApellido.getText().toString().length() == 0) {
                    msj = "Ingrese su apellido";
                    respuesta = false;
                    mostrarAlerta("Apellido", msj);
                } else if (binding.etSignupEmail.getText().toString().length() == 0) {
                    msj = "Ingrese su email";
                    respuesta = false;
                    mostrarAlerta("Email", msj);
                } else if (binding.etSignupEmail.getText().toString().length() > 0) {
                    Pattern pattern = Patterns.EMAIL_ADDRESS;
                    if (pattern.matcher(binding.etSignupEmail.getText().toString()).matches()) {
                        respuesta = true;
                    } else {
                        msj = "Su email " + binding.etSignupEmail.getText().toString() + " no es valido";
                        respuesta = false;
                        mostrarAlerta("correo", msj);
                    }
                } else if (binding.etSignupContrasena.getText().toString().length() == 0) {
                    msj = "Ingrese su correo";
                    respuesta = false;
                    mostrarAlerta("Email", msj);
                } else if (binding.etSignupContrasena.getText().toString().length() > 0) {
                    Pattern pattern = Patterns.EMAIL_ADDRESS;
                    if (pattern.matcher(binding.etSignupContrasena.getText().toString()).matches()) {
                        respuesta = true;
                    } else {
                        msj = "Su contraseña" + binding.etSignupContrasena.getText().toString() + " no es valido";
                        respuesta = false;
                        mostrarAlerta("contraseña", msj);
                    }
                } else {
                    respuesta = true;
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
        binding.btnRcrear.setEnabled(false);

       Map<String,String> parametros= new HashMap<>();
       parametros.put("nombre",binding.etSignupNombre.getText().toString());
        parametros.put("apellido",binding.etSignupApellido.getText().toString());
        parametros.put("email",binding.etSignupEmail.getText().toString());
        parametros.put("contrasenia",binding.etSignupContrasena.getText().toString());

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

