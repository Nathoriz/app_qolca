package edu.pe.idat.app_qolca.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        binding.btnLogin.setOnClickListener(view -> {


            autentificacion();
            binding.btnLogin.setEnabled(true);


        });

        binding.tvLoginRegistrar.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this,SignUpActivity.class));


        });

    }

    private void autentificacion() {
        RequestQueue colapeticiones = Volley.newRequestQueue(this);
        binding.btnLogin.setEnabled(false);

        Map<String,String> parametros= new HashMap<>();
        parametros.put("email",binding.etLoginEmail.getText().toString());
        parametros.put("contrasenia",binding.etLoginContrasenia.getText().toString());

        JSONObject jsonObjectParametro = new JSONObject(parametros);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                Constantes.URL_API_USUARIO_LOGIN,
                jsonObjectParametro,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            if(response.getString("mensaje").equals("Credenciales válidas")){
                                JSONObject user = response.getJSONObject("Usuario");
                                SharedPreferenceManager.setSomeIntValue(Constantes.PREF_ID,user.getInt("id"));
                                SharedPreferenceManager.setSomeStringValue(Constantes.PREF_NOMBRE,user.getString("nombre"));
                                SharedPreferenceManager.setSomeStringValue(Constantes.PREF_APELLIDO,user.getString("apellido"));
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            }

                        } catch (JSONException ex) {
                            mensaje(ex.toString() + ":'C");
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
                        Toast.makeText(getApplicationContext(),testV.getString("message"),Toast.LENGTH_LONG).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    }

                }
            }
        }
        );
        colapeticiones.add(request);
    }

    private void mensaje(String m){
        Toast.makeText(getApplicationContext(),
                m,Toast.LENGTH_LONG).show();
    }
}