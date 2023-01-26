// Used to display the list of the task schedule by using recycleView
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

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
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class laporanJadualTugasan extends Fragment implements penyambungJadual.OnTugasListener
{
    private SwipeRefreshLayout refresh;
    private FirebaseFirestore db;
    private final ArrayList<Tugas> list = new ArrayList<>();
    private penyambungJadual penyambung;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laporan_jadual_tugasan, container, false);

        RecyclerView recyclerView = v.findViewById(R.id.senaraiTugas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh = v.findViewById(R.id.refresh);
        db = FirebaseFirestore.getInstance();

        penyambung = new penyambungJadual(getContext(), list, this);
        recyclerView.setAdapter(penyambung);

        refresh.setOnRefreshListener(() -> {
            list.clear();
            db.collection("JadualTugasan").orderBy("idJadual", Query.Direction.DESCENDING)
                    .addSnapshotListener((value, error) -> {
                        if (error != null)
                        {
                            Log.e("Firestore bermasalah", error.getMessage());
                            return;
                        }

                        assert value != null;
                        for (DocumentChange dc : value.getDocumentChanges())
                        {
                            if (dc.getType() == DocumentChange.Type.ADDED)
                            {
                                list.add(dc.getDocument().toObject(Tugas.class));
                            }
                            penyambung.notifyDataSetChanged();
                        }
                    });
            refresh.setRefreshing(false);
        });

        db.collection("JadualTugasan").orderBy("idJadual", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null)
                    {
                        Log.e("Firestore bermasalah", error.getMessage());
                        return;
                    }

                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges())
                    {
                        if (dc.getType() == DocumentChange.Type.ADDED)
                        {
                            list.add(dc.getDocument().toObject(Tugas.class));
                        }
                        penyambung.notifyDataSetChanged();
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
