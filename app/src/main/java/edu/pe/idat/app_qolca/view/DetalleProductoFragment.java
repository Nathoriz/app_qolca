package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;

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
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.FragmentDetalleProductoBinding;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;


public class DetalleProductoFragment extends Fragment {
    private FragmentDetalleProductoBinding binding;
    private Producto producto;
    private JSONObject productojson;
    private JSONObject carritojson;


    public DetalleProductoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleProductoBinding.inflate(inflater,container,false);
        getParentFragmentManager().setFragmentResultListener("ID", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                int id = result.getInt("idproduct");
                detalleProducto(Constantes.URL_API_PRODUCTO_ID + id);
            }
        });
        obtenerCarrito(Constantes.URL_API_CARRITO_ID+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
        binding.ivDetailproductDecrementar.setOnClickListener(view ->{
            decrementCantidad();
        });
        binding.ivDetailproductIncrementar.setOnClickListener(view ->{
            incrementCantidad();
        });
        binding.ivDetailproductCart.setOnClickListener(view->{
            aniadirCarrito(Constantes.URL_API_CARRITOPRODUCTOS_CREAR);
        });
        binding.btnDetailproductComprar.setOnClickListener(view ->{
            Bundle bundle = new Bundle();
            bundle.putString("message", "producto");
            bundle.putInt("idproducto",producto.getId());
            bundle.putInt("cantidad",Integer.valueOf(binding.tvDetailproductCantidad.getText().toString()));
            getParentFragmentManager().setFragmentResult("key", bundle);

            PedidoFragment fragment = new PedidoFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main,fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return binding.getRoot();
    }

    private void detalleProducto(String url) {
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jsonObjectSubcategoria = response.getJSONObject("subcategoria");
                            JSONObject jsonObjectCategoria = jsonObjectSubcategoria.getJSONObject("categoria");
                            Categoria categoria = new Categoria(jsonObjectCategoria.getInt("id"),jsonObjectCategoria.getString("nombre"));
                            Subcategoria subcategoria = new Subcategoria(jsonObjectSubcategoria.getInt("id"),jsonObjectSubcategoria.getString("nombre"),categoria);

                            producto = new Producto(
                                    response.getInt("id"),
                                    response.getString("nombre"),
                                    response.getString("descripcion"),
                                    response.getString("marca"),
                                    response.getDouble("precio"),
                                    response.getString("urlImg"),
                                    response.getInt("stock"),
                                    subcategoria);

                            productojson = response;

                            setInformacionProducto(producto);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            boxMessage("⊙︿⊙",e.toString());
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonObjectRequest);
    }

    private void setInformacionProducto(Producto producto){
        binding.tvDetailproductNombre.setText(producto.getNombre());
        binding.tvDetailproductMarca.setText(producto.getMarca());
        binding.tvDetailproductPrecio.setText(String.valueOf(producto.getPrecio()));
        Glide.with(getContext()).load(producto.getImagen()).into(binding.ivDetailproductImg);
        binding.tvDetailproductStock.setText(binding.tvDetailproductStock.getText().toString() + producto.getStock());
    }

    private void incrementCantidad(){
        if(Integer.valueOf(binding.tvDetailproductCantidad.getText().toString()) >= producto.getStock()){
            boxMessage("Ups","Solo disponemos " + producto.getStock() +" en stock de este producto :c");
        }else {
            DecimalFormat df = new DecimalFormat("#.00");
            int cant = Integer.valueOf(binding.tvDetailproductCantidad.getText().toString())+1;
            double prec = producto.getPrecio();
            double total = prec*cant;

            binding.tvDetailproductCantidad.setText(String.valueOf(cant));
            binding.tvDetailproductPrecio.setText(df.format(total));
        }
    }

    private void decrementCantidad(){
        if(Integer.valueOf(binding.tvDetailproductCantidad.getText().toString()) <= 1){
            // Que poner aqui, no lo se. ┐(︶▽︶)┌
        }else {
            DecimalFormat df = new DecimalFormat("#.00");
            int cant = Integer.valueOf(binding.tvDetailproductCantidad.getText().toString())-1;
            double prec = producto.getPrecio();
            double total = prec*cant;

            binding.tvDetailproductCantidad.setText(String.valueOf(cant));
            binding.tvDetailproductPrecio.setText(df.format(total));
        }
    }

    private void obtenerCarrito(String url){
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        carritojson = response;
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonObjectRequest);
    }

    private void aniadirCarrito(String url){
        RequestQueue colapeticiones = Volley.newRequestQueue(getContext());
        Map<String, Object> parametros= new HashMap<>();
        parametros.put("cantidad",binding.tvDetailproductCantidad.getText().toString());
        parametros.put("carrito",carritojson);
        parametros.put("producto",productojson);
        parametros.put("selected","false");

        JSONObject jsonObjectParametro = new JSONObject(parametros);
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonObjectParametro,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("Se añadio a su carrito")){
                                boxMessage("Wiiiii :D",response.getString("message"));
                            }
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
                            boxMessage("⊙︿⊙ -",jsonException.toString());
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