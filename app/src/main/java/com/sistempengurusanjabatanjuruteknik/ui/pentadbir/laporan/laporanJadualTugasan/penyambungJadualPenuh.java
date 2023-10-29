// used to display list of task in senarai_jadual.xml
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.databinding.SenaraiJadualBinding;

import java.util.ArrayList;

public class penyambungJadualPenuh extends RecyclerView.Adapter<penyambungJadualPenuh.ViewHolder> {

    private final ArrayList<TugasPenuh> list;

    public penyambungJadualPenuh(ArrayList<TugasPenuh> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SenaraiJadualBinding binding = SenaraiJadualBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TugasPenuh data = list.get(position);
        holder.bind(data);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private final SenaraiJadualBinding binding;

        public ViewHolder(SenaraiJadualBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(TugasPenuh data) {
            binding.tugasJadual.setText(data.tugasJadual);
        }
    }
}
