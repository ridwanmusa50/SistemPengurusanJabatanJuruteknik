// Used to set value for each list data got to senarai_aduan.xml
package com.sistempengurusanjabatanjuruteknik.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.databinding.SenaraiAduanBinding;

import java.util.ArrayList;

public class penyambungSenaraiAduan extends RecyclerView.Adapter<penyambungSenaraiAduan.MyViewHolder>
{
    private final ArrayList<Aduan> list;
    private final OnAduanListener mOnAduanListener;

    public penyambungSenaraiAduan(ArrayList<Aduan> list, OnAduanListener onAduanListener) {
        this.list = list;
        this.mOnAduanListener = onAduanListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        SenaraiAduanBinding binding = SenaraiAduanBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding, mOnAduanListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        Aduan aduan = list.get(position);
        holder.bind(aduan);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final SenaraiAduanBinding binding;
        OnAduanListener onAduanListener;

        public MyViewHolder(SenaraiAduanBinding binding, OnAduanListener onAduanListener)
        {
            super(binding.getRoot());
            this.binding = binding;
            this.onAduanListener = onAduanListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAduanListener.onAduanClick(getAdapterPosition());
        }

        public void bind(Aduan data) {
            binding.idAduan.setText(data.getIdAduan());
            binding.idMesin.setText(data.getIdMesin());
            binding.hurainAduan.setText(data.getHuraianAduan());
            binding.tarikhAduan.setText(data.getTarikhAduan());
            binding.masaAduan.setText(data.getMasaAduan());
        }
    }

    public interface OnAduanListener {
        void onAduanClick(int position);

    }
}
