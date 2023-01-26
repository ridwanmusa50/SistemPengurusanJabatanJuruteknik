package com.sistempengurusanjabatanjuruteknik.ui.juruteknik.lamanUtama;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.Tugas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class penyambungJadual extends RecyclerView.Adapter<penyambungJadual.ViewHolder> {

    private final Context context;
    private final ArrayList<Tugas> list;
    private FirebaseFirestore db;
    String idJadual;

    public penyambungJadual(Context context, ArrayList<Tugas> list, String idJadual){
        this.context = context;
        this.list = list;
        this.idJadual = idJadual;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.senarai_jadual_juruteknik, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tugasJadual.setText(list.get(position).tugasJadual);
        db = FirebaseFirestore.getInstance();

        db.collection("JadualTugasan").document(idJadual)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> friendsMap = document.getData();
                            assert friendsMap != null;
                            for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                if (entry.getKey().equals("statusTugas")) {
                                    Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                    int i = position + 1;
                                    for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                        String in = "" + i;
                                        if (dataEntry.getKey().equals(in)) {
                                            if (dataEntry.getValue().toString().equals("SELESAI"))
                                            {
                                                holder.ikon.setImageResource(R.drawable.ikon_double_arrow_right_biru);
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d("TAG", "No such document");
                        }
                    } else {
                        Log.d("TAG", "get failed with ", task.getException());
                    }
                });

        holder.row.setOnClickListener(v -> {
            db = FirebaseFirestore.getInstance();

            db.collection("JadualTugasan").document(idJadual)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> friendsMap1 = document.getData();
                                assert friendsMap1 != null;
                                for (Map.Entry<String, Object> entry1 : friendsMap1.entrySet()) {
                                    if (entry1.getKey().equals("statusTugas")) {
                                        Map<String, Object> newFriendMap = (Map<String, Object>) entry1.getValue();
                                        int i = position + 1;
                                        for (Map.Entry<String, Object> dataEntry1 : newFriendMap.entrySet()) {
                                            String in = "" + i;

                                            if (dataEntry1.getKey().equals(in)) {
                                                if (dataEntry1.getValue().equals("TIDAK SELESAI"))
                                                {
                                                    AlertDialog.Builder builder = new AlertDialog.Builder(context)
                                                            .setTitle("Kemaskini Status Tugas")
                                                            .setMessage("Anda pasti dengan keputusan ini?")
                                                            .setIcon(R.drawable.ikon_info)
                                                            .setPositiveButton("SELESAI", (dialog, which) -> db.collection("JadualTugasan").document(idJadual)
                                                                    .get()
                                                                    .addOnCompleteListener(task1 -> {
                                                                        if (task1.isSuccessful()) {
                                                                            DocumentSnapshot document1 = task1.getResult();
                                                                            if (document1.exists()) {
                                                                                Map<String, Object> friendsMap = document1.getData();
                                                                                assert friendsMap != null;
                                                                                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                                                                    if (entry.getKey().equals("statusTugas")) {
                                                                                        Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                                                                        int i1 = position + 1;
                                                                                        for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                                                                            String in1 = "" + i1;

                                                                                            Map<String, Object> map1 = new HashMap<>();

                                                                                            for (Map.Entry<String, Object> data : newFriend0Map.entrySet()) {
                                                                                                map1.put(data.getKey(), data.getValue());
                                                                                            }

                                                                                            if (dataEntry.getKey().equals(in1)) {
                                                                                                map1.put(dataEntry.getKey(), "SELESAI");

                                                                                                Map<String, Object> map = new HashMap<>();
                                                                                                map.put("statusTugas", map1);

                                                                                                db.collection("JadualTugasan").document(idJadual)
                                                                                                        .update(map)
                                                                                                        .addOnSuccessListener(unused -> holder.ikon.setImageResource(R.drawable.ikon_double_arrow_right_biru));
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                Log.d("TAG", "No such document");
                                                                            }
                                                                        } else {
                                                                            Log.d("TAG", "get failed with ", task1.getException());
                                                                        }
                                                                    }))
                                                            .setNegativeButton("KEMBALI", (dialog, which) -> {
                                                            });
                                                    builder.show();
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    });
            });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final TextView tugasJadual;
        private final ImageView ikon;
        private final LinearLayout row;

        public ViewHolder(View itemView){
            super(itemView);

            tugasJadual = itemView.findViewById(R.id.tugasJadual);
            ikon = itemView.findViewById(R.id.ikon);
            row = itemView.findViewById(R.id.row);
        }
    }
}
