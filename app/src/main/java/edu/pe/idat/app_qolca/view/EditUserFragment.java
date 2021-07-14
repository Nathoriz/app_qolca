package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.FragmentEditUserBinding;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;
import edu.pe.idat.app_qolca.model.Usuario;


public class EditUserFragment extends Fragment {

    private FragmentEditUserBinding binding;
    private Usuario usuario;

    public EditUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditUserBinding.inflate(inflater,container,false);
        obtenerDatos(Constantes.URL_API_USUARIO_ID+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
        binding.btnEdituserGuardar.setOnClickListener(view->{
            actualizarDatos(Constantes.URL_API_USUARIO_EDIT+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
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
                            SharedPreferenceManager.setSomeStringValue("PREF_NOMBRE",usuario.getNombre());
                            SharedPreferenceManager.setSomeStringValue("PREF_APELLIDO",usuario.getApellido());
                            setInformacion(usuario);
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

    public void setInformacion(Usuario usuario){
        binding.etEdituserNombre.setText(usuario.getNombre());
        binding.etEdituserApellido.setText(usuario.getApellido());
        binding.etEdituserDireccion.setText(usuario.getDireccion());
        binding.etEdituserNumero.setText(usuario.getNumero());
    }

    private void actualizarDatos(String url){
        RequestQueue colapeticiones = Volley.newRequestQueue(getContext());

        String nombre = binding.etEdituserNombre.getText().toString();
        String apellido = binding.etEdituserApellido.getText().toString();
        String direccion = binding.etEdituserDireccion.getText().toString();
        String numero = binding.etEdituserNumero.getText().toString();

        if(nombre.isEmpty() || nombre.equals("") || nombre == null){
            nombre = usuario.getNombre();
        }
        if(apellido.isEmpty() || apellido.equals("") || apellido == null){
            apellido = usuario.getApellido();
        }
        if(direccion.isEmpty() || direccion.equals("") || direccion == null){
            direccion = usuario.getDireccion();
        }
        if(numero.isEmpty() || numero.equals("") || numero == null){
            numero = usuario.getNumero();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                url + "?apellido="+ apellido.toLowerCase()+
                        "&direccion=" + direccion.toLowerCase()+
                        "&name=" + nombre.toLowerCase()+
                        "&numero=" + numero,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("Sus datos se actualizaron correctamente")){
                               boxMessage("Wiiiii :D",response.getString("message"));
                               obtenerDatos(Constantes.URL_API_USUARIO_ID+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
                            }
                        } catch (JSONException ex) {
                            boxMessage("⊙︿⊙ -",ex.toString());
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