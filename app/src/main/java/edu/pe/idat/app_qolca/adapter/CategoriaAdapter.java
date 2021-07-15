package edu.pe.idat.app_qolca.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Random;

import edu.pe.idat.app_qolca.databinding.CategoriaCardBinding;
import edu.pe.idat.app_qolca.model.Categoria;
import edu.pe.idat.app_qolca.model.Producto;

public class CategoriaAdapter extends RecyclerView.Adapter<CategoriaAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Categoria> list;
    private CategoriaAdapter.RecyclerItemClick itemClick;

    public CategoriaAdapter(Context context, CategoriaAdapter.RecyclerItemClick itemClick){
        this.context = context;
        list = new ArrayList<>();
        this.itemClick = itemClick;
    }

    @NonNull
    @NotNull
    @Override
    public CategoriaAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CategoriaCardBinding recyclerBinding = CategoriaCardBinding.inflate(layoutInflater,parent,false);
        return new ViewHolder(recyclerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CategoriaAdapter.ViewHolder holder, int position) {
        final Categoria item = list.get(position);
        Random random = new Random();
        holder.recyclerBinding.tvCategoria.setText(item.getNombre());
        holder.recyclerBinding.llCategoria.setBackgroundColor(
                Color.rgb((random.nextInt(256) + random.nextInt(256)) / 2,
                        (random.nextInt(256) + random.nextInt(256)) / 2,
                        (random.nextInt(256) + random.nextInt(256)) / 2)
        );
        holder.recyclerBinding.getRoot().setOnClickListener(view ->{
            itemClick.itemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CategoriaCardBinding recyclerBinding;
        public ViewHolder(@NonNull @NotNull CategoriaCardBinding itemView) {
            super(itemView.getRoot());
            recyclerBinding=itemView;
        }
    }

    public void addCategoria(ArrayList<Categoria> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    public interface RecyclerItemClick{
        void itemClick(Categoria c);
    }
}
