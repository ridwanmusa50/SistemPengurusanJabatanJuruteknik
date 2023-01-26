package com.sistempengurusanjabatanjuruteknik.ui.juruteknik.jadualTugasan;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.Tugas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class jadualTugasan extends Fragment
{
    private FirebaseFirestore db; // declare Firebase Firestore variable
    ArrayList<Tugas> list = new ArrayList<>();
    String idJadual = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_jadual_tugasan_juruteknik, container, false);

        RecyclerView recylerview = v.findViewById(R.id.senaraiTugas);
        db = FirebaseFirestore.getInstance();

        recylerview.setLayoutManager(new LinearLayoutManager(getContext()));
        penyambungJadual penyambung = new penyambungJadual(getContext(), list, idJadual);
        recylerview.setAdapter(penyambung);

        String tarikh = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        db.collection("JadualTugasan")
                .whereEqualTo("tarikhJadual", tarikh)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                    for (DocumentSnapshot snapshot: documentSnapshots)
                    {
                       idJadual = snapshot.getId();
                       penyambung.idJadual = idJadual;
                    }

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
                                                            list.add(new Tugas(dataEntry.getValue().toString()));
                                                            penyambung.notifyItemInserted(list.size() - 1);
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
        return v;
    }
}