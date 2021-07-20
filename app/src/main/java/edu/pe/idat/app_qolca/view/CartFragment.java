package edu.pe.idat.app_qolca.view;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import edu.pe.idat.app_qolca.R;
import edu.pe.idat.app_qolca.adapter.CarritoProductoAdapter;
import edu.pe.idat.app_qolca.common.Constantes;
import edu.pe.idat.app_qolca.common.SharedPreferenceManager;
import edu.pe.idat.app_qolca.databinding.CartCardBinding;
import edu.pe.idat.app_qolca.databinding.FragmentCartBinding;
import edu.pe.idat.app_qolca.model.Carrito;
import edu.pe.idat.app_qolca.model.CarritoProducto;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.Producto;
import edu.pe.idat.app_qolca.model.Subcategoria;
import edu.pe.idat.app_qolca.model.Usuario;


public class CartFragment extends Fragment implements CarritoProductoAdapter.RecyclerItemClick{
    private FragmentCartBinding binding;
    private CarritoProductoAdapter adapter;
    private double total;

    public CartFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCartBinding.inflate(inflater,container,false);
        adapter = new CarritoProductoAdapter(getContext(), this::itemClick);
        binding.rvCartproducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCartproducts.setAdapter(adapter);
        obtenerProductosCarrito(Constantes.URL_API_CARRITOPRODUCTOS_LISTAR+ SharedPreferenceManager.getSomeIntValue("PREF_ID").toString());
        binding.btnCarritoComprar.setOnClickListener(view ->{
            Bundle bundle = new Bundle();
            bundle.putString("message", "cart");
            getParentFragmentManager().setFragmentResult("key", bundle);

            PedidoFragment fragment = new PedidoFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.nav_host_fragment_activity_main,fragment);
            transaction.commit();
        });
        return binding.getRoot();
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
                        ArrayList<CarritoProducto> carritoProductos = new ArrayList<>();
                        for (int i = 0; i <response.length(); i++){
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                JSONObject jsonObjectCarrito = jsonObject.getJSONObject("carrito");
                                JSONObject jsonObjectUsuario = jsonObjectCarrito.getJSONObject("usuario");
                                JSONObject jsonObjectProducto = jsonObject.getJSONObject("producto");
                                JSONObject jsonObjectSubcategoria = jsonObjectProducto.getJSONObject("subcategoria");
                                JSONObject jsonObjectCategoria = jsonObjectSubcategoria.getJSONObject("categoria");


                                Usuario usuario = new Usuario(
                                        jsonObjectUsuario.getInt("id"),
                                        jsonObjectUsuario.getString("nombre"),
                                        jsonObjectUsuario.getString("apellido"),
                                        jsonObjectUsuario.getString("email"),
                                        jsonObjectUsuario.getString("direccion"),
                                        jsonObjectUsuario.getString("numero"),
                                        jsonObjectUsuario.getString("contrasenia"),
                                        jsonObjectUsuario.getString("estado"));
                                Carrito carrito = new Carrito(jsonObjectCarrito.getInt("id"),usuario);

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

                                CarritoProducto carritoProducto = new CarritoProducto(
                                        jsonObject.getInt("id"),
                                        carrito,
                                        producto,
                                        jsonObject.getInt("cantidad"),
                                        jsonObject.getString("selected"));
                                total += carritoProducto.getCantidad() * producto.getPrecio();
                                carritoProductos.add(carritoProducto);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                boxMessage("⊙︿⊙",e.toString());
                            }
                        }
                        adapter.addCarritoProducto(carritoProductos);
                        binding.tvCarritoTotal.setText(df.format(total));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        colaPeticiones.add(jsonArrayRequest);
    }

    private void eliminarProductoCarrito(String url, CartCardBinding cardBinding){
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                DecimalFormat df = new DecimalFormat("#.00");
                                double total = Double.valueOf(binding.tvCarritoTotal.getText().toString());
                                JSONObject jsonCarritoProducto = response.getJSONObject("carritoproducto");
                                JSONObject jsonProducto = jsonCarritoProducto.getJSONObject("producto");
                                double subtotal = jsonProducto.getDouble("precio") * jsonCarritoProducto.getInt("cantidad");
                                total = total - subtotal;
                                binding.tvCarritoTotal.setText(df.format(total));
                                boxMessage("UwU","El producto se eliminó de su carrito :)");
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

    private void incrementarCantidadProductoCarrito(String url, CartCardBinding cardBinding){
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                DecimalFormat df = new DecimalFormat("#.00");
                                double total = Double.valueOf(binding.tvCarritoTotal.getText().toString());
                                double precio = Double.valueOf(cardBinding.tvCartPrecio.getText().toString());
                                int cantidad = Integer.valueOf(cardBinding.tvCartCantidad.getText().toString())+1;
                                cardBinding.tvCartCantidad.setText(String.valueOf(cantidad));
                                cardBinding.tvCartSubtotal.setText(df.format(cantidad*precio));
                                binding.tvCarritoTotal.setText(df.format(total+precio));
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

    private void decrementarCantidadProductoCarrito(String url, CartCardBinding cardBinding){
        RequestQueue colaPeticiones = Volley.newRequestQueue(getContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.PUT,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(response.getString("message").equals("ok")){
                                DecimalFormat df = new DecimalFormat("#.00");
                                double total = Double.valueOf(binding.tvCarritoTotal.getText().toString());
                                double precio = Double.valueOf(cardBinding.tvCartPrecio.getText().toString());
                                int cantidad = Integer.valueOf(cardBinding.tvCartCantidad.getText().toString())-1;
                                cardBinding.tvCartCantidad.setText(String.valueOf(cantidad));
                                cardBinding.tvCartSubtotal.setText(df.format(cantidad*precio));
                                binding.tvCarritoTotal.setText(df.format(total-precio));
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

    @Override
    public void itemClick(CarritoProducto cp, ImageView img, CartCardBinding cardBinding) {
        if(img.getId() == cardBinding.ivCartBorrar.getId() ){
            eliminarProductoCarrito(Constantes.URL_API_CARRITOPRODUCTOS_DELETE + cp.getId(),cardBinding);
        }else if(img.getId() == cardBinding.ivCartIncrementar.getId() ){
            if(Integer.valueOf(cardBinding.tvCartCantidad.getText().toString()) == cp.getProducto().getStock()){
            }else{ incrementarCantidadProductoCarrito(Constantes.URL_API_CARRITOPRODUCTOS_INCREMENTAR + cp.getId(),cardBinding); }
        }else if(img.getId() == cardBinding.ivCartDecrementar.getId() ){
            if(Integer.valueOf(cardBinding.tvCartCantidad.getText().toString()) <= 1){
            } else{ decrementarCantidadProductoCarrito(Constantes.URL_API_CARRITOPRODUCTOS_DECREMENTAR + cp.getId(),cardBinding);}
        }else{
//            boxMessage("O.O",String.valueOf(img.getId()));
        }
    }
}