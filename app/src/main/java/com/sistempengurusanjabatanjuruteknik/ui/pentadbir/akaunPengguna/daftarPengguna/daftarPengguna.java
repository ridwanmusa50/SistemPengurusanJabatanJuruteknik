// Used to register new user account
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.akaunPengguna.daftarPengguna;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentDaftarPenggunaBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class daftarPengguna extends Fragment
{
    private FragmentDaftarPenggunaBinding binding;
    private String jawatanPengguna2;
    private int max;
    private FirebaseAuth mAuth; // declare Firebase variable
    private FirebaseFirestore db; // declare Firebase Firestore variable
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDaftarPenggunaBinding.inflate(inflater, container, false);

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
        dataAdapter = new ArrayAdapter<>(requireContext(), R.layout.support_simple_spinner_dropdown_item, jawatanPengguna1);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        binding.spinner.setAdapter(dataAdapter);

        binding.butangDaftarPengguna.setOnClickListener(v1 -> {
            String idPengguna = binding.idPengguna.getText().toString().trim();
            String namaPenuh = binding.namaPenuh.getText().toString().trim();
            String emelPengguna = binding.emelPengguna.getText().toString().trim();
            String kataLaluan = binding.kataLaluan.getText().toString().trim();
            String kataLaluanSemula = binding.kataLaluanSemula.getText().toString().trim();
            String nomborTelefon = binding.nomborTelefon.getText().toString().trim();
            String jawatanPengguna = jawatanPengguna2.trim();

            if (namaPenuh.isEmpty()) {
                binding.namaPenuh.setError("Nama Penuh perlu diisi!");
                binding.namaPenuh.requestFocus();
            } else {
                if (emelPengguna.isEmpty()) {
                    binding.emelPengguna.setError("Emel Pengguna perlu diisi!");
                    binding.emelPengguna.requestFocus();
                } else {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emelPengguna).matches()) {
                        binding.emelPengguna.setError("Sila masukkan Email Pengguna yang sah!");
                        binding.emelPengguna.requestFocus();
                    } else {
                        if (kataLaluan.isEmpty()) {
                            binding.kataLaluan.setError("Kata Laluan perlu diisi!");
                            binding.kataLaluan.requestFocus();
                        } else {
                            if (kataLaluan.length() < 6) {
                                binding.kataLaluan.setError("Minimum Panjang Kata Laluan mestilah 6 patah perkataan!");
                                binding.kataLaluan.requestFocus();
                            } else {
                                if (kataLaluan.length() > 15) {
                                    binding.kataLaluan.setError("Maksimum Panjang Kata Laluan mestilah 15 patah perkataan!");
                                    binding.kataLaluan.requestFocus();
                                } else {
                                    if (kataLaluanSemula.isEmpty()) {
                                        binding.kataLaluanSemula.setError("Kata Laluan Semula perlu diisi!");
                                        binding.kataLaluanSemula.requestFocus();
                                    } else {
                                        if (kataLaluanSemula.length() < 6) {
                                            binding.kataLaluanSemula.setError("Minimum Panjang Kata Laluan Semula mestilah 6 patah perkataan!");
                                            binding.kataLaluanSemula.requestFocus();
                                        } else {
                                            if (kataLaluanSemula.length() > 15) {
                                                binding.kataLaluanSemula.setError("Maksimum Panjang Kata Laluan Semula mestilah 15 patah perkataan!");
                                                binding.kataLaluanSemula.requestFocus();
                                            } else {
                                                if (!kataLaluanSemula.equals(kataLaluan)) {
                                                    binding.kataLaluanSemula.setError("Kata Laluan Semula perlu sama seperti Kata Laluan!");
                                                    binding.kataLaluanSemula.requestFocus();
                                                } else {
                                                    if (nomborTelefon.isEmpty()) {
                                                        binding.nomborTelefon.setError("Nombor Telefon perlu diisi");
                                                        binding.nomborTelefon.requestFocus();
                                                    } else {
                                                        if (nomborTelefon.length() > 12 || nomborTelefon.length() < 10) {
                                                            binding.nomborTelefon.setError("Panjang Nombor Telefon mestilah antara 10 hingga 12 nombor!");
                                                            binding.nomborTelefon.requestFocus();
                                                        } else {
                                                            mAuth.signOut();
                                                            binding.progressBar.setVisibility(View.VISIBLE);

                                                            daftarPenggunaBaru(emelPengguna, kataLaluan, idPengguna, jawatanPengguna, namaPenuh, nomborTelefon);
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });

        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Pilih Jawatan Pengguna"))
                {
                    jawatanPengguna2 = "Pilih Jawatan Pengguna";
                    binding.idPengguna.setText("");
                    binding.namaPenuh.setEnabled(false);
                    binding.emelPengguna.setEnabled(false);
                    binding.kataLaluan.setEnabled(false);
                    binding.kataLaluanSemula.setEnabled(false);
                    binding.nomborTelefon.setEnabled(false);
                    binding.butangDaftarPengguna.setEnabled(false);
                    binding.butangDaftarPengguna.setBackgroundColor(Color.GRAY);
                    binding.namaPenuh.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                    binding.namaPenuh.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                    binding.emelPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                    binding.emelPengguna.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                    binding.kataLaluan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                    binding.kataLaluan.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                    binding.kataLaluanSemula.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                    binding.kataLaluanSemula.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                    binding.nomborTelefon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                    binding.nomborTelefon.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                }
                else
                {
                    jawatanPengguna2 = parent.getItemAtPosition(position).toString();
                    binding.namaPenuh.setEnabled(true);
                    binding.emelPengguna.setEnabled(true);
                    binding.kataLaluan.setEnabled(true);
                    binding.kataLaluanSemula.setEnabled(true);
                    binding.nomborTelefon.setEnabled(true);
                    binding.butangDaftarPengguna.setEnabled(true);
                    binding.butangDaftarPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));
                    binding.namaPenuh.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextBG));
                    binding.namaPenuh.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    binding.emelPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextBG));
                    binding.emelPengguna.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    binding.kataLaluan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextBG));
                    binding.kataLaluan.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    binding.kataLaluanSemula.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextBG));
                    binding.kataLaluanSemula.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                    binding.nomborTelefon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.editTextBG));
                    binding.nomborTelefon.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));

                    if (jawatanPengguna2.equals("PENGURUSAN") || jawatanPengguna2.equals("JURUTERA JURUTEKNIK") || jawatanPengguna2.equals("PENYELIA JURUTEKNIK"))
                    {
                        db.collection("Pentadbir")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                    {
                                        for (DocumentSnapshot ignored : task.getResult())
                                        {
                                            max++;
                                        }
                                        max = max + 1;

                                        if (max < 10) {
                                            binding.idPengguna.setText("P00" + max);
                                        } else if (max < 100) {
                                            binding.idPengguna.setText("P0" + max);
                                        } else {
                                            binding.idPengguna.setText("P" + max);
                                        }
                                    }
                                    else
                                    {
                                        max = 1;
                                        binding.idPengguna.setText("P00" + max);
                                    }
                                });
                    }
                    else
                    {
                        db.collection("Juruteknik")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful())
                                    {
                                        for (DocumentSnapshot ignored : task.getResult())
                                        {
                                            max++;
                                        }
                                        max = max + 1;

                                        if (max < 10) {
                                            binding.idPengguna.setText("M00" + max);
                                        } else if (max < 100) {
                                            binding.idPengguna.setText("M0" + max);
                                        } else {
                                            binding.idPengguna.setText("M" + max);
                                        }
                                    }
                                    else
                                    {
                                        max = 1;
                                        binding.idPengguna.setText("M00" + max);
                                    }
                                });
                    }
                    max = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                binding.idPengguna.setText("");
                binding.namaPenuh.setEnabled(false);
                binding.emelPengguna.setEnabled(false);
                binding.kataLaluan.setEnabled(false);
                binding.kataLaluanSemula.setEnabled(false);
                binding.nomborTelefon.setEnabled(false);
                binding.butangDaftarPengguna.setEnabled(false);
                binding.butangDaftarPengguna.setBackgroundColor(Color.GRAY);
                binding.emelPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                binding.emelPengguna.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                binding.kataLaluan.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                binding.kataLaluan.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                binding.kataLaluanSemula.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                binding.kataLaluanSemula.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                binding.nomborTelefon.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                binding.nomborTelefon.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
            }
        });

        return binding.getRoot();
    }

    private void daftarPenggunaBaru(String emelPengguna, String kataLaluan, String idPengguna, String jawatanPengguna, String namaPenuh, String nomborTelefon)
    {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        Map<String, Object> Pengguna = new HashMap<>();
        Pengguna.put("idPengguna", idPengguna);
        Pengguna.put("emelPengguna", emelPengguna);
        Pengguna.put("namaPenuh", namaPenuh);
        Pengguna.put("kataLaluan", kataLaluan);
        Pengguna.put("nomborTelefon", nomborTelefon);
        Pengguna.put("jawatanPengguna", jawatanPengguna);

        mAuth.createUserWithEmailAndPassword(emelPengguna, kataLaluan)
                .addOnSuccessListener(authResult -> db.collection("Pengguna").document(idPengguna)
                        .set(Pengguna)
                        .addOnSuccessListener(unused -> {
                            if (jawatanPengguna.equals("JURUTEKNIK")) {
                                // menukar maklumat idPengguna dalam objek Pengguna kepada idJuruteknik
                                Pengguna.remove("idPengguna");
                                Pengguna.put("idJuruteknik", idPengguna);
                                mAuth.signOut();
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Sistem berjaya menambah pengguna!", Toast.LENGTH_LONG).show();

                                db.collection("Juruteknik").document(idPengguna).set(Pengguna);

                                mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""))
                                        .addOnSuccessListener(authResult1 -> startActivity(new Intent(getContext(), requireActivity().getClass())));
                            } else {
                                Pengguna.remove("idPengguna");
                                Pengguna.put("idPentadbir", idPengguna);
                                mAuth.signOut();
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Sistem berjaya menambah pengguna!", Toast.LENGTH_LONG).show();

                                db.collection("Pentadbir").document(idPengguna).set(Pengguna);

                                mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""))
                                        .addOnSuccessListener(authResult1 -> startActivity(new Intent(getContext(), requireActivity().getClass())));
                            }
                            mAuth.signOut();
                        })
                        .addOnFailureListener(e -> {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Sistem gagal menambah pengguna! Sila cuba lagi", Toast.LENGTH_LONG).show();
                        }))
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);

                    binding.emelPengguna.setError("Emel telah wujud dalam sistem");
                    binding.emelPengguna.requestFocus();
                });
    }
}
