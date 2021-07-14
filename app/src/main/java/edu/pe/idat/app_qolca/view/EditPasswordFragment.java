package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.FragmentEditPasswordBinding;
import edu.pe.idat.app_qolca.model.Usuario;


public class EditPasswordFragment extends Fragment {

    private FragmentEditPasswordBinding binding;
    private Usuario usuario;

    public EditPasswordFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditPasswordBinding.inflate(inflater,container,false);
        obtenerDatos(Constantes.URL_API_USUARIO_ID+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
        binding.btnEditpasswordGuardar.setOnClickListener(view ->{
            if(validacion()){
            actualizarContrasenia(Constantes.URL_API_USUARIO_EDIT_PASS+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
            clear();
            }
        });
        return binding.getRoot();
    }

    private void obtenerDatos(String url){
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            usuario = new Usuario(
                                    response.getInt("id"),
                                    response.getString("nombre"),
                                    response.getString("apellido"),
                                    response.getString("email"),
                                    response.getString("direccion"),
                                    response.getString("numero"),
                                    response.getString("contrasenia"),
                                    response.getString("estado"));
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            boxMessage("⊙︿⊙",jsonException.toString());
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse networkResponse = error.networkResponse;
                        if (networkResponse != null && networkResponse.data != null) {
                            byte[] datos = networkResponse.data;
                            try {
                                JSONObject testV=new JSONObject(new String(datos));
                                boxMessage("Ups (◕︵◕)",testV.getString("message"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                                boxMessage("⊙︿⊙",e.toString());
                            }

                        }
                    }
                });
        colaPeticiones.add(jsonObjectRequest);
    }

    private void actualizarContrasenia(String url){
        RequestQueue colapeticiones = Volley.newRequestQueue(getContext());

        String contrasenia = binding.etEditpasswordContrasenia.getText().toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url + "?contrasenia="+ contrasenia,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("Su contraseña se actualizo")){
                                boxMessage("Wiiiii :D",response.getString("message"));
                            }
                        } catch (JSONException ex) {
                            boxMessage("⊙︿⊙",ex.toString());
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
                        boxMessage("Ups (◕︵◕)",testV.getString("message"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        boxMessage("⊙︿⊙",e.toString());
                    }
                }
            }
        });
        colapeticiones.add(request);
    }

    public boolean validacion(){
        boolean rsp = true;
        String contrasenia = binding.etEditpasswordContrasenia.getText().toString();
        String confirmacion = binding.etEditpasswordContraseniaConfirmacion.getText().toString();

        if(contrasenia.isEmpty()|| contrasenia.equals("") || contrasenia == null){
            boxMessage("Ups","Ingrese su nueva contraseña");
            rsp=false;
        }else if(confirmacion.isEmpty()||confirmacion.equals("") || confirmacion == null){
            boxMessage("Ups","Ingrese su nueva contraseña de nuevo");
            rsp=false;
        }else if(!contrasenia.equals(confirmacion)){
            boxMessage("Ups", "Los datos ingresados no coinciden");
            rsp=false;
        }
        return rsp;
    }

    public void clear(){
        binding.etEditpasswordContrasenia.setText("");
        binding.etEditpasswordContraseniaConfirmacion.setText("");
    }


    public void boxMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(mensaje)
                .setTitle(titulo);

        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}