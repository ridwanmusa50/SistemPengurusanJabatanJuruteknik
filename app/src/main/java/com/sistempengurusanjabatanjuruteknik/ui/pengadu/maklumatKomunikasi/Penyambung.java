package com.sistempengurusanjabatanjuruteknik.ui.pengadu.maklumatKomunikasi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class Penyambung extends RecyclerView.Adapter<Penyambung.MyViewHolder>
{
    private Context context;
    private ArrayList<Pengguna> list;

    public Penyambung(Context context, ArrayList<Pengguna> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.senarai_pengguna, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        Pengguna pengguna = list.get(position);
        holder.namaPenuh.setText(pengguna.getNamaPenuh());
        holder.jawatanPengguna.setText(pengguna.getJawatanPengguna());
        holder.nomborTelefon.setText(pengguna.getNomborTelefon());
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView namaPenuh, jawatanPengguna, nomborTelefon;

        public MyViewHolder(@NonNull View itemView)
        {
            super(itemView);

            namaPenuh = itemView.findViewById(R.id.namaPenuh);
            jawatanPengguna = itemView.findViewById(R.id.jawatanPengguna);
            nomborTelefon = itemView.findViewById(R.id.nomborTelefon);
        }
    }
}
