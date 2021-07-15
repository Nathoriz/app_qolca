package edu.pe.idat.app_qolca.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import edu.pe.idat.app_qolca.adapter.CategoriaAdapter;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.databinding.FragmentCategoryBinding;
import edu.pe.idat.app_qolca.model.Categoria;


public class CategoryFragment extends Fragment implements CategoriaAdapter.RecyclerItemClick{
    private FragmentCategoryBinding binding;
    private CategoriaAdapter adapter;

    public CategoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCategoryBinding.inflate(inflater,container,false);

        adapter = new CategoriaAdapter(getContext(), this::itemClick);

        binding.rvCategoria.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.rvCategoria.setAdapter(adapter);
        obtenerCategoria(Constantes.URL_API_CATEGORIA_LISTAR);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void obtenerCategoria(String url) {
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<Categoria> categorias = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                Categoria categoria = new Categoria(
                                        jsonObject.getInt("id"),
                                        jsonObject.getString("nombre"));
                                categorias.add(categoria);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        adapter.addCategoria(categorias);
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
    public void itemClick(Categoria c) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", c.getId());
        bundle.putString("nombre", c.getNombre());
        getParentFragmentManager().setFragmentResult("key", bundle);

        ProductosCategoriaFragment fragment = new ProductosCategoriaFragment();
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_host_fragment_activity_main,fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }
}