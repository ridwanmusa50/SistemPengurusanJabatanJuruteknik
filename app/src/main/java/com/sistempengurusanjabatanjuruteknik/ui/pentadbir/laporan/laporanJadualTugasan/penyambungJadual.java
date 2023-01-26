// Used to display the schedule id and the date.

package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class penyambungJadual extends RecyclerView.Adapter<penyambungJadual.ViewHolder> {

    private final Context context;
    private final ArrayList<Tugas> list;
    private final OnTugasListener mTugasListener;

    public penyambungJadual(Context context, ArrayList<Tugas> list, OnTugasListener onTugasListener){
        this.context = context;
        this.list = list;
        this.mTugasListener = onTugasListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.senarai_jadual_juruteknik_laporan, parent, false);

        return new ViewHolder(v, mTugasListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tugas tugas = list.get(position);
        holder.idJadual.setText(tugas.getIdJadual());
        holder.tarikhJadual.setText(tugas.getTarikhJadual());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        TextView idJadual, tarikhJadual;
        OnTugasListener onTugasListener;

        public ViewHolder(View itemView, OnTugasListener mTugasListener){
            super(itemView);

            idJadual = itemView.findViewById(R.id.idJadual);
            tarikhJadual = itemView.findViewById(R.id.tarikhJadual);
            this.onTugasListener = mTugasListener;
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            onTugasListener.onTugasClick(getAdapterPosition());
        }
    }

    public interface OnTugasListener {
        void onTugasClick(int position);
    }
}
