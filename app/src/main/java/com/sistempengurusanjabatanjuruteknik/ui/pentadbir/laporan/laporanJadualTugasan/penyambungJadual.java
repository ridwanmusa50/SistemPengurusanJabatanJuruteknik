// Used to display the schedule id and the date.

package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.databinding.SenaraiJadualJuruteknikLaporanBinding;

import java.util.ArrayList;

public class penyambungJadual extends RecyclerView.Adapter<penyambungJadual.ViewHolder> {

    private final ArrayList<Tugas> list;
    private final OnTugasListener mTugasListener;

    public penyambungJadual(ArrayList<Tugas> list, OnTugasListener onTugasListener){
        this.list = list;
        this.mTugasListener = onTugasListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SenaraiJadualJuruteknikLaporanBinding binding = SenaraiJadualJuruteknikLaporanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, mTugasListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tugas tugas = list.get(position);
        holder.bind(tugas);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final SenaraiJadualJuruteknikLaporanBinding binding;
        OnTugasListener onTugasListener;

        public ViewHolder(SenaraiJadualJuruteknikLaporanBinding binding, OnTugasListener mTugasListener){
            super(binding.getRoot());
            this.binding = binding;
            this.onTugasListener = mTugasListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTugasListener.onTugasClick(getAdapterPosition());
        }

        public void bind(Tugas data) {
            binding.idJadual.setText(data.getIdJadual());
            binding.tarikhJadual.setText(data.getTarikhJadual());
        }
    }

    public interface OnTugasListener {
        void onTugasClick(int position);
    }
}
