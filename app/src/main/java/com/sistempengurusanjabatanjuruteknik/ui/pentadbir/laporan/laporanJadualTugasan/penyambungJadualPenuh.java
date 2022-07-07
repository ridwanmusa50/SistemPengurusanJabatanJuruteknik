package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class penyambungJadualPenuh extends RecyclerView.Adapter<penyambungJadualPenuh.ViewHolder> {

    private Context context;
    private ArrayList<TugasPenuh> list;

    public penyambungJadualPenuh(Context context, ArrayList<TugasPenuh> list){
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.senarai_jadual, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tugasJadual.setText(list.get(position).tugasJadual);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tugasJadual;

        public ViewHolder(View itemView){
            super(itemView);

            tugasJadual = itemView.findViewById(R.id.tugasJadual);
        }
    }
}
