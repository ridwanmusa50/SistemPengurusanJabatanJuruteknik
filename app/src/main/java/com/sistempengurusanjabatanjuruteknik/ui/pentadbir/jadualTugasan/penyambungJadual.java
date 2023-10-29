// Used to insert the task to senarai_jadual.xml
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.SenaraiJadualBinding;

import java.util.ArrayList;

public class penyambungJadual extends RecyclerView.Adapter<penyambungJadual.ViewHolder> {

    private final ArrayList<Tugas> list;

    public penyambungJadual(ArrayList<Tugas> list){
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SenaraiJadualBinding binding = SenaraiJadualBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent.getContext(), list);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Tugas data = list.get(position);
        holder.bind(data, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private final SenaraiJadualBinding binding;
        private final Context context;
        private final ArrayList<Tugas> list;

        public ViewHolder(SenaraiJadualBinding binding, Context context, ArrayList<Tugas> list){
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            this.list = list;
        }

        public void bind(Tugas data, int position) {
            binding.tugasJadual.setText(data.tugasJadual);

            binding.row.setOnClickListener(v -> {
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.tambah_kemaskini_jadual);

                EditText tugasJadual1 = dialog.findViewById(R.id.tugasJadual);
                Button butangKemaskini1 = dialog.findViewById(R.id.butangTambah);
                TextView tajukKemaskiniTugas1 = dialog.findViewById(R.id.tajukTambahTugas);

                butangKemaskini1.setText("Kemaskini");
                tajukKemaskiniTugas1.setText("Kemaskini Tugas");
                tugasJadual1.setText(data.tugasJadual);

                butangKemaskini1.setOnClickListener(v1 -> {
                    String tugasJadual = tugasJadual1.getText().toString().trim();

                    if (tugasJadual.isEmpty())
                    {
                        tugasJadual1.setError("Tugasan perlu diisi!");
                        tugasJadual1.requestFocus();
                    }
                    else
                    {
                        list.set(position, new Tugas(tugasJadual));
                        notifyItemChanged(position);
                        dialog.dismiss();
                    }
                });
                dialog.show();
            });

            binding.row.setOnLongClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Buang Tugas")
                        .setMessage("Anda pasti dengan keputusan ini?")
                        .setIcon(R.drawable.ic_baseline_delete_24)
                        .setPositiveButton("Ya", (dialog, which) -> {
                            list.remove(position);
                            notifyItemRemoved(position);
                        })
                        .setNegativeButton("Tidak", (dialog, which) -> {

                        });
                builder.show();

                return true;
            });
        }
    }
}
