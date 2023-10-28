// Used for delete the user account
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.akaunPengguna.buangPengguna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentBuangPenggunaBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class buangPengguna extends Fragment {
    
    private FragmentBuangPenggunaBinding binding;
   
    private FirebaseAuth mAuth;
    private final String jawatanPengguna2 = "";
    private FirebaseFirestore db;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBuangPenggunaBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = requireContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        List<String> jawatanPengguna1 = new ArrayList<>();
        jawatanPengguna1.add(0, "Pilih Jawatan Pengguna");
        jawatanPengguna1.add("PENGURUSAN");
        jawatanPengguna1.add("JURUTERA JURUTEKNIK");
        jawatanPengguna1.add("PENYELIA JURUTEKNIK");
        jawatanPengguna1.add("JURUTEKNIK");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, jawatanPengguna1);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);

        initialize();

        binding.butangCariPengguna.setOnClickListener(v1 -> {
            String idPengguna = binding.idPengguna.getText().toString().trim();
            final String[] namaPenuh = {binding.namaPenuh.getText().toString().trim()};
            final String[] emelPengguna = {binding.emelPengguna.getText().toString().trim()};
            final String[] kataLaluan = {binding.kataLaluan.getText().toString().trim()};
            final String[] nomborTelefon = {binding.nomborTelefon.getText().toString().trim()};
            final String[] jawatanPengguna = {jawatanPengguna2.trim()};

            if (binding.butangBuangPengguna.isEnabled())
            {
                binding.idPengguna.getText().clear();
                initialize();
                binding.idPengguna.requestFocus();
            }
            else
            {
                if (idPengguna.isEmpty()) {
                    binding.idPengguna.setError("Id Pengguna perlu diisi!");
                    binding.idPengguna.requestFocus();
                }
                else
                {
                    db.collection("Pengguna").document(idPengguna)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    binding.idPengguna.setEnabled(false);
                                    binding.idPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                                    binding.idPengguna.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                                    int i;

                                    namaPenuh[0] = (String) documentSnapshot.get("namaPenuh");
                                    binding.namaPenuh.setText(namaPenuh[0]);

                                    emelPengguna[0] = (String) documentSnapshot.get("emelPengguna");
                                    binding.emelPengguna.setText(emelPengguna[0]);

                                    kataLaluan[0] = (String) documentSnapshot.get("kataLaluan");
                                    binding.kataLaluan.setText(kataLaluan[0]);

                                    nomborTelefon[0] = (String) documentSnapshot.get("nomborTelefon");
                                    binding.nomborTelefon.setText(nomborTelefon[0]);

                                    jawatanPengguna[0] = (String) documentSnapshot.get("jawatanPengguna");

                                    switch (Objects.requireNonNull(jawatanPengguna[0])) {
                                        case "PENGURUSAN":
                                            i = 1;
                                            break;
                                        case "JURUTERA JURUTEKNIK":
                                            i = 2;
                                            break;
                                        case "PENYELIA JURUTEKNIK":
                                            i = 3;
                                            break;
                                        default:
                                            i = 4;
                                            break;
                                    }

                                    binding.spinner.setSelection(i);

                                    if (emelPengguna[0].toLowerCase(Locale.ROOT).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                                    {
                                        Toast.makeText(getContext(), "Pengguna tidak boleh buang akaunnya sendiri!!!", Toast.LENGTH_LONG).show();
                                        binding.butangBuangPengguna.setEnabled(false);
                                        binding.idPengguna.setEnabled(true);
                                    }
                                    else
                                    {
                                        binding.butangBuangPengguna.setEnabled(true);
                                        binding.butangBuangPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "ID Pengguna tiada dalam pangkalan data!!!", Toast.LENGTH_SHORT).show();
                                    binding.idPengguna.requestFocus();
                                }
                            });
                }
            }
        });

        binding.butangBuangPengguna.setOnClickListener(v12 -> {
            String emelPengguna = binding.emelPengguna.getText().toString().trim();
            String kataLaluan = binding.kataLaluan.getText().toString().trim();

            AlertDialog dialog = new AlertDialog.Builder(requireContext())
            .setTitle("Adakah anda pasti?")
            .setMessage("Tindakan membuang akaun ini akan membuatkan akaun dikeluarkan dari sistem sepenuhnya"
                    + " dan akaun ini tidak akan dapat mengakses aplikasi lagi.")
            .setPositiveButton("Buang", (dialog1, which) -> {
                mAuth.signOut();
                final boolean[] hint = {true};

                mAuth.signInWithEmailAndPassword(emelPengguna, kataLaluan)
                                .addOnSuccessListener(authResult -> {
                                    Objects.requireNonNull(mAuth.getCurrentUser()).delete();
                                    binding.progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), "Sistem berjaya membuang pengguna!", Toast.LENGTH_LONG).show();
                                    mAuth.signOut();
                                    hint[0] = true;
                                })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(getContext(), "Pengguna sudah tidak lagi boleh mengakses sistem.!", Toast.LENGTH_LONG).show();
                                            hint[0] = false;
                                        });

                        mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""))
                                .addOnSuccessListener(authResult -> {
                                    if (hint.equals(true)) {
                                        startActivity(new Intent(getContext(), requireActivity().getClass()));
                                    }
                                });
            })
                    .setNegativeButton("Tidak", (dialog12, which) -> dialog12.dismiss())
            .create();
            dialog.show();
        });

        return binding.getRoot();
    }

    private void initialize() {
        binding.idPengguna.setEnabled(true);
        binding.relativeLayout1.setEnabled(false);
        binding.relativeLayout2.setEnabled(false);
        binding.relativeLayout3.setEnabled(false);
        binding.relativeLayout4.setEnabled(false);
        binding.spinner.setEnabled(false);
        binding.butangBuangPengguna.setEnabled(false);
        binding.idPengguna.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
        binding.butangBuangPengguna.setBackgroundColor(Color.GRAY);
    }
}