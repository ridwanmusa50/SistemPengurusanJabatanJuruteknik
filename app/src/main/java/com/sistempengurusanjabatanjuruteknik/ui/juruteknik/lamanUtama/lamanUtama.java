package com.sistempengurusanjabatanjuruteknik.ui.juruteknik.lamanUtama;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentLamanUtamaJuruteknikBinding;
import com.sistempengurusanjabatanjuruteknik.ui.Aduan;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.Tugas;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.penyambungJadual;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.senaraiPenuhAduan;
import com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class lamanUtama extends Fragment implements com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan.OnAduanListener
{
    private FirebaseFirestore db;
    private penyambungSenaraiAduan penyambungSenaraiAduan;
    private ArrayList<Aduan> list;
    private final ArrayList<Tugas> tugas = new ArrayList<>();
    private String idJadual = "";

    String tarikh = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    @SuppressLint("SimpleDateFormat") String tarikhSemalam = new SimpleDateFormat("dd/MM/yyyy").format(System.currentTimeMillis() - 24*60*60*1000);

    /** @noinspection unchecked*/
    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        FragmentLamanUtamaJuruteknikBinding binding = FragmentLamanUtamaJuruteknikBinding.inflate(inflater, container, false);

        binding.senaraiAduan.setHasFixedSize(true);
        binding.senaraiAduan.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        penyambungSenaraiAduan = new penyambungSenaraiAduan(list, this);
        binding.senaraiAduan.setAdapter(penyambungSenaraiAduan);

        binding.senaraiTugas.setLayoutManager(new LinearLayoutManager(getContext()));
        penyambungJadual penyambung = new penyambungJadual(tugas, idJadual);
        binding.senaraiTugas.setAdapter(penyambung);

        db = FirebaseFirestore.getInstance();
        tugas.clear();
        penyambung.notifyDataSetChanged();
        list.clear();
        penyambungSenaraiAduan.notifyDataSetChanged();

        binding.refresh.setOnRefreshListener(() -> {
            list.clear();
            tunjukMaklumat();
            binding.refresh.setRefreshing(false);
        });

        tunjukMaklumat();

        db.collection("JadualTugasan")
                .whereEqualTo("tarikhJadual", tarikh)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot snapshot : documentSnapshots) {
                        idJadual = snapshot.getId();
                        penyambung.idJadual = idJadual;
                    }

                    if (!idJadual.equals(""))
                    {
                        db.collection("JadualTugasan").document(idJadual)
                                .get()
                                .addOnSuccessListener(documentSnapshot -> {
                                    if (!Objects.requireNonNull(documentSnapshot.get("tugasJadual")).toString().contains(tarikhSemalam))
                                    {
                                        Map<String, Object> map1 = new HashMap<>();
                                        Map<String, Object> map = new HashMap<>();
                                        Map<String, Object> bigmap = new HashMap<>();
                                        final int[] k = {1};
                                        final int[] l = {1};

                                        db.collection("JadualTugasan")
                                                .whereEqualTo("tarikhJadual", tarikhSemalam)
                                                .get()
                                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                                    List<DocumentSnapshot> documentSnapshots1 = queryDocumentSnapshots1.getDocuments();

                                                    for (DocumentSnapshot snapshot: documentSnapshots1)
                                                    {
                                                        db.collection("JadualTugasan").document(snapshot.getId())
                                                                    .get()
                                                                    .addOnCompleteListener(task -> {
                                                                        if (task.isSuccessful()) {
                                                                            DocumentSnapshot document = task.getResult();
                                                                            if (document.exists()) {
                                                                                Map<String, Object> friendsMap = document.getData();
                                                                                assert friendsMap != null;
                                                                                for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                                                                    if (entry.getKey().equals("statusTugas")) {
                                                                                        Map<String, Object> newFriendMap = (Map<String, Object>) entry.getValue();
                                                                                        int i = 1;
                                                                                        for (Map.Entry<String, Object> dataEntry : newFriendMap.entrySet()) {
                                                                                            String in = "" + i;
                                                                                            if (dataEntry.getKey().equals(in)) {
                                                                                                if (dataEntry.getValue().equals("TIDAK SELESAI")) {
                                                                                                    for (Map.Entry<String, Object> entryBaru : friendsMap.entrySet()) {
                                                                                                        if (entryBaru.getKey().equals("tugasJadual")) {
                                                                                                            Map<String, Object> newFriendmap2 = (Map<String, Object>) entryBaru.getValue();
                                                                                                            for (Map.Entry<String, Object> dataEntry1 : newFriendmap2.entrySet()) {
                                                                                                                String ti = "" + k[0];
                                                                                                                if (dataEntry1.getKey().equals(in)) {
                                                                                                                    map1.put(ti, dataEntry1.getValue() + " [" + tarikhSemalam + "]");
                                                                                                                    map.put(ti, "TIDAK SELESAI");
                                                                                                                    k[0]++;
                                                                                                                }
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                            i++;
                                                                                        }
                                                                                    }
                                                                                }
                                                                                k[0] = map.size();
                                                                                l[0] = k[0];
                                                                            }
                                                                            else
                                                                            {
                                                                                Log.d("TAG", "No such document");
                                                                            }
                                                                        }
                                                                        else
                                                                        {
                                                                            Log.d("TAG", "get failed with ", task.getException());
                                                                        }
                                                                    });

                                                            db.collection("JadualTugasan")
                                                                    .whereEqualTo("tarikhJadual", tarikh)
                                                                    .get()
                                                                    .addOnSuccessListener(queryDocumentSnapshots11 -> {
                                                                        if (!idJadual.equals("")) {
                                                                            db.collection("JadualTugasan").document(idJadual)
                                                                                    .get()
                                                                                    .addOnCompleteListener(task1 -> {
                                                                                        if (task1.isSuccessful()) {
                                                                                            DocumentSnapshot document1 = task1.getResult();
                                                                                            if (document1.exists()) {
                                                                                                Map<String, Object> friendsMap1 = document1.getData();
                                                                                                assert friendsMap1 != null;
                                                                                                for (Map.Entry<String, Object> entry3 : friendsMap1.entrySet()) {
                                                                                                    String ti;
                                                                                                    if (entry3.getKey().equals("statusTugas"))
                                                                                                    {
                                                                                                        Map<String, Object> newFriend0Map = (Map<String, Object>) entry3.getValue();
                                                                                                        for (Map.Entry<String, Object> dataEntry3 : newFriend0Map.entrySet()) {
                                                                                                            k[0]++;
                                                                                                            ti = "" + k[0];

                                                                                                            map.put(ti, dataEntry3.getValue());
                                                                                                        }
                                                                                                    }

                                                                                                    if (entry3.getKey().equals("tugasJadual")) {
                                                                                                        Map<String, Object> newFriend0Map = (Map<String, Object>) entry3.getValue();
                                                                                                        for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                                                                                            l[0]++;
                                                                                                            ti = "" + l[0];

                                                                                                            map1.put(ti, dataEntry.getValue());
                                                                                                        }
                                                                                                    }
                                                                                                }
                                                                                                bigmap.put("statusTugas", map);
                                                                                                bigmap.put("tugasJadual", map1);
                                                                                                db.collection("JadualTugasan").document(idJadual)
                                                                                                        .update(bigmap)
                                                                                                        .addOnSuccessListener(unused -> Log.d("TAG", "Berjaya tambah"));
                                                                                                startActivity(new Intent(getContext(), requireActivity().getClass()));
                                                                                            }
                                                                                            else
                                                                                            {
                                                                                                Log.d("TAG", "No such document");
                                                                                            }
                                                                                        }
                                                                                        else
                                                                                        {
                                                                                            Log.d("TAG", "get failed with ", task1.getException());
                                                                                        }
                                                                                    });
                                                                        }
                                                                        else
                                                                        {
                                                                            Log.d("TAG", "No such document");
                                                                        }
                                                                    });
                                                    }
                                                });
                                    }
                                });

                        db.collection("JadualTugasan")
                                .whereEqualTo("tarikhJadual", tarikh)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots12 -> {
                                    if (!idJadual.equals("")) {
                                        db.collection("JadualTugasan").document(idJadual)
                                                .get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if (document.exists()) {
                                                            Map<String, Object> friendsMap = document.getData();
                                                            assert friendsMap != null;
                                                            for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                                                if (entry.getKey().equals("tugasJadual")) {
                                                                    Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                                                    int i = 1;
                                                                    for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                                                        String in = "" + i;
                                                                        if (dataEntry.getKey().equals(in)) {
                                                                            tugas.add(new Tugas(dataEntry.getValue().toString()));
                                                                            penyambung.notifyItemInserted(tugas.size() - 1);
                                                                        }
                                                                        i++;
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
                                    }
                                    else
                                    {
                                        Log.d("TAG", "No such document");
                                    }
                                });
                    }
                });
        return binding.getRoot();
    }

    // aduan ditekan
    @Override
    public void onAduanClick(int position) {
        Intent intent = new Intent(getContext(), senaraiPenuhAduan.class);
        intent.putExtra("lokasi", list.get(position).getIdAduan());
        startActivity(intent);
    }

    @SuppressLint("NotifyDataSetChanged")
    public  void tunjukMaklumat()
    {
        db.collection("AduanKerosakan").orderBy("idAduan", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null)
                    {
                        Log.e("Firestore bermasalah", Objects.requireNonNull(error.getMessage()));
                        return;
                    }

                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges())
                    {
                        if (dc.getType() == DocumentChange.Type.ADDED)
                        {
                            // Tugas selesai sahaja dipamer
                            if (!dc.getDocument().contains("idJuruteknik"))
                            {
                                list.add(dc.getDocument().toObject(Aduan.class));
                            }
                        }
                        penyambungSenaraiAduan.notifyDataSetChanged();
                    }
                });
    }
}
