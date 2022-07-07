package com.sistempengurusanjabatanjuruteknik.ui.juruteknik.jadualTugasan;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.Tugas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class penyambungJadual extends RecyclerView.Adapter<penyambungJadual.ViewHolder> {

    Context context;
    ArrayList<Tugas> list;
    FirebaseFirestore db;
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
        ViewHolder viewHolder = new ViewHolder(v);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tugasJadual.setText(list.get(position).tugasJadual);
        db = FirebaseFirestore.getInstance();

        db.collection("JadualTugasan").document(idJadual)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Map<String, Object> friendsMap = document.getData();
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
                    }
                });

        holder.row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db = FirebaseFirestore.getInstance();

                db.collection("JadualTugasan").document(idJadual)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        Map<String, Object> friendsMap1 = document.getData();
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
                                                                    .setPositiveButton("SELESAI", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                            db.collection("JadualTugasan").document(idJadual)
                                                                                    .get()
                                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                            if (task.isSuccessful()) {
                                                                                                DocumentSnapshot document = task.getResult();
                                                                                                if (document.exists()) {
                                                                                                    Map<String, Object> friendsMap = document.getData();
                                                                                                    for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                                                                                        if (entry.getKey().equals("statusTugas")) {
                                                                                                            Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                                                                                            int i = position + 1;
                                                                                                            for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                                                                                                String in = "" + i;

                                                                                                                Map<String, Object> map1 = new HashMap<>();

                                                                                                                for (Map.Entry<String, Object> data : newFriend0Map.entrySet()) {
                                                                                                                    map1.put(data.getKey(), data.getValue());
                                                                                                                }

                                                                                                                if (dataEntry.getKey().equals(in)) {
                                                                                                                    map1.put(dataEntry.getKey(), "SELESAI");

                                                                                                                    Map<String, Object> map = new HashMap<>();
                                                                                                                    map.put("statusTugas", map1);

                                                                                                                    db.collection("JadualTugasan").document(idJadual)
                                                                                                                            .update(map)
                                                                                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onSuccess(Void unused) {
                                                                                                                                    holder.ikon.setImageResource(R.drawable.ikon_double_arrow_right_biru);
                                                                                                                                }
                                                                                                                            });
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
                                                                                        }
                                                                                    });
                                                                        }
                                                                    })
                                                                    .setNegativeButton("KEMBALI", new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                        }
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
                            }
                        });
                }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tugasJadual;
        ImageView ikon;
        LinearLayout row;

        public ViewHolder(View itemView){
            super(itemView);

            tugasJadual = itemView.findViewById(R.id.tugasJadual);
            ikon = itemView.findViewById(R.id.ikon);
            row = itemView.findViewById(R.id.row);
        }
    }
}
