package com.sistempengurusanjabatanjuruteknik;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.utamaJuruteknik;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.utamaPentadbir;

public class log_masuk extends Fragment {
    private ProgressBar progressBar;
    private TextView emelPengguna1;
    private TextView idPengguna1;
    private TextView kataLaluan1;
    private FirebaseFirestore db; // istihar nama pembolehubah Firestore
    private FirebaseAuth mAuth; // istihar nama pembolehubah pengesahan Firebase
    SharedPreferences sp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_log_masuk, container, false);

        // tetapkan pembolehubah mengambil maklumat
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        sp = getActivity().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);
        emelPengguna1 = v.findViewById(R.id.emelPengguna);
        idPengguna1 = v.findViewById(R.id.idPengguna);
        kataLaluan1 = v.findViewById(R.id.kataLaluan);
        MaterialButton butangLogMasuk = v.findViewById(R.id.butangLogMasuk);
        progressBar = v.findViewById(R.id.progressBar);
        MaterialButton lupaKataLaluan = v.findViewById(R.id.lupaKataLaluan);

        butangLogMasuk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // tetapkan pembolehubah mengambil maklumat jenis String
                String emelPengguna = emelPengguna1.getText().toString().trim();
                String kataLaluan = kataLaluan1.getText().toString().trim();
                String idPengguna = idPengguna1.getText().toString().trim();

                if (emelPengguna.isEmpty()) {
                    emelPengguna1.setError("Email Pengguna perlu diisi!");
                    emelPengguna1.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(emelPengguna).matches()) {
                    emelPengguna1.setError("Sila masukkan Email Pengguna yang sah!");
                    emelPengguna1.requestFocus();
                    return;
                }

                if (idPengguna.isEmpty()) {
                    idPengguna1.setError("ID Pengguna perlu diisi!");
                    idPengguna1.requestFocus();
                    return;
                }

                if (kataLaluan.isEmpty()) {
                    kataLaluan1.setError("Kata Laluan perlu diisi!");
                    kataLaluan1.requestFocus();
                    return;
                }

                if (kataLaluan.length() < 6) {
                    kataLaluan1.setError("Minimum Panjang Kata Laluan mestilah 6 patah perkataan!");
                    kataLaluan1.requestFocus();
                    return;
                }

                if (kataLaluan.length() > 15) {
                    kataLaluan1.setError("Maksimum Panjang Kata Laluan mestilah 15 patah perkataan!");
                    kataLaluan1.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                sahkanPengguna(emelPengguna, kataLaluan, idPengguna);
            }
        });

        lupaKataLaluan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), setSemulaKataLaluan.class));
            }
        });

        return v;
    }

    private void sahkanPengguna(String emelPengguna, String kataLaluan, String idPengguna)
    {
        mAuth.signInWithEmailAndPassword(emelPengguna, kataLaluan).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    db.collection("Pengguna").document(idPengguna)
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Toast toast;
                                    if (documentSnapshot.exists()) {
                                        String jawatan = documentSnapshot.getString("jawatanPengguna");

                                        progressBar.setVisibility(View.GONE);

                                        if (emelPengguna.equals(documentSnapshot.getString("emelPengguna"))) {
                                            SharedPreferences.Editor editor = sp.edit();

                                            db.collection("Pengguna").document(idPengguna)
                                                    .update("kataLaluan", kataLaluan);
                                            db.collection("Pentadbir").document(idPengguna)
                                                    .update("kataLaluan", kataLaluan);

                                            editor.putString("idPengguna", idPengguna);
                                            editor.putString("kataLaluan", kataLaluan);
                                            editor.putString("emelPengguna", emelPengguna);
                                            editor.apply();

                                            getActivity().finish();
                                            if (jawatan.equals("JURUTEKNIK")) {
                                                progressBar.setVisibility(View.GONE);
                                                startActivity(new Intent(getContext(), utamaJuruteknik.class)); //terus ke laman utama juruteknik
                                            }
                                            else {
                                                startActivity(new Intent(getContext(), utamaPentadbir.class)); //terus ke laman utama pentadbir
                                            }
                                        } else {
                                            toast = Toast.makeText(getContext(), "Gagal untuk log masuk! ID Pengguna tidak sepadan dengan Emel", Toast.LENGTH_LONG);
                                            toast.setGravity(Gravity.CENTER, 0, 0);
                                            toast.show();
                                        }
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        toast = Toast.makeText(getContext(), "Gagal untuk log masuk! ID Pengguna tiada dalam pangkalan data", Toast.LENGTH_LONG);
                                        toast.setGravity(Gravity.CENTER, 0, 0);
                                        toast.show();
                                        idPengguna1.requestFocus();
                                    }
                                }
                            });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Gagal untuk log masuk! Sila semak kesahihan akaun", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}