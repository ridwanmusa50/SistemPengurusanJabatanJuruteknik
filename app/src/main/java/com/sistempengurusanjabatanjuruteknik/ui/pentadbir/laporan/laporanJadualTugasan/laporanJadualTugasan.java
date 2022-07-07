package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class laporanJadualTugasan extends Fragment implements penyambungJadual.OnTugasListener
{
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private FirebaseFirestore db;
    private ArrayList<Tugas> list = new ArrayList<>();
    private penyambungJadual penyambung;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laporan_jadual_tugasan, container, false);

        recyclerView = v.findViewById(R.id.senaraiTugas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh = v.findViewById(R.id.refresh);
        db = FirebaseFirestore.getInstance();

        penyambung = new penyambungJadual(getContext(), list, this::onTugasClick);
        recyclerView.setAdapter(penyambung);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                db.collection("JadualTugasan").orderBy("idJadual", Query.Direction.DESCENDING)
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
                                        list.add(dc.getDocument().toObject(Tugas.class));
                                    }
                                    penyambung.notifyDataSetChanged();
                                }
                            }
                        });
                refresh.setRefreshing(false);
            }
        });

        db.collection("JadualTugasan").orderBy("idJadual", Query.Direction.DESCENDING)
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
                                list.add(dc.getDocument().toObject(Tugas.class));
                            }
                            penyambung.notifyDataSetChanged();
                        }
                    }
                });

        return v;
    }

    // tugas ditekan
    @Override
    public void onTugasClick(int position) {
        Intent intent = new Intent(getContext(), senaraiPenuhTugasPentadbir.class);
        intent.putExtra("lokasi", list.get(position).getIdJadual());
        startActivity(intent);
    }
}
