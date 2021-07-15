package edu.pe.idat.app_qolca.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.pe.idat.app_qolca.databinding.CartCardBinding;
import edu.pe.idat.app_qolca.model.CarritoProducto;

public class CarritoProductoAdapter extends RecyclerView.Adapter<CarritoProductoAdapter.ViewHolder>{
    private Context context;
    private ArrayList<CarritoProducto> list;
    private RecyclerItemClick itemClick;

    public CarritoProductoAdapter(Context context, RecyclerItemClick itemClick){
        this.context = context;
        list = new ArrayList<>();
        this.itemClick = itemClick;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CartCardBinding recyclerBinding = CartCardBinding.inflate(layoutInflater,parent,false);
        return new CarritoProductoAdapter.ViewHolder(recyclerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.00");
        final CarritoProducto item = list.get(position);
        holder.recyclerBinding.tvCartMarca.setText(item.getProducto().getMarca());
        holder.recyclerBinding.tvCartNombre.setText(item.getProducto().getNombre());
        holder.recyclerBinding.tvCartPrecio.setText(String.valueOf(item.getProducto().getPrecio()));
        holder.recyclerBinding.tvCartCantidad.setText(String.valueOf(item.getCantidad()));
        holder.recyclerBinding.tvCartSubtotal.setText(df.format((item.getCantidad() * item.getProducto().getPrecio())));
        Glide.with(context).load(item.getProducto().getImagen()).into(holder.recyclerBinding.ivCartImg);

        holder.recyclerBinding.ivCartBorrar.setOnClickListener(view->{
            itemClick.itemClick(item, holder.recyclerBinding.ivCartBorrar,holder.recyclerBinding);
            list.remove(item);
            notifyDataSetChanged();
        });

        holder.recyclerBinding.ivCartDecrementar.setOnClickListener(view->{
            itemClick.itemClick(item, holder.recyclerBinding.ivCartDecrementar,holder.recyclerBinding);
        });

        holder.recyclerBinding.ivCartIncrementar.setOnClickListener(view ->{
           itemClick.itemClick(item,holder.recyclerBinding.ivCartIncrementar,holder.recyclerBinding);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CartCardBinding recyclerBinding;
        public ViewHolder(@NonNull @NotNull CartCardBinding itemView) {
            super(itemView.getRoot());
            recyclerBinding = itemView;
        }
    }

    public void addCarritoProducto(ArrayList<CarritoProducto> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }

    public interface RecyclerItemClick{
        void itemClick(CarritoProducto cp, ImageView img,CartCardBinding cardBinding);
    }
}
