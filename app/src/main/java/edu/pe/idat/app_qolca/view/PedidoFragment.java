package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.pe.idat.app_qolca.adapter.CarritoProductoAdapter;
import edu.pe.idat.app_qolca.adapter.PedidoProductoAdapter;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.FragmentPedidoBinding;
import edu.pe.idat.app_qolca.model.Carrito;
import edu.pe.idat.app_qolca.model.CarritoProducto;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.PedidoProducto;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;
import edu.pe.idat.app_qolca.model.Usuario;


@RequiresApi(api = Build.VERSION_CODES.O)
public class PedidoFragment extends Fragment {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'del' yyyy hh:mm a");
    private String id_user = SharedPreferenceManager.getSomeIntValue("PREF_ID").toString();
    private FragmentPedidoBinding binding;
    private PedidoProductoAdapter adapter;
    private JSONObject jsonUsuario;
    private double total = 00;
    private String rsp = "";
    private int cantidad;
    private int idproducto;

    public PedidoFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPedidoBinding.inflate(inflater,container,false);
        adapter = new PedidoProductoAdapter(getContext());
        binding.rvProductosPedido.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProductosPedido.setAdapter(adapter);
        obtenerDatos(Constantes.URL_API_USUARIO_ID+id_user);
        getParentFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                String msj =  result.getString("message");
                rsp = msj;
                if(msj.equals("cart")){
                    obtenerProductosCarrito(Constantes.URL_API_CARRITOPRODUCTOS_LISTAR+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
                }else if(msj.equals("producto")){
                    idproducto = result.getInt("idproducto");
                    cantidad = result.getInt("cantidad");
                    obtenerProducto(Constantes.URL_API_PRODUCTO_ID + idproducto, cantidad);
                }
            }
        });
        binding.btnPedidoRealizarpedido.setOnClickListener(view ->{
            if(rsp.equals("cart")){
                crearPedidoComprarTodo(Constantes.URL_API_PEDIDO_COMPRAR_TODO);
            }else if(rsp.equals("producto")){
                crearPedidoComprar(Constantes.URL_API_PEDIDO_COMPRAR+"cantidad="+cantidad+"&idproducto="+idproducto);
            }else {
                boxMessage("Ups","Al parecer hubo un error, intententlo más tarde :(");
            }
        });
        binding.ivPedidoUp.setOnClickListener(view->{
            binding.lyPedidoDatosenvio.setVisibility(View.GONE);
            binding.lyPedidoDatos.setVisibility(View.GONE);
            binding.lyPedidoTexto.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            binding.ivPedidoDown.setVisibility(View.VISIBLE);

        });
        binding.ivPedidoDown.setOnClickListener(view ->{
            binding.lyPedidoDatos.setVisibility(View.VISIBLE);
            binding.lyPedidoDatos.setVisibility(View.VISIBLE);
            binding.lyPedidoTexto.setVisibility(View.VISIBLE);
            binding.ivPedidoUp.setVisibility(View.VISIBLE);
            view.setVisibility(View.GONE);
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
                            jsonUsuario = response;
                            binding.etPedidoDireccion.setText(response.getString("direccion"));
                            binding.etPedidoNombre.setText(response.getString("nombre"));
                            binding.etPedidoNumero.setText(response.getString("numero"));
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

    private void obtenerProductosCarrito(String url){
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        DecimalFormat df = new DecimalFormat("#.00");
                        ArrayList<PedidoProducto> pedidosProductos = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject jsonObjectProducto = jsonObject.getJSONObject("producto");
                                int cantidad = jsonObject.getInt("cantidad");
                                double subtotal = jsonObjectProducto.getDouble("precio") * cantidad;
                                PedidoProducto pedidoProducto = new PedidoProducto(
                                        jsonObject.getInt("id"),
                                        jsonObjectProducto.getString("marca"),
                                        jsonObjectProducto.getString("nombre"),
                                        jsonObjectProducto.getDouble("precio"),
                                        cantidad,
                                        subtotal,
                                        jsonObjectProducto.getString("urlImg")
                                        );
                                total += pedidoProducto.getCantidad() * pedidoProducto.getPrecio();
                                pedidosProductos.add(pedidoProducto);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                boxMessage("⊙︿⊙",e.toString());
                            }
                        }
                        adapter.addPedidoProducto(pedidosProductos);
                        binding.tvPedidoTotal.setText(df.format(total));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonArrayRequest);
    }

    private void obtenerProducto(String url,int cant){
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DecimalFormat df = new DecimalFormat("#.00");
                        ArrayList<PedidoProducto> pedidosProductos = new ArrayList<>();
                        try {
                            double subtotal = response.getDouble("precio") * cant;
                            PedidoProducto pedidoProducto = new PedidoProducto(
                                    response.getInt("id"),
                                    response.getString("marca"),
                                    response.getString("nombre"),
                                    response.getDouble("precio"),
                                    cant,
                                    subtotal,
                                    response.getString("urlImg")
                            );
                            pedidosProductos.add(pedidoProducto);
                            total += pedidoProducto.getCantidad() * pedidoProducto.getPrecio();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            boxMessage("⊙︿⊙",e.toString());
                        }
                        adapter.addPedidoProducto(pedidosProductos);
                        binding.tvPedidoTotal.setText(df.format(total));
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonObjectRequest);
    }

    private void crearPedidoComprarTodo(String url){
        RequestQueue colapeticiones = Volley.newRequestQueue(getContext());
        Map<String,Object> parametros= new HashMap<>();
        parametros.put("direccion",binding.etPedidoDireccion.getText().toString());
        parametros.put("numero",binding.etPedidoNumero.getText().toString());
        parametros.put("persona",binding.etPedidoNombre.getText().toString());
        parametros.put("fecha",dtf.format(LocalDateTime.now()));
        parametros.put("total",binding.tvPedidoTotal.getText().toString());
        parametros.put("usuario",jsonUsuario);
        parametros.put("estado","EN PROCESO");

        JSONObject jsonObjectParametro = new JSONObject(parametros);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObjectParametro,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                clear();
                                Intent intent = new Intent(getContext(),PagoCardioActivity.class);
                                intent.putExtra("idpedido",response.getInt("id"));
                                startActivity(intent);
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
        }
        );
        colapeticiones.add(request);
    }

    private void crearPedidoComprar(String url){
        RequestQueue colapeticiones = Volley.newRequestQueue(getContext());
        Map<String,Object> parametros= new HashMap<>();
        parametros.put("direccion",binding.etPedidoDireccion.getText().toString());
        parametros.put("numero",binding.etPedidoNumero.getText().toString());
        parametros.put("persona",binding.etPedidoNombre.getText().toString());
        parametros.put("fecha",dtf.format(LocalDateTime.now()));
        parametros.put("total",binding.tvPedidoTotal.getText().toString());
        parametros.put("usuario",jsonUsuario);
        parametros.put("estado","EN PROCESO");

        JSONObject jsonObjectParametro = new JSONObject(parametros);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObjectParametro,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                clear();
                                Intent intent = new Intent(getContext(),PagoCardioActivity.class);
                                intent.putExtra("idpedido",response.getInt("id"));
                                startActivity(intent);
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

    private void clear(){
        binding.etPedidoNombre.setText("");
        binding.etPedidoDireccion.setText("");
        binding.etPedidoNumero.setText("");
        binding.tvPedidoTotal.setText("");
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