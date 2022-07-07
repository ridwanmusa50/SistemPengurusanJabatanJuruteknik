package com.sistempengurusanjabatanjuruteknik.ui.juruteknik.senaraiAduan;

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
import com.sistempengurusanjabatanjuruteknik.ui.Aduan;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.senaraiPenuhAduan;
import com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan;

import java.util.ArrayList;

public class senaraiAduan extends Fragment implements com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan.OnAduanListener
{
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private FirebaseFirestore db;
    private penyambungSenaraiAduan penyambungSenaraiAduan;
    private ArrayList<Aduan> list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_senarai_aduan_juruteknik, container, false);

        recyclerView = v.findViewById(R.id.senaraiAduan);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh = v.findViewById(R.id.refresh);
        db = FirebaseFirestore.getInstance();
        list = new ArrayList<>();
        penyambungSenaraiAduan = new penyambungSenaraiAduan(getContext(), list, this::onAduanClick);
        recyclerView.setAdapter(penyambungSenaraiAduan);

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
                                        list.add(dc.getDocument().toObject(Aduan.class));
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
                                list.add(dc.getDocument().toObject(Aduan.class));
                            }
                            penyambungSenaraiAduan.notifyDataSetChanged();
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
