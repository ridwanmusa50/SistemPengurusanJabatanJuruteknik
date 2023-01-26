// Used to show list user contact information
package com.sistempengurusanjabatanjuruteknik.ui.pengadu.maklumatKomunikasi;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;
import java.util.Objects;

public class maklumat_pengguna extends Fragment {
    private SwipeRefreshLayout refresh;
    private Penyambung penyambung;
    private ArrayList<Pengguna> list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_maklumat_pengguna, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        refresh = v.findViewById(R.id.refresh);
        RecyclerView recyclerView = v.findViewById(R.id.userlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<>();
        penyambung = new Penyambung(getContext(), list);
        recyclerView.setAdapter(penyambung);
        list.clear();
        penyambung.notifyDataSetChanged();

        paparPengguna();

        refresh.setOnRefreshListener(() -> {
            list.clear();
            penyambung.notifyDataSetChanged();
            paparPengguna();
            refresh.setRefreshing(false);
        });
        return v;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void paparPengguna()
    {
        db.collection("Pengguna").orderBy("jawatanPengguna", Query.Direction.DESCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null)
                    {
                        Log.e("Firestore bermasalah", error.getMessage());
                    }
                    assert value != null;
                    for (DocumentChange dc : value.getDocumentChanges())
                    {
                        String emel = Objects.requireNonNull(dc.getDocument().get("emelPengguna")).toString().trim();

                        mAuth.fetchSignInMethodsForEmail(emel)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        boolean isNewUser = task.getResult().getSignInMethods().isEmpty();

                                        if (!isNewUser) {
                                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                                list.add(dc.getDocument().toObject(Pengguna.class));
                                            }
                                            penyambung.notifyDataSetChanged();
                                        } else {
                                            Log.e("TAG", "Ini merupakan pengguna lama yang sudah berhenti!");
                                        }
                                    }
                                });
                    }
                });
    }
}