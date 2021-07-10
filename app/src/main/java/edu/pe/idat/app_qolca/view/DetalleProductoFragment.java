package edu.pe.idat.app_qolca.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import edu.pe.idat.app_qolca.model.Producto;


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
                obtenerProducto(Constantes.URL_API_PRODUCTO_ID + id);
            }
        });

        return binding.getRoot();
    }


    private void obtenerProducto(String url) {
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            binding.tvDetailproductNombre.setText(response.getString("nombre"));
                            binding.tvDetailproductMarca.setText(response.getString("marca"));
                            binding.tvDetailproductPrecio.setText(response.getString("precio"));
                            Glide.with(getContext()).load(response.getString("urlImg")).into(binding.ivDetailproductImg);
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
}