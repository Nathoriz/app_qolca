package edu.pe.idat.app_qolca.view;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.pe.idat.app_qolca.adapter.DetallePedidoAdapter;
import edu.pe.idat.app_qolca.adapter.PedidoProductoAdapter;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.ActivityPagoCardioBinding;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.DetallePedido;
import edu.pe.idat.app_qolca.model.Pedido;
import edu.pe.idat.app_qolca.model.PedidoProducto;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;
import edu.pe.idat.app_qolca.model.Usuario;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;

public class PagoCardioActivity extends AppCompatActivity {

    private ActivityPagoCardioBinding binding;
    private DetallePedidoAdapter adapter;
    private static final int SCAN_RESULT = 100;
    private int id_pedido;
    private double total;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        binding = ActivityPagoCardioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        id_pedido =getIntent().getIntExtra("idpedido",0);
        if(id_pedido== 0 ){
            boxMessage("Ups","ocurrio un problema vuelva a intentarlo más tarde");
        }else{
            adapter = new DetallePedidoAdapter(this);
            binding.rvDetallepedido.setLayoutManager(new LinearLayoutManager(this));
            binding.rvDetallepedido.setAdapter(adapter);
            obtenerProductos(Constantes.URL_API_DETPED_LISTAR_PEDIDO+id_pedido);
            binding.btnEscanear.setOnClickListener(view->{
                scanearTarjeta();
            });
            binding.btnPagar.setOnClickListener(view->{
                pedidoPagado(Constantes.URL_API_PEDIDO_PAGAR+id_pedido);
            });
            binding.ivClose.setOnClickListener(view->{
                eliminarPedido(Constantes.URL_API_PEDIDO_DELETE + id_pedido);
            });
        }
    }

    private void eliminarPedido(String url) {
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                boxMessage("O_O","Su pedido se canceló");
                                Intent intent = new Intent(context,MainActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            boxMessage("⊙︿⊙ -",e.toString());
                        }
                    }
                },
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

    private void scanearTarjeta(){
        Intent intent = new Intent(this, CardIOActivity.class)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY,true)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_CVV,false)
                .putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE,false);
        startActivityForResult(intent,SCAN_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        binding.btnEscanear.setVisibility(View.GONE);
        binding.cvTarjeta.setVisibility(View.GONE);
        if(requestCode == SCAN_RESULT){
            if(data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)){
                CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
                binding.tvNumerotarjeta.setText(scanResult.getRedactedCardNumber());
                if (scanResult.isExpiryValid()){
                    String mes = String.valueOf(scanResult.expiryMonth);
                    String an = String.valueOf(scanResult.expiryYear);
                    binding.tvFechaVencimiento.setText(mes+"/"+an);

                    binding.btnPagar.setVisibility(View.VISIBLE);
                    binding.rvDetallepedido.setVisibility(View.VISIBLE);
                    binding.tvPagoTitulo.setVisibility(View.VISIBLE);
                }else{
                    boxMessage("Ups","Su tarjeta ya expiró");
                    binding.btnPagar.setVisibility(View.GONE);
                    binding.rvDetallepedido.setVisibility(View.GONE);
                    binding.tvPagoTitulo.setVisibility(View.GONE);
                    binding.btnEscanear.setVisibility(View.VISIBLE);
                    binding.cvTarjeta.setVisibility(View.VISIBLE);
                }
            }else{
                binding.btnPagar.setVisibility(View.GONE);
                binding.rvDetallepedido.setVisibility(View.GONE);
                binding.tvPagoTitulo.setVisibility(View.GONE);
                binding.btnEscanear.setVisibility(View.VISIBLE);
                binding.cvTarjeta.setVisibility(View.VISIBLE);
            }
        }else{
            boxMessage("Ups","Al parecer ocurrio un problema, vuelva a intentarlo más tarde");
            binding.btnPagar.setVisibility(View.GONE);
            binding.rvDetallepedido.setVisibility(View.GONE);
            binding.tvPagoTitulo.setVisibility(View.GONE);
            binding.btnEscanear.setVisibility(View.VISIBLE);
            binding.cvTarjeta.setVisibility(View.VISIBLE);
        }
    }

    private void obtenerProductos(String url){
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<DetallePedido> detallePedidos = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject jsonObjectProducto = jsonObject.getJSONObject("producto");
                                JSONObject jsonObjectSubcategoria = jsonObjectProducto.getJSONObject("subcategoria");
                                JSONObject jsonObjectCategoria = jsonObjectSubcategoria.getJSONObject("categoria");
                                JSONObject jsonObjectPedido = jsonObject.getJSONObject("pedido");
                                JSONObject jsonObjectUsuario = jsonObjectPedido.getJSONObject("usuario");

                                Usuario usuario = new Usuario(
                                        jsonObjectUsuario.getInt("id"),
                                        jsonObjectUsuario.getString("nombre"),
                                        jsonObjectUsuario.getString("apellido"),
                                        jsonObjectUsuario.getString("email"),
                                        jsonObjectUsuario.getString("direccion"),
                                        jsonObjectUsuario.getString("numero"),
                                        jsonObjectUsuario.getString("contrasenia"),
                                        jsonObjectUsuario.getString("estado"));

                                Pedido pedido = new Pedido(jsonObjectPedido.getInt("id"),
                                        usuario,
                                        jsonObjectPedido.getString("persona"),
                                        jsonObjectPedido.getString("direccion"),
                                        jsonObjectPedido.getString("numero"),
                                        jsonObjectPedido.getDouble("total"),
                                        jsonObjectPedido.getString("fecha"),
                                        jsonObjectPedido.getString("estado"));

                                Categoria categoria = new Categoria(jsonObjectCategoria.getInt("id"),jsonObjectCategoria.getString("nombre"));
                                Subcategoria subcategoria = new Subcategoria(jsonObjectSubcategoria.getInt("id"),jsonObjectSubcategoria.getString("nombre"),categoria);

                                Producto producto = new Producto(
                                        jsonObjectProducto.getInt("id"),
                                        jsonObjectProducto.getString("nombre"),
                                        jsonObjectProducto.getString("descripcion"),
                                        jsonObjectProducto.getString("marca"),
                                        jsonObjectProducto.getDouble("precio"),
                                        jsonObjectProducto.getString("urlImg"),
                                        jsonObjectProducto.getInt("stock"),
                                        subcategoria);

                                DetallePedido detallePedido = new DetallePedido(
                                        jsonObject.getInt("id"),
                                        pedido,
                                        producto,
                                        jsonObject.getInt("cantidad"),
                                        jsonObject.getDouble("total")
                                );
                                total = jsonObjectPedido.getDouble("total");
                                detallePedidos.add(detallePedido);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                boxMessage("⊙︿⊙",e.toString());
                            }
                        }
                        adapter.addProductosPedidoDetalle(detallePedidos);
                        binding.btnPagar.setText(binding.btnPagar.getText().toString() + String.valueOf(total));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonArrayRequest);
    }

    private void pedidoPagado(String url){
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                borrarProductosCarrito(Constantes.URL_API_CARRITOPRODUCTOS_DELETE_ALL+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            boxMessage("⊙︿⊙ -",e.toString());
                        }
                    }
                },
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

    private void borrarProductosCarrito(String url){
        RequestQueue colaPeticiones = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                boxMessage("OwO","Gracias por su compra");
                                Intent intent = new Intent(context,MainActivity.class);
                                startActivity(intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            boxMessage("⊙︿⊙ -",e.toString());
                        }
                    }
                },
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

    public void boxMessage(String titulo, String mensaje) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(mensaje)
                .setTitle(titulo);

        AlertDialog alert = builder.create();
        alert.show();
    }
}