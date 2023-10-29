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

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentLaporanJadualTugasanBinding;

import java.util.ArrayList;

public class laporanJadualTugasan extends Fragment implements penyambungJadual.OnTugasListener
{
    private FirebaseFirestore db;
    private final ArrayList<Tugas> list = new ArrayList<>();
    private penyambungJadual penyambung;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentLaporanJadualTugasanBinding binding = FragmentLaporanJadualTugasanBinding.inflate(inflater, container, false);

        binding.senaraiTugas.setHasFixedSize(true);
        binding.senaraiTugas.setLayoutManager(new LinearLayoutManager(getContext()));
        
        db = FirebaseFirestore.getInstance();

        penyambung = new penyambungJadual(list, this);
        binding.senaraiTugas.setAdapter(penyambung);

        binding.refresh.setOnRefreshListener(() -> {
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
            binding.refresh.setRefreshing(false);
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

        return binding.getRoot();
    }

    // tugas ditekan
    @Override
    public void onTugasClick(int position) {
        Intent intent = new Intent(getContext(), senaraiPenuhTugasPentadbir.class);
        intent.putExtra("lokasi", list.get(position).getIdJadual());
        startActivity(intent);
    }
}
