// Used for user profile page
package com.sistempengurusanjabatanjuruteknik.ui.profilPengguna;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentProfilPenggunaBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class profilPengguna extends Fragment
{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private String jawatanPengguna2;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentProfilPenggunaBinding binding = FragmentProfilPenggunaBinding.inflate(inflater, container, false);

        mAuth = FirebaseAuth.getInstance(); // initialize FirebaseAuth
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
        binding.spinner.setEnabled(false);

        String idDigunakan = sp.getString("idPengguna", "");
        binding.idPengguna.setText(idDigunakan);

        db.collection("Pengguna").document(idDigunakan)
                .get()
                .addOnSuccessListener(documentSnapshot1 -> {
                    int i;

                    String namaPenuh = documentSnapshot1.getString("namaPenuh");
                    binding.namaPenuh.setText(namaPenuh);
                    binding.namaPenuhTajuk.setText(namaPenuh);

                    String emelPengguna = documentSnapshot1.getString("emelPengguna");
                    binding.emelPengguna.setText(emelPengguna);

                    String kataLaluan = documentSnapshot1.getString("kataLaluan");
                    binding.kataLaluan.setText(kataLaluan);

                    String nomborTelefon = documentSnapshot1.getString("nomborTelefon");
                    binding.nomborTelefon.setText(nomborTelefon);

                    String jawatanPengguna = documentSnapshot1.getString("jawatanPengguna");
                    binding.jawatanPenggunaTajuk.setText(jawatanPengguna);
                    jawatanPengguna2 = jawatanPengguna;

                    switch (Objects.requireNonNull(jawatanPengguna)) {
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
                });

        binding.butangKemaskiniProfil.setOnClickListener(v -> {
            String idPengguna = Objects.requireNonNull(binding.idPengguna.getText()).toString().trim();
            String namaPenuh = Objects.requireNonNull(binding.namaPenuh.getText()).toString().trim();
            String emelPengguna = Objects.requireNonNull(binding.emelPengguna.getText()).toString().trim();
            String kataLaluan = Objects.requireNonNull(binding.kataLaluan.getText()).toString().trim();
            String nomborTelefon = Objects.requireNonNull(binding.nomborTelefon.getText()).toString().trim();

            Map<String, Object> Pengguna = new HashMap<>();
            Pengguna.put("idPengguna", idPengguna);
            Pengguna.put("emelPengguna", emelPengguna);
            Pengguna.put("namaPenuh", namaPenuh);
            Pengguna.put("kataLaluan", kataLaluan);
            Pengguna.put("nomborTelefon", nomborTelefon);
            Pengguna.put("jawatanPengguna", jawatanPengguna2);

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
                                    boolean isCurrentUser = task.getResult().getSignInMethods().size() == 1;

                                    if (isNewUser || isCurrentUser)
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
                                                                        .update(Pengguna)
                                                                        .addOnSuccessListener(unused -> {
                                                                            if (idPengguna.contains("M"))
                                                                            {
                                                                                Pengguna.remove("idPengguna");
                                                                                Pengguna.put("idJuruteknik", idPengguna);

                                                                                db.collection("Juruteknik").document(idPengguna)
                                                                                        .update(Pengguna);
                                                                            }
                                                                            else if (idPengguna.contains("P"))
                                                                            {
                                                                                Pengguna.remove("idPengguna");
                                                                                Pengguna.put("idPentadbir", idPengguna);

                                                                                db.collection("Pentadbir").document(idPengguna)
                                                                                        .update(Pengguna);
                                                                            }

                                                                            if (!sp.getString("emelPengguna", "").equals(emelPengguna))
                                                                            {
                                                                                tukarEmail(emelPengguna);
                                                                            }
                                                                            if (!sp.getString("kataLaluan", "").equals(kataLaluan))
                                                                            {
                                                                                tukarPass(kataLaluan);
                                                                            }

                                                                            Toast.makeText(getContext(), "Maklumat profil pengguna berjaya dikemaskini", Toast.LENGTH_LONG).show();
                                                                            startActivity(new Intent(getContext(), requireActivity().getClass()));
                                                                        });
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else {
                                        binding.emelPengguna.setError("Emel Pengguna telah terdaftar dalam sistem!");
                                        binding.emelPengguna.requestFocus();
                                    }
                                });
                    }
                }
            }
        });


        binding.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Pilih Jawatan Pengguna"))
                {
                    jawatanPengguna2 = "Pilih Jawatan Pengguna";
                }
                else
                {
                    jawatanPengguna2 = parent.getItemAtPosition(position).toString();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                jawatanPengguna2 = "Pilih Jawatan Pengguna";
            }
        });
        return binding.getRoot();
    }

    public void tukarEmail(String emelPengguna)
    {
        Objects.requireNonNull(mAuth.getCurrentUser()).updateEmail(emelPengguna);

        editor = sp.edit();
        editor.putString("emelPengguna", emelPengguna);
        editor.apply();
    }

    public void tukarPass(String kataLaluan)
    {
        Objects.requireNonNull(mAuth.getCurrentUser()).updatePassword(kataLaluan).addOnSuccessListener(unused -> {
            editor = sp.edit();

            editor.putString("kataLaluan", kataLaluan);
            editor.apply();
        }).addOnFailureListener(e -> System.out.println("\nError password reset: " + e));
    }
}
