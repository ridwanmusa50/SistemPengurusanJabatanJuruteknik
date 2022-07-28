package com.sistempengurusanjabatanjuruteknik.ui.pengadu.maklumatKomunikasi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;

public class maklumatKomunikasi extends AppCompatActivity
{
    private RecyclerView recyclerView;
    private SwipeRefreshLayout refresh;
    private Penyambung penyambung;
    private ArrayList<Pengguna> list;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maklumat_komunikasi);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.tema_bar));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        refresh = findViewById(R.id.refresh);
        recyclerView = findViewById(R.id.userlist);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<>();
        penyambung = new Penyambung(this, list);
        recyclerView.setAdapter(penyambung);
        list.clear();
        penyambung.notifyDataSetChanged();

        paparPengguna();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                penyambung.notifyDataSetChanged();
                paparPengguna();
                refresh.setRefreshing(false);
            }
        });

    }

    private void paparPengguna()
    {
        mAuth.signInWithEmailAndPassword("pengadu@gmail.com", "kw11_mppg0301")
                .addOnCompleteListener(maklumatKomunikasi.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            db.collection("Pengguna").orderBy("jawatanPengguna", Query.Direction.DESCENDING)
                                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                        @Override
                                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                            if (error != null)
                                            {
                                                Log.e("Firestore bermasalah", error.getMessage());
                                            }

                                            for (DocumentChange dc : value.getDocumentChanges())
                                            {
                                                String emel = dc.getDocument().get("emelPengguna").toString().trim();

                                                mAuth.fetchSignInMethodsForEmail(emel)
                                                                .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
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
                                                                    }
                                                                });
                                            }
                                        }
                                    });
                            mAuth.signOut();
                        }
                        else
                        {
                            Log.w(TAG, "Log masuk tidak berhasil: ", task.getException());
                            Toast.makeText(maklumatKomunikasi.this, "Sistem tidak dapat mengakses pangkalan data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}