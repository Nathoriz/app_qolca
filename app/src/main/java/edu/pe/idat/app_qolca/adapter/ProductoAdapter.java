package edu.pe.idat.app_qolca.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.pe.idat.app_qolca.databinding.ProductoCardBinding;
import edu.pe.idat.app_qolca.model.Producto;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder>{
    private Context context;
    private ArrayList<Producto> list;
    private ArrayList<Producto> originalList;
    private RecyclerItemClick itemClick;


    public ProductoAdapter(Context context, RecyclerItemClick itemClick){
        this.context = context;
        list = new ArrayList<>();
        this.originalList = new ArrayList<>();
        originalList.addAll(list);
        this.itemClick = itemClick;
    }

    @NonNull
    @NotNull
    @Override
    public ProductoAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        ProductoCardBinding recyclerBinding = ProductoCardBinding.inflate(layoutInflater,parent,false);

        return new ProductoAdapter.ViewHolder(recyclerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ProductoAdapter.ViewHolder holder, int position) {
        final Producto item = list.get(position);
        if(item.getNombre().length() > 20){
            holder.recyclerBinding.tvItemNombre.setText(capitalize(item.getNombre().substring(0,20)+"..."));
        }else{
            holder.recyclerBinding.tvItemNombre.setText(capitalize(item.getNombre()));
        }
        holder.recyclerBinding.tvItemPrecio.setText(String.valueOf(item.getPrecio()));
        Glide.with(context).load(item.getImagen()).into(holder.recyclerBinding.ivProductoImagen);

        holder.recyclerBinding.getRoot().setOnClickListener(view ->{
            itemClick.itemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public interface RecyclerItemClick{
        void itemClick(Producto p);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ProductoCardBinding recyclerBinding;
        public ViewHolder(@NonNull @NotNull ProductoCardBinding itemView) {
            super(itemView.getRoot());
            recyclerBinding=itemView;
        }
    }

    public void addProducto(ArrayList<Producto> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    public void filter(final String strSearch){
        if(strSearch.length() == 0){
            list.clear();
            list.addAll(originalList);
        }else {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                list.clear();
                List<Producto> collect = originalList.stream()
                        .filter(i-> i.getNombre().toLowerCase().contains(strSearch.toLowerCase()))
                        .collect(Collectors.toList());
                list.addAll(collect);
            }else {
                list.clear();
                for(Producto i:originalList){
                    if(i.getNombre().toLowerCase().contains(strSearch.toLowerCase())){
                        list.add(i);
                    }
                }
            }
        }
        notifyDataSetChanged();
    }

    private String capitalize(String m){
        StringBuffer strbf = new StringBuffer();
        Matcher match = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(m);
        while(match.find())
        {
            match.appendReplacement(strbf, match.group(1).toUpperCase() + match.group(2).toLowerCase());
        }
        return match.appendTail(strbf).toString();
    }
}
