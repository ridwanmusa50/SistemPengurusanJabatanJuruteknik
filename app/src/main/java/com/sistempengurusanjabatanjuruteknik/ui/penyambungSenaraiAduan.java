package com.sistempengurusanjabatanjuruteknik.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class penyambungSenaraiAduan extends RecyclerView.Adapter<penyambungSenaraiAduan.MyViewHolder>
{
    private Context context;
    private ArrayList<Aduan> list;
    private OnAduanListener mOnAduanListener;

    public penyambungSenaraiAduan(Context context, ArrayList<Aduan> list, OnAduanListener onAduanListener) {
        this.context = context;
        this.list = list;
        this.mOnAduanListener = onAduanListener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.senarai_aduan, parent, false);
        return new MyViewHolder(v, mOnAduanListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position)
    {
        Aduan aduan = list.get(position);
        holder.idAduan.setText(aduan.getIdAduan());
        holder.idMesin.setText(aduan.getIdMesin());
        holder.huraianAduan.setText(aduan.getHuraianAduan());
        holder.tarikhAduan.setText(aduan.getTarikhAduan());
        holder.masaAduan.setText(aduan.getMasaAduan());
    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView idAduan, idMesin, huraianAduan, tarikhAduan, masaAduan;
        OnAduanListener onAduanListener;

        public MyViewHolder(@NonNull View itemView, OnAduanListener onAduanListener)
        {
            super(itemView);

            idAduan = itemView.findViewById(R.id.idAduan);
            idMesin = itemView.findViewById(R.id.idMesin);
            huraianAduan = itemView.findViewById(R.id.hurainAduan);
            tarikhAduan = itemView.findViewById(R.id.tarikhAduan);
            masaAduan = itemView.findViewById(R.id.masaAduan);
            this.onAduanListener = onAduanListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onAduanListener.onAduanClick(getAdapterPosition());
        }
    }

    public interface OnAduanListener {
        void onAduanClick(int position);

    }
}
