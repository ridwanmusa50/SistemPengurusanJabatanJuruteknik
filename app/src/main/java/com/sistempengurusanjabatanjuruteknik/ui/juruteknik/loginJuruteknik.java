package com.sistempengurusanjabatanjuruteknik.ui.juruteknik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth; // import Firebase library
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.setSemulaKataLaluan;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class loginJuruteknik extends AppCompatActivity {
    private ProgressBar progressBar;
    private  TextView emelPengguna1;
    private TextView idPengguna1;
    private  TextView kataLaluan1;
    private MaterialButton butangLogMasuk;
    private MaterialButton lupaKataLaluan;
    private FirebaseFirestore db; // declare Firebase Firestore variable
    private FirebaseAuth mAuth; // declare Firebase variable
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_masuk);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.tema_bar));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true); // atur butang patah balik ke antara muka sebelum di navigation bar atas

            db = FirebaseFirestore.getInstance();
            mAuth = FirebaseAuth.getInstance(); // initialize FirebaseAuth
            sp = getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);
            emelPengguna1 = findViewById(R.id.emelPengguna);
            idPengguna1 = findViewById(R.id.idPengguna);
            kataLaluan1 = findViewById(R.id.kataLaluan);
            butangLogMasuk =  findViewById(R.id.butangLogMasuk);
            progressBar =  findViewById(R.id.progressBar);
            lupaKataLaluan = findViewById(R.id.lupaKataLaluan);

            butangLogMasuk.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
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
                    finish();
                    startActivity(new Intent(loginJuruteknik.this, setSemulaKataLaluan.class));
                }
            });
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
                                    if (documentSnapshot.exists()) {
                                        String jawatan = documentSnapshot.getString("jawatanPengguna");

                                        if (jawatan.equals("JURUTEKNIK")) {
                                            progressBar.setVisibility(View.GONE);
                                            if (emelPengguna.equals(documentSnapshot.getString("emelPengguna"))) {
                                                SharedPreferences.Editor editor = sp.edit();

                                                db.collection("Pengguna").document(idPengguna)
                                                        .update("kataLaluan", kataLaluan);
                                                db.collection("Juruteknik").document(idPengguna)
                                                        .update("kataLaluan", kataLaluan);

                                                editor.putString("idPengguna", idPengguna);
                                                editor.putString("kataLaluan", kataLaluan);
                                                editor.putString("emelPengguna", emelPengguna);
                                                editor.commit();

                                                finish();
                                                startActivity(new Intent(loginJuruteknik.this, utamaJuruteknik.class)); //terus ke laman utama pentadbir
                                            } else {
                                                Toast.makeText(loginJuruteknik.this, "Gagal untuk log masuk! ID Pengguna tidak sepadan dengan Emel", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(loginJuruteknik.this, "Gagal untuk log masuk! Akaun terdaftar sebagai akaun PENTADBIR", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                    else
                                    {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(loginJuruteknik.this, "Gagal untuk log masuk! ID Pengguna tiada dalam pangkalan data", Toast.LENGTH_LONG).show();
                                        idPengguna1.requestFocus();
                                    }
                                }
                            });
                }
                else
                {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(loginJuruteknik.this, "Gagal untuk log masuk! Sila semak kesahihan akaun", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}