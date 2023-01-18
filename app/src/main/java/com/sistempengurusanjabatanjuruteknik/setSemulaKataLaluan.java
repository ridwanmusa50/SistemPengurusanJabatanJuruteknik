package com.sistempengurusanjabatanjuruteknik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.sistempengurusanjabatanjuruteknik.ui.login;

public class setSemulaKataLaluan extends AppCompatActivity {
    private FirebaseAuth mAuth; // declare Firebase variable
    private ProgressBar progressBar;
    private TextView emailPengguna;
    private MaterialButton butangSetSemula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_semula_kata_laluan);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.tema));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true); // atur butang patah balik ke antara muka sebelum di navigation bar atas

        mAuth = FirebaseAuth.getInstance(); // initialize FirebaseAuth
        emailPengguna = findViewById(R.id.emailPengguna);
        butangSetSemula = findViewById(R.id.butangSetSemula);
        progressBar = findViewById(R.id.progressBar);

        butangSetSemula.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailPengguna1 = emailPengguna.getText().toString().trim();

                if (emailPengguna1.isEmpty())
                {
                    emailPengguna.setError("Email Pengguna perlu diisi!");
                    emailPengguna.requestFocus();
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(emailPengguna1).matches())
                {
                    emailPengguna.setError("Sila masukkan Email Pengguna yang sah!");
                    emailPengguna.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                mAuth.sendPasswordResetEmail(emailPengguna1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(setSemulaKataLaluan.this, "Sila Semak Emel Anda untuk Reset Kata Laluan.", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(setSemulaKataLaluan.this, "Kesilapan! Emel yang diberi tiada dalam sistem. ", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, login.class));
    }}