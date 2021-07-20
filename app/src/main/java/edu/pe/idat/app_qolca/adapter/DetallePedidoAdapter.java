package edu.pe.idat.app_qolca.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.pe.idat.app_qolca.databinding.DetallePedidoCardBinding;
import edu.pe.idat.app_qolca.model.DetallePedido;


public class DetallePedidoAdapter  extends RecyclerView.Adapter<DetallePedidoAdapter.ViewHolder>{
    private Context context;
    private ArrayList<DetallePedido> list;

    public DetallePedidoAdapter(Context context){
        this.context = context;
        list = new ArrayList<>();
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DetallePedidoCardBinding recyclerBinding = DetallePedidoCardBinding.inflate(layoutInflater,parent,false);
        return new DetallePedidoAdapter.ViewHolder(recyclerBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        DecimalFormat df = new DecimalFormat("#.00");
        final DetallePedido item = list.get(position);
        holder.recyclerBinding.tvDetpedMarca.setText(item.getProducto().getMarca());
        holder.recyclerBinding.tvDetpedNombre.setText(item.getProducto().getNombre());
        holder.recyclerBinding.tvDetpedPrecio.setText(String.valueOf(item.getProducto().getPrecio()));
        holder.recyclerBinding.tvDetpedCantidad.setText(String.valueOf(item.getCantidad()));
        holder.recyclerBinding.tvDetpedTotal.setText(df.format(item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        DetallePedidoCardBinding recyclerBinding;
        public ViewHolder(@NonNull @NotNull DetallePedidoCardBinding itemView) {
            super(itemView.getRoot());
            recyclerBinding = itemView;
        }
    }

    public void addProductosPedidoDetalle(ArrayList<DetallePedido> data){
        list.clear();
        list.addAll(data);
        notifyDataSetChanged();
    }
}
