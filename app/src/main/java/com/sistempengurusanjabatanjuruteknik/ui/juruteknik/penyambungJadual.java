package com.sistempengurusanjabatanjuruteknik.ui.juruteknik;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.SenaraiJadualJuruteknikBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class penyambungJadual extends RecyclerView.Adapter<penyambungJadual.ViewHolder> {

    private final ArrayList<Tugas> list;
    public String idJadual;
    @SuppressLint("SimpleDateFormat") public static String tarikhSemalam = new SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis() - 24*60*60*1000);


    public penyambungJadual(ArrayList<Tugas> list, String idJadual){
        this.list = list;
        this.idJadual = idJadual;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SenaraiJadualJuruteknikBinding binding = SenaraiJadualJuruteknikBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent.getContext(), idJadual);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Tugas data = list.get(position);
        holder.bind(data, position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private final SenaraiJadualJuruteknikBinding binding;
        private final Context context;
        private final String idJadual;
        private FirebaseFirestore db;

        public ViewHolder(SenaraiJadualJuruteknikBinding binding, Context context, String idJadual){
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            this.idJadual = idJadual;
        }

        /** @noinspection unchecked*/
        public void bind(Tugas data, int position) {
            binding.tugasJadual.setText(data.tugasJadual);
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
                                                    binding.ikon.setImageResource(R.drawable.ikon_double_arrow_right_biru);
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

            binding.row.setOnClickListener(v -> {
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
                                                                .setPositiveButton("SELESAI", (dialog, which) ->
                                                                        db.collection("JadualTugasan").document(idJadual)
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

                                                                                                        Map<String, Object> map1 = new HashMap<>(newFriend0Map);

                                                                                                        if (dataEntry.getKey().equals(in1)) {
                                                                                                            map1.put(dataEntry.getKey(), "SELESAI");

                                                                                                            Map<String, Object> map = new HashMap<>();
                                                                                                            map.put("statusTugas", map1);

                                                                                                            db.collection("JadualTugasan").document(idJadual)
                                                                                                                    .update(map)
                                                                                                                    .addOnSuccessListener(unused -> binding.ikon.setImageResource(R.drawable.ikon_double_arrow_right_biru));
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

                                                                                    db.collection("JadualTugasan")
                                                                                            .whereEqualTo("tarikhJadual", tarikhSemalam)
                                                                                            .get()
                                                                                            .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                                                                                List<DocumentSnapshot> documentSnapshots1 = queryDocumentSnapshots1.getDocuments();

                                                                                                for (DocumentSnapshot snapshot: documentSnapshots1)
                                                                                                {
                                                                                                    db.collection("JadualTugasan").document(snapshot.getId())
                                                                                                            .get()
                                                                                                            .addOnCompleteListener(task2 -> {
                                                                                                                if (task2.isSuccessful()) {
                                                                                                                    DocumentSnapshot document1 = task2.getResult();
                                                                                                                    if (document1.exists()) {
                                                                                                                        Map<String, Object> friendsMap = document1.getData();
                                                                                                                        assert friendsMap != null;
                                                                                                                        for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                                                                                                            if (entry.getKey().equals("statusTugas")) {
                                                                                                                                Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                                                                                                                int i1 = position + 1;

                                                                                                                                if (i1 <= newFriend0Map.size()) {
                                                                                                                                    for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                                                                                                                        String in1 = "" + i1;

                                                                                                                                        Map<String, Object> map1 = new HashMap<>(newFriend0Map);

                                                                                                                                        if (dataEntry.getKey().equals(in1)) {
                                                                                                                                            map1.put(dataEntry.getKey(), "SELESAI");

                                                                                                                                            Map<String, Object> map = new HashMap<>();
                                                                                                                                            map.put("statusTugas", map1);

                                                                                                                                            db.collection("JadualTugasan").document(snapshot.getId())
                                                                                                                                                    .update(map);
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                }
                                                                                                                            }
                                                                                                                        }
                                                                                                                    } else {
                                                                                                                        Log.d("TAG", "No such document");
                                                                                                                    }
                                                                                                                }
                                                                                                                else
                                                                                                                {
                                                                                                                    Log.d("TAG", "get failed with ", task2.getException());
                                                                                                                }
                                                                                                            });
                                                                                                }});
                                                                                })
                                                                )
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
    }
}