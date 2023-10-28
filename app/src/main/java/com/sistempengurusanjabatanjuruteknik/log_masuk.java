//Used for login page

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
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentLogMasukBinding;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.utamaJuruteknik;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.utamaPentadbir;

import java.util.Objects;

public class log_masuk extends Fragment {

    private FragmentLogMasukBinding binding;
    
    private FirebaseFirestore db; // istihar nama pembolehubah Firestore
    private FirebaseAuth mAuth; // istihar nama pembolehubah pengesahan Firebase
    SharedPreferences sp;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLogMasukBinding.inflate(inflater, container, false);

        // tetapkan pembolehubah mengambil maklumat
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        sp = requireActivity().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        binding.butangLogMasuk.setOnClickListener(v1 -> {
            // tetapkan pembolehubah mengambil maklumat jenis String
            String emelPengguna = Objects.requireNonNull(binding.emelPengguna.getText()).toString().trim();
            String kataLaluan = Objects.requireNonNull(binding.kataLaluan.getText()).toString().trim();
            String idPengguna = Objects.requireNonNull(binding.idPengguna.getText()).toString().trim();

            if (emelPengguna.isEmpty()) {
                binding.emelPengguna.setError("Email Pengguna perlu diisi!");
                binding.emelPengguna.requestFocus();
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(emelPengguna).matches()) {
                binding.emelPengguna.setError("Sila masukkan Email Pengguna yang sah!");
                binding.emelPengguna.requestFocus();
                return;
            }

            if (idPengguna.isEmpty()) {
                binding.idPengguna.setError("ID Pengguna perlu diisi!");
                binding.idPengguna.requestFocus();
                return;
            }

            if (kataLaluan.isEmpty()) {
                binding.kataLaluan.setError("Kata Laluan perlu diisi!");
                binding.kataLaluan.requestFocus();
                return;
            }

            if (kataLaluan.length() < 6) {
                binding.kataLaluan.setError("Minimum Panjang Kata Laluan mestilah 6 patah perkataan!");
                binding.kataLaluan.requestFocus();
                return;
            }

            if (kataLaluan.length() > 15) {
                binding.kataLaluan.setError("Maksimum Panjang Kata Laluan mestilah 15 patah perkataan!");
                binding.kataLaluan.requestFocus();
                return;
            }

            binding.progressBar.setVisibility(View.VISIBLE);

            sahkanPengguna(emelPengguna, kataLaluan, idPengguna);
        });

        binding.lupaKataLaluan.setOnClickListener(v12 -> startActivity(new Intent(getContext(), setSemulaKataLaluan.class)));

        return binding.getRoot();
    }

    private void sahkanPengguna(String emelPengguna, String kataLaluan, String idPengguna)
    {
        mAuth.signInWithEmailAndPassword(emelPengguna, kataLaluan).addOnCompleteListener(task -> {
            if (task.isSuccessful())
            {
                db.collection("Pengguna").document(idPengguna)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            Toast toast;
                            if (documentSnapshot.exists()) {
                                String jawatan = documentSnapshot.getString("jawatanPengguna");

                                binding.progressBar.setVisibility(View.GONE);

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

                                    requireActivity().finish();
                                    assert jawatan != null;
                                    if (jawatan.equals("JURUTEKNIK")) {
                                        binding.progressBar.setVisibility(View.GONE);
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
                                binding.progressBar.setVisibility(View.GONE);
                                toast = Toast.makeText(getContext(), "Gagal untuk log masuk! ID Pengguna tiada dalam pangkalan data", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                                binding.kataLaluan.requestFocus();
                            }
                        });
            }
            else
            {
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Gagal untuk log masuk! Sila semak kesahihan akaun", Toast.LENGTH_LONG).show();
            }
        });
    }
}