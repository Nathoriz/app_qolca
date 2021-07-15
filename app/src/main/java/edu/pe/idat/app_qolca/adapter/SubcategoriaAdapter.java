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

import edu.pe.idat.app_qolca.databinding.SubcategoriaCardBinding;
import edu.pe.idat.app_qolca.model.Subcategoria;

public class SubcategoriaAdapter extends RecyclerView.Adapter<SubcategoriaAdapter.ViewHolder>{

    private Context context;
    private ArrayList<Subcategoria> list;
    private SubcategoriaAdapter.RecyclerItemClick itemClickSubcategoria;

    public SubcategoriaAdapter(Context context, SubcategoriaAdapter.RecyclerItemClick itemClickSubcategoria){
        this.context = context;
        list = new ArrayList<>();
        this.itemClickSubcategoria = itemClickSubcategoria;
    }

    @NonNull
    @NotNull
    @Override
    public SubcategoriaAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SubcategoriaCardBinding recyclerBinding = SubcategoriaCardBinding.inflate(layoutInflater,parent,false);
        return new SubcategoriaAdapter.ViewHolder(recyclerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SubcategoriaAdapter.ViewHolder holder, int position) {
        final Subcategoria item = list.get(position);
        Random random = new Random();
        holder.recyclerBinding.tvSubcategoria.setText(item.getNombre());
        holder.recyclerBinding.llSubcategoria.setBackgroundColor(
                Color.rgb((random.nextInt(256) + random.nextInt(256)) / 2,
                        (random.nextInt(256) + random.nextInt(256)) / 2,
                        (random.nextInt(256) + random.nextInt(256)) / 2)
        );
        holder.recyclerBinding.getRoot().setOnClickListener(view ->{
            itemClickSubcategoria.itemClickSubcategoria(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        SubcategoriaCardBinding recyclerBinding;
        public ViewHolder(@NonNull @NotNull SubcategoriaCardBinding itemView) {
            super(itemView.getRoot());
            recyclerBinding=itemView;
        }
    }

    public void addSubcategoria(ArrayList<Subcategoria> data) {
        list.addAll(data);
        notifyDataSetChanged();
    }

    public interface RecyclerItemClick{
        void itemClickSubcategoria(Subcategoria c);
    }
}
