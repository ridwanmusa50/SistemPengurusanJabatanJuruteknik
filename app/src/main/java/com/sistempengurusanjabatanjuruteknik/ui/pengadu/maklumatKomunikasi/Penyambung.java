// Used to display the list data to senarai_pengguna.xml
package com.sistempengurusanjabatanjuruteknik.ui.pengadu.maklumatKomunikasi;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.databinding.SenaraiPenggunaBinding;

import java.util.ArrayList;

public class Penyambung extends RecyclerView.Adapter<Penyambung.MyViewHolder>
{
    private final ArrayList<Pengguna> list;

    public Penyambung(ArrayList<Pengguna> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SenaraiPenggunaBinding binding = SenaraiPenggunaBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Pengguna pengguna = list.get(position);
        holder.bind(pengguna);
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private final SenaraiPenggunaBinding binding;

        public MyViewHolder(SenaraiPenggunaBinding binding)
        {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pengguna data) {
            binding.namaPenuh.setText(data.getNamaPenuh());
            binding.jawatanPengguna.setText(data.getJawatanPengguna());
            binding.nomborTelefon.setText(data.getNomborTelefon());
        }
    }
}
