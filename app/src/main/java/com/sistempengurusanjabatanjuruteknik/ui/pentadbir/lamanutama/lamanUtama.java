// used for main page for management
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.lamanutama;

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
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentLamanUtamaPentadbirBinding;
import com.sistempengurusanjabatanjuruteknik.ui.Aduan;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.senaraiPenuhAduanPentadbir;
import com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan;

import java.util.ArrayList;

public class lamanUtama extends Fragment implements com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan.OnAduanListener
{
    private FirebaseFirestore db;
    private penyambungSenaraiAduan penyambungSenaraiAduan;
    private ArrayList<Aduan> list;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentLamanUtamaPentadbirBinding binding = FragmentLamanUtamaPentadbirBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();
        binding.senaraiAduan.setHasFixedSize(true);
        binding.senaraiAduan.setLayoutManager(new LinearLayoutManager(getContext()));
        
        list = new ArrayList<>();
        penyambungSenaraiAduan = new penyambungSenaraiAduan(getContext(), list, this);
        binding.senaraiAduan.setAdapter(penyambungSenaraiAduan);

        binding.refresh.setOnRefreshListener(() -> {
            list.clear();
            db.collection("AduanKerosakan").orderBy("idAduan", Query.Direction.DESCENDING)
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
                                // Tugas selesai sahaja dipamer
                                if (!dc.getDocument().contains("idPentadbir"))
                                {
                                    list.add(dc.getDocument().toObject(Aduan.class));
                                }
                            }
                            penyambungSenaraiAduan.notifyDataSetChanged();
                        }
                    });
            binding.refresh.setRefreshing(false);
        });

        db.collection("AduanKerosakan").orderBy("idAduan", Query.Direction.DESCENDING)
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
                            // Tugas selesai sahaja dipamer
                            if (!dc.getDocument().contains("idPentadbir"))
                            {
                                list.add(dc.getDocument().toObject(Aduan.class));
                            }
                        }
                        penyambungSenaraiAduan.notifyDataSetChanged();
                    }
                });

        return binding.getRoot();
    }

    // aduan ditekan
    @Override
    public void onAduanClick(int position) {
        Intent intent = new Intent(getContext(), senaraiPenuhAduanPentadbir.class);
        intent.putExtra("lokasi", list.get(position).getIdAduan());
        startActivity(intent);
    }
}
