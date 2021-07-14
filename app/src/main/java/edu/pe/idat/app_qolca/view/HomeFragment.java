package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Parcelable;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.adapter.ProductoAdapter;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.FragmentHomeBinding;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;

import static androidx.navigation.Navigation.findNavController;


public class HomeFragment extends Fragment implements SearchView.OnQueryTextListener, ProductoAdapter.RecyclerItemClick {

    private FragmentHomeBinding binding;
    private ProductoAdapter adapter;
    private ArrayList<Producto> list;
    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        adapter = new ProductoAdapter(getContext(), this::itemClick);
        binding.rvProductos.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.rvProductos.setAdapter(adapter);
        obtenerProducto(Constantes.URL_API_PRODUCTO_LISTAR);
        binding.svHomeBuscar.setOnQueryTextListener(this);

        binding.bienvenida.setText(binding.bienvenida.getText() +
                " "+
                SharedPreferenceManager.getSomeStringValue("PREF_NOMBRE"));

        return binding.getRoot();
    }

    private void obtenerProducto(String url) {
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
                        adapter.addProducto(productos);
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
        obtenerProducto(Constantes.URL_API_PRODUCTO_BUSCAR+newText);
        adapter.filter(newText);
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