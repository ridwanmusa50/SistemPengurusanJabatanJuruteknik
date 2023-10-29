// Used to modify the user information
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.akaunPengguna.kemaskiniPengguna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentKemaskiniPenggunaBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class kemaskiniPengguna extends Fragment {

    private FragmentKemaskiniPenggunaBinding binding;
    private FirebaseAuth mAuth;
    private String jawatanPengguna2 = "";
    private FirebaseFirestore db;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentKemaskiniPenggunaBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = requireContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);
        
        final String[] email = new String[1];
        final String[] pass = new String[1];

        List<String> jawatanPengguna1 = new ArrayList<>();
        jawatanPengguna1.add(0, "Pilih Jawatan Pengguna");
        jawatanPengguna1.add("PENGURUSAN");
        jawatanPengguna1.add("JURUTERA JURUTEKNIK");
        jawatanPengguna1.add("PENYELIA JURUTEKNIK");
        jawatanPengguna1.add("JURUTEKNIK");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, jawatanPengguna1);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);

        mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""));

        initialize();

        binding.butangCariPengguna.setOnClickListener(v1 -> {
            String idPengguna = binding.idPengguna.getText().toString().trim();
            final String[] namaPenuh = {binding.namaPenuh.getText().toString().trim()};
            final String[] emelPengguna = {binding.emelPengguna.getText().toString().trim()};
            final String[] kataLaluan = {binding.kataLaluan.getText().toString().trim()};
            final String[] nomborTelefon = {binding.nomborTelefon.getText().toString().trim()};
            final String[] jawatanPengguna = {jawatanPengguna2.trim()};

            if (binding.butangKemaskiniPengguna.isEnabled())
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
                                    email[0] = emelPengguna[0];

                                    kataLaluan[0] = (String) documentSnapshot.get("kataLaluan");
                                    binding.kataLaluan.setText(kataLaluan[0]);
                                    pass[0] = kataLaluan[0];

                                    nomborTelefon[0] = (String) documentSnapshot.get("nomborTelefon");
                                    binding.nomborTelefon.setText(nomborTelefon[0]);

                                    jawatanPengguna[0] = (String) documentSnapshot.get("jawatanPengguna");
                                    jawatanPengguna2 = jawatanPengguna[0];

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
                                        Toast.makeText(getContext(), "Pengguna hanya boleh kemaskini diri sendiri di profil pengguna!!!", Toast.LENGTH_LONG).show();
                                        binding.butangKemaskiniPengguna.setEnabled(false);
                                        binding.idPengguna.setEnabled(true);
                                    }
                                    else
                                    {
                                        binding.butangKemaskiniPengguna.setEnabled(true);
                                        binding.namaPenuh.setEnabled(true);
                                        binding.emelPengguna.setEnabled(true);
                                        binding.kataLaluan.setEnabled(true);
                                        binding.nomborTelefon.setEnabled(true);
                                        binding.namaPenuh.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        binding.namaPenuh.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        binding.emelPengguna.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        binding.emelPengguna.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        binding.kataLaluan.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        binding.kataLaluan.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        binding.nomborTelefon.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        binding.nomborTelefon.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        binding.butangKemaskiniPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));
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

        binding.butangKemaskiniPengguna.setOnClickListener(v12 -> {
            String idPengguna = binding.idPengguna.getText().toString().trim();
            String namaPenuh = binding.namaPenuh.getText().toString().trim();
            String emelPengguna = binding.emelPengguna.getText().toString().trim();
            String kataLaluan = binding.kataLaluan.getText().toString().trim();
            String nomborTelefon = binding.nomborTelefon.getText().toString().trim();
            String jawatanPengguna = jawatanPengguna2.trim();

            Map<String, Object> Pengguna = new HashMap<>();
            Pengguna.put("idPengguna", idPengguna);
            Pengguna.put("emelPengguna", emelPengguna);
            Pengguna.put("namaPenuh", namaPenuh);
            Pengguna.put("kataLaluan", kataLaluan);
            Pengguna.put("nomborTelefon", nomborTelefon);
            Pengguna.put("jawatanPengguna", jawatanPengguna);

            if (namaPenuh.isEmpty())
            {
                binding.namaPenuh.setError("Nama Penuh perlu diisi!");
                binding.namaPenuh.requestFocus();
            }
            else
            {
                if (emelPengguna.isEmpty())
                {
                    binding.emelPengguna.setError("Emel Pengguna perlu diisi!");
                    binding.emelPengguna.requestFocus();
                }
                else
                {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emelPengguna).matches())
                    {
                        binding.emelPengguna.setError("Sila masukkan Email Pengguna yang sah!");
                        binding.emelPengguna.requestFocus();
                    }
                    else
                    {
                        mAuth.fetchSignInMethodsForEmail(emelPengguna)
                                .addOnCompleteListener(task -> {
                                    boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();

                                    if(!isNewUser && !emelPengguna.equals(email[0]))
                                    {
                                        binding.emelPengguna.setError("Emel Pengguna telah terdaftar dalam sistem!");
                                        binding.emelPengguna.requestFocus();
                                    }
                                    else
                                    {
                                        if (kataLaluan.isEmpty())
                                        {
                                            binding.kataLaluan.setError("Kata Laluan perlu diisi!");
                                            binding.kataLaluan.requestFocus();
                                        }
                                        else
                                        {
                                            if (kataLaluan.length() < 6)
                                            {
                                                binding.kataLaluan.setError("Minimum Panjang Kata Laluan mestilah 6 patah perkataan!");
                                                binding.kataLaluan.requestFocus();
                                            }
                                            else
                                            {
                                                if (kataLaluan.length() > 15)
                                                {
                                                    binding.kataLaluan.setError("Maksimum Panjang Kata Laluan mestilah 15 patah perkataan!");
                                                    binding.kataLaluan.requestFocus();
                                                }
                                                else
                                                {
                                                    if (nomborTelefon.isEmpty())
                                                    {
                                                        binding.nomborTelefon.setError("Nombor Telefon perlu diisi");
                                                        binding.nomborTelefon.requestFocus();
                                                    }
                                                    else
                                                    {
                                                        if (nomborTelefon.length() > 12 || nomborTelefon.length() < 10)
                                                        {
                                                            binding.nomborTelefon.setError("Panjang Nombor Telefon mestilah antara 10 hingga 12 nombor!");
                                                            binding.nomborTelefon.requestFocus();
                                                        }
                                                        else
                                                        {
                                                                binding.progressBar.setVisibility(View.VISIBLE);

                                                                db.collection("Pengguna").document(idPengguna)
                                                                        .set(Pengguna)
                                                                        .addOnSuccessListener(unused -> {
                                                                            if (idPengguna.contains("M"))
                                                                            {
                                                                                Pengguna.remove("idPengguna");
                                                                                Pengguna.put("idJuruteknik", idPengguna);

                                                                                db.collection("Juruteknik").document(idPengguna)
                                                                                        .set(Pengguna);
                                                                            }
                                                                            else if (idPengguna.contains("P"))
                                                                            {
                                                                                Pengguna.remove("idPengguna");
                                                                                Pengguna.put("idPentadbir", idPengguna);

                                                                                db.collection("Pentadbir").document(idPengguna)
                                                                                        .set(Pengguna);
                                                                            }
                                                                            mAuth.signOut();
                                                                            updateEmailPass(email[0], pass[0], emelPengguna, kataLaluan);

                                                                            mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""))
                                                                                    .addOnSuccessListener(authResult -> {
                                                                                        Toast.makeText(getContext(), "Maklumat pengguna berjaya dikemaskini", Toast.LENGTH_LONG).show();
                                                                                        startActivity(new Intent(getContext(), requireActivity().getClass()));
                                                                                    });
                                                                        });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                }
            }
        });

        return binding.getRoot();
    }

    private void initialize() {
        binding.idPengguna.setEnabled(true);
        binding.relativeLayout1.setEnabled(false);
        binding.relativeLayout2.setEnabled(false);
        binding.relativeLayout3.setEnabled(false);
        binding.relativeLayout4.setEnabled(false);
        binding.idPengguna.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
        binding.namaPenuh.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        binding.namaPenuh.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        binding.emelPengguna.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        binding.emelPengguna.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        binding.kataLaluan.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        binding.kataLaluan.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        binding.nomborTelefon.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        binding.nomborTelefon.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        binding.spinner.setEnabled(false);
        binding.butangKemaskiniPengguna.setEnabled(false);
        binding.butangKemaskiniPengguna.setBackgroundColor(Color.GRAY);
    }

    private void updateEmailPass(String email, String pass, String emelPengguna, String kataLaluan)
    {
        mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    if(!email.equals(emelPengguna))
                    {
                        Objects.requireNonNull(mAuth.getCurrentUser()).updateEmail(emelPengguna);
                    }
                    if (!pass.equals(kataLaluan))
                    {
                        Objects.requireNonNull(mAuth.getCurrentUser()).updatePassword(kataLaluan);
                    }
                })
                        .addOnFailureListener(e -> System.out.println("Gagal kemaskini: " + e));

        mAuth.signOut();
    }
}