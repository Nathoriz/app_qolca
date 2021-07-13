package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.databinding.FragmentDetalleProductoBinding;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;


public class DetalleProductoFragment extends Fragment {
    private FragmentDetalleProductoBinding binding;
    private Producto producto;

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
        binding.ivDetailproductDecrementar.setOnClickListener(view ->{
            decrementCantidad();
        });
        binding.ivDetailproductIncrementar.setOnClickListener(view ->{
            incrementCantidad();
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
                            setInformacionProducto(producto);
                        } catch (JSONException jsonException) {
                            jsonException.printStackTrace();
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
    }

    private void incrementCantidad(){
        if(Integer.valueOf(binding.tvDetailproductCantidad.getText().toString()) >= producto.getStock()){
            boxMessage("Ups","Solo disponemos " + producto.getStock() +" en stock de este producto :c");
        }else {
            binding.tvDetailproductCantidad.setText(String.valueOf(Integer.valueOf(binding.tvDetailproductCantidad.getText().toString()) +1));
            binding.tvDetailproductPrecio.setText(String.valueOf(Double.valueOf(binding.tvDetailproductPrecio.getText().toString()) * Integer.valueOf(binding.tvDetailproductCantidad.getText().toString())));
        }
    }

    private void decrementCantidad(){
        if(Integer.valueOf(binding.tvDetailproductCantidad.getText().toString()) <= 1){
            // Que poner aqui, no lo se. ┐(︶▽︶)┌
        }else {
            binding.tvDetailproductCantidad.setText(String.valueOf(Integer.valueOf(binding.tvDetailproductCantidad.getText().toString()) -1));
            binding.tvDetailproductPrecio.setText(String.valueOf(Double.valueOf(binding.tvDetailproductPrecio.getText().toString()) * Integer.valueOf(binding.tvDetailproductCantidad.getText().toString())));
        }
    }

    private void aniadirCarrito(){
        //FALTA
    }

    private void comprarProducto(){
        //FALTA
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