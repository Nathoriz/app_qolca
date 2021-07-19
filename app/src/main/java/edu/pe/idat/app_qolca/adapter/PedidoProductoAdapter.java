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

import edu.pe.idat.app_qolca.databinding.PedidoCardBinding;
import edu.pe.idat.app_qolca.model.CarritoProducto;
import edu.pe.idat.app_qolca.model.PedidoProducto;

public class PedidoProductoAdapter extends RecyclerView.Adapter<PedidoProductoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PedidoProducto> list;

    public PedidoProductoAdapter(Context context){
        this.context = context;
        list = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public PedidoProductoAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PedidoCardBinding recyclerBinding = PedidoCardBinding.inflate(layoutInflater,parent,false);
        return new PedidoProductoAdapter.ViewHolder(recyclerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull PedidoProductoAdapter.ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.00");
        final PedidoProducto item = list.get(position);
        holder.recyclerBinding.tvPedidoproductosMarca.setText(item.getMarca());
        holder.recyclerBinding.tvPedidoproductosNombre.setText(item.getNombre());
        holder.recyclerBinding.tvPedidoproductosPrecio.setText(String.valueOf(item.getPrecio()));
        holder.recyclerBinding.tvPedidoproductosCantidad.setText(String.valueOf(item.getCantidad()));
        holder.recyclerBinding.tvPedidoproductosSubtotal.setText(df.format((item.getCantidad() * item.getPrecio())));
        Glide.with(context).load(item.getImagen()).into(holder.recyclerBinding.ivPedidoproductosImg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PedidoCardBinding recyclerBinding;
        public ViewHolder(@NonNull @NotNull PedidoCardBinding itemView) {
            super(itemView.getRoot());
            recyclerBinding = itemView;
        }
    }


    public void clearlist(){
        list.clear();
        notifyDataSetChanged();
    }

    public void addPedidoProducto(ArrayList<PedidoProducto> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }
}
