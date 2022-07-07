package com.sistempengurusanjabatanjuruteknik.ui.juruteknik.lamanUtama;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.Aduan;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.Tugas;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.senaraiPenuhAduan;
import com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class lamanUtama extends Fragment implements com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan.OnAduanListener
{
    private RecyclerView recyclerView;
    private RecyclerView senaraiTugas;
    private SwipeRefreshLayout refresh;
    private FirebaseFirestore db;
    private penyambungSenaraiAduan penyambungSenaraiAduan;
    private ArrayList<Aduan> list;
    private ArrayList<Tugas> tugas = new ArrayList<>();
    private String idJadual = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laman_utama_juruteknik, container, false);

        recyclerView = v.findViewById(R.id.senaraiAduan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        list = new ArrayList<>();
        penyambungSenaraiAduan = new penyambungSenaraiAduan(getContext(), list, this::onAduanClick);
        recyclerView.setAdapter(penyambungSenaraiAduan);

        senaraiTugas = v.findViewById(R.id.senaraiTugas);
        senaraiTugas.setLayoutManager(new LinearLayoutManager(getContext()));
        penyambungJadual penyambung = new penyambungJadual(getContext(), tugas, idJadual);
        senaraiTugas.setAdapter(penyambung);

        refresh = v.findViewById(R.id.refresh);
        db = FirebaseFirestore.getInstance();
        tugas.clear();
        penyambung.notifyDataSetChanged();
        list.clear();
        penyambungSenaraiAduan.notifyDataSetChanged();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                db.collection("AduanKerosakan").orderBy("idAduan", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null)
                                {
                                    Log.e("Firestore bermasalah", error.getMessage());
                                    return;
                                }

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
                            }
                        });
                refresh.setRefreshing(false);
            }
        });

        db.collection("AduanKerosakan").orderBy("idAduan", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null)
                                {
                                    Log.e("Firestore bermasalah", error.getMessage());
                                    return;
                                }

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
                            }
                        });

        String tarikh = new SimpleDateFormat("dd/MMM/YYYY", Locale.getDefault()).format(new Date());
        String tarikhSemalam = new SimpleDateFormat("dd/MMM/YYYY").format(System.currentTimeMillis() - 24*60*60*1000);

        db.collection("JadualTugasan")
                .whereEqualTo("tarikhJadual", tarikh)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot snapshot : documentSnapshots) {
                            idJadual = snapshot.getId();
                            penyambung.idJadual = idJadual;
                        }

                        if (!idJadual.equals(""))
                        {
                            db.collection("JadualTugasan").document(idJadual)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            if (!documentSnapshot.get("tugasJadual").toString().contains(tarikhSemalam))
                                            {
                                                Map<String, Object> map1 = new HashMap<>();
                                                Map<String, Object> map = new HashMap<>();
                                                Map<String, Object> bigmap = new HashMap<>();
                                                final int[] k = {1};
                                                final int[] l = {1};

                                                db.collection("JadualTugasan")
                                                        .whereEqualTo("tarikhJadual", tarikhSemalam)
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                            @Override
                                                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                                                                for (DocumentSnapshot snapshot: documentSnapshots)
                                                                {
                                                                    db.collection("JadualTugasan").document(snapshot.getId())
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
                                                                                            l[0] = k[0];

                                                                                            db.collection("JadualTugasan")
                                                                                                    .whereEqualTo("tarikhJadual", tarikh)
                                                                                                    .get()
                                                                                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                                                                                        @Override
                                                                                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                                                                                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                                                                                                            if (!idJadual.equals("")) {
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
                                                                                                                                            if (entry.getKey().equals("statusTugas"))
                                                                                                                                            {
                                                                                                                                                Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                                                                                                                                int i = 1;
                                                                                                                                                for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                                                                                                                                    String in = "" + i;
                                                                                                                                                    String ti = "" + k[0];

                                                                                                                                                    if (dataEntry.getKey().equals(in)) {
                                                                                                                                                        map.put(ti, dataEntry.getValue());
                                                                                                                                                        k[0]++;
                                                                                                                                                    }
                                                                                                                                                    i++;
                                                                                                                                                }
                                                                                                                                            }

                                                                                                                                            if (entry.getKey().equals("tugasJadual")) {
                                                                                                                                                Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                                                                                                                                int i = 1;
                                                                                                                                                for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                                                                                                                                    String in = "" + i;
                                                                                                                                                    String li = "" + l[0];
                                                                                                                                                    if (dataEntry.getKey().equals(in)) {
                                                                                                                                                        map1.put(li, dataEntry.getValue());
                                                                                                                                                        l[0]++;
                                                                                                                                                    }
                                                                                                                                                    i++;
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        }

                                                                                                                                        bigmap.put("statusTugas", map);
                                                                                                                                        bigmap.put("tugasJadual", map1);

                                                                                                                                        db.collection("JadualTugasan").document(idJadual)
                                                                                                                                                .update(bigmap)
                                                                                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onSuccess(Void unused) {
                                                                                                                                                        Log.d("TAG", "Berjaya tambah");
                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                    } else {
                                                                                                                                        Log.d("TAG", "No such document");
                                                                                                                                    }
                                                                                                                                } else {
                                                                                                                                    Log.d("TAG", "get failed with ", task.getException());
                                                                                                                                }
                                                                                                                            }
                                                                                                                        });
                                                                                                            }
                                                                                                            else
                                                                                                            {
                                                                                                                Log.d("TAG", "No such document");
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        } else {
                                                                                            Log.d("TAG", "No such document");
                                                                                        }
                                                                                    } else {
                                                                                        Log.d("TAG", "get failed with ", task.getException());
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                            db.collection("JadualTugasan")
                                    .whereEqualTo("tarikhJadual", tarikh)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                                            if (!idJadual.equals("")) {
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
                                                            }
                                                        });
                                            }
                                            else
                                            {
                                                Log.d("TAG", "No such document");
                                            }
                                        }
                                    });
                        }
                    }
                });
        return v;
    }

    // aduan ditekan
    @Override
    public void onAduanClick(int position) {
        Intent intent = new Intent(getContext(), senaraiPenuhAduan.class);
        intent.putExtra("lokasi", list.get(position).getIdAduan());
        startActivity(intent);
    }
}
