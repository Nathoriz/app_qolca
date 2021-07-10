package edu.pe.idat.app_qolca.view;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Toast;


import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
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
                registrarUsuario(Constantes.URL_API_USUARIO_CREAR);
                binding.btnRcrear.setEnabled(true);
                clear();
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

    private void registrarUsuario(String urlApiUsuarioCrear) {
        RequestQueue colapeticiones = Volley.newRequestQueue(this);
        binding.btnRcrear.setEnabled(false);

       Map<String,String> parametros= new HashMap<>();
       parametros.put("nombre",binding.etSignupNombre.getText().toString());
        parametros.put("apellido",binding.etSignupApellido.getText().toString());
        parametros.put("email",binding.etSignupEmail.getText().toString());
        parametros.put("contrasenia",binding.etSignupContrasena.getText().toString());

        JSONObject jsonObjectParametro = new JSONObject(parametros);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                urlApiUsuarioCrear,
                jsonObjectParametro,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            mostrarAlerta("Wiiiii :D",response.getString("Mensaje"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                if (networkResponse != null && networkResponse.data != null) {
                    byte[] datos = networkResponse.data;
                    try {
                        JSONObject testV=new JSONObject(new String(datos));
                        mostrarAlerta("Ups (◕︵◕)",testV.getString("message"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }

        });
        colapeticiones.add(request);
    }

    public void clear(){
        binding.etSignupNombre.setText("");
        binding.etSignupApellido.setText("");
        binding.etSignupEmail.setText("");
        binding.etSignupContrasena.setText("");
    }
}

