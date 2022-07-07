package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class penyambungJadual extends RecyclerView.Adapter<penyambungJadual.ViewHolder> {

    private Context context;
    private ArrayList<Tugas> list;

    public penyambungJadual(Context context, ArrayList<Tugas> list){
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

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.tambah_kemaskini_jadual);

                EditText tugasJadual1 = dialog.findViewById(R.id.tugasJadual);
                Button butangKemaskini1 = dialog.findViewById(R.id.butangTambah);
                TextView tajukKemaskiniTugas1 = dialog.findViewById(R.id.tajukTambahTugas);

                butangKemaskini1.setText("Kemaskini");
                tajukKemaskiniTugas1.setText("Kemaskini Tugas");
                tugasJadual1.setText(list.get(position).tugasJadual);

                butangKemaskini1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tugasJadual = tugasJadual1.getText().toString().trim();

                        if (tugasJadual.isEmpty())
                        {
                            tugasJadual1.setError("Tugasan perlu diisi!");
                            tugasJadual1.requestFocus();
                            return;
                        }
                        else
                        {
                            list.set(position, new Tugas(tugasJadual));
                            notifyItemChanged(position);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        holder.row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Buang Tugas")
                        .setMessage("Anda pasti dengan keputusan ini?")
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                list.remove(position);
                                notifyItemRemoved(position);
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tugasJadual;
        LinearLayout row;

        public ViewHolder(View itemView){
            super(itemView);

            tugasJadual = itemView.findViewById(R.id.tugasJadual);
            row = itemView.findViewById(R.id.row);
        }
    }
}
