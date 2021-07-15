package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.adapter.PedidoProductoAdapter;
import edu.pe.idat.app_qolca.adapter.ProductoAdapter;
import edu.pe.idat.app_qolca.adapter.SubcategoriaAdapter;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.FragmentProductosCategoriaBinding;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;

public class ProductosCategoriaFragment extends Fragment implements SearchView.OnQueryTextListener, ProductoAdapter.RecyclerItemClick, SubcategoriaAdapter.RecyclerItemClick {

    FragmentProductosCategoriaBinding binding;
    private SubcategoriaAdapter subcategoriaAdapter;
    private ProductoAdapter productoAdapter;
    private int idCategoria;
    private String nombreCategoria;


    public ProductosCategoriaFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductosCategoriaBinding.inflate(inflater,container,false);
        subcategoriaAdapter = new SubcategoriaAdapter(getContext(),this::itemClickSubcategoria);
        binding.rvSubcategorias.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false));
        binding.rvSubcategorias.setAdapter(subcategoriaAdapter);

        productoAdapter = new ProductoAdapter(getContext(),this::itemClick);
        binding.rvCategoriaproductos.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.rvCategoriaproductos.setAdapter(productoAdapter);

        getParentFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull @NotNull String requestKey, @NonNull @NotNull Bundle result) {
                    idCategoria = result.getInt("id");
                    nombreCategoria = result.getString("nombre");
                    binding.tvCategoriaproductosCategoria.setText(nombreCategoria);
                    obtenerSubcategorias(Constantes.URL_API_SUBCATEGORIA_CATEGORIA + idCategoria);
                    obtenerProductos(Constantes.URL_API_PRODUCTO_CATEGORIA + idCategoria);
                }
        });
        binding.svCategoriaBuscar.setOnQueryTextListener(this);
        return binding.getRoot();
    }

    private void obtenerSubcategorias(String url) {
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Subcategoria> subcategorias = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject jsonObjectCategoria = jsonObject.getJSONObject("categoria");

                                Categoria categoria = new Categoria(jsonObjectCategoria.getInt("id"),jsonObjectCategoria.getString("nombre"));
                                Subcategoria subcategoria = new Subcategoria(jsonObject.getInt("id"),jsonObject.getString("nombre"),categoria);

                                subcategorias.add(subcategoria);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                boxMessage("⊙︿⊙",e.toString());
                            }
                        }
                        subcategoriaAdapter.addSubcategoria(subcategorias);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonArrayRequest);
    }

    private void obtenerProductos(String url) {
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Producto> productos = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject jsonObjectSubcategoria = jsonObject.getJSONObject("subcategoria");
                                JSONObject jsonObjectCategoria = jsonObjectSubcategoria.getJSONObject("categoria");

                                Categoria categoria = new Categoria(jsonObjectCategoria.getInt("id"),jsonObjectCategoria.getString("nombre"));
                                Subcategoria subcategoria = new Subcategoria(jsonObjectSubcategoria.getInt("id"),jsonObjectSubcategoria.getString("nombre"),categoria);

                                Producto producto = new Producto(
                                        jsonObject.getInt("id"),
                                        jsonObject.getString("nombre"),
                                        jsonObject.getString("descripcion"),
                                        jsonObject.getString("marca"),
                                        jsonObject.getDouble("precio"),
                                        jsonObject.getString("urlImg"),
                                        jsonObject.getInt("stock"),
                                        subcategoria);
                                productos.add(producto);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                boxMessage("⊙︿⊙",e.toString());
                            }
                        }
                        productoAdapter.addProducto(productos);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonArrayRequest);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        obtenerProductos(Constantes.URL_API_PRODUCTO_CATEGORIA_BUSCAR+idCategoria+"?nombre="+newText);
        productoAdapter.filter(newText);
        return false;
    }

    @Override
    public void itemClick(Producto p) {
        Bundle bundle = new Bundle();
        bundle.putInt("idproduct", p.getId());
        getParentFragmentManager().setFragmentResult("ID", bundle);

        DetalleProductoFragment fragment = new DetalleProductoFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main,fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void itemClickSubcategoria(Subcategoria s) {
        Bundle bundle = new Bundle();
        bundle.putInt("id_subcategoria", s.getId());
        bundle.putString("nombre_subcategoria", s.getNombre());
        bundle.putInt("id_categoria", s.getCategoria().getId());
        bundle.putString("nombre_categoria", s.getCategoria().getNombre());
        getParentFragmentManager().setFragmentResult("key", bundle);

        ProductosSubcategoriaFragment fragment = new ProductosSubcategoriaFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main,fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
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