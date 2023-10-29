// Used: To reset the account password using email

package com.sistempengurusanjabatanjuruteknik;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.sistempengurusanjabatanjuruteknik.databinding.SetSemulaKataLaluanBinding;

import java.util.Objects;

public class setSemulaKataLaluan extends AppCompatActivity {
    private FirebaseAuth mAuth; // declare Firebase variable

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SetSemulaKataLaluanBinding binding = SetSemulaKataLaluanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                startActivity(new Intent(setSemulaKataLaluan.this, utamaAplikasi.class));
            }
        });

        // bar at the top for return previous page
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(getApplicationContext(), R.color.tema));
        assert actionBar != null;
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true); // atur butang patah balik ke antara muka sebelum di navigation bar atas

        mAuth = FirebaseAuth.getInstance(); // initialize FirebaseAuth

        binding.butangSetSemula.setOnClickListener(v -> {
            String emailPengguna1 = Objects.requireNonNull(binding.emailPengguna.getText()).toString().trim();

            if (emailPengguna1.isEmpty()) {
                binding.emailPengguna.setError("Email Pengguna perlu diisi!");
                binding.emailPengguna.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emailPengguna1).matches()) {
                binding.emailPengguna.setError("Sila masukkan Email Pengguna yang sah!");
                binding.emailPengguna.requestFocus();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            mAuth.sendPasswordResetEmail(emailPengguna1).addOnSuccessListener(unused -> {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(setSemulaKataLaluan.this, "Sila Semak Emel Anda untuk Reset Kata Laluan.", Toast.LENGTH_LONG).show();
            }).addOnFailureListener(e -> {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(setSemulaKataLaluan.this, "Kesilapan! Emel yang diberi tiada dalam sistem. ", Toast.LENGTH_LONG).show();
            });
        });
    }
}