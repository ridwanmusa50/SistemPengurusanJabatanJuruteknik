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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class kemaskiniPengguna extends Fragment {
    private EditText idPengguna1;
    private EditText namaPenuh1;
    private EditText emelPengguna1;
    private EditText kataLaluan1;
    private EditText nomborTelefon1;
    private Button butangKemaskiniPengguna;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner spinner;
    private String jawatanPengguna2 = "";
    private FirebaseFirestore db;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kemaskini_pengguna, container, false);

        idPengguna1 = v.findViewById(R.id.idPengguna);
        namaPenuh1 = v.findViewById(R.id.namaPenuh);
        emelPengguna1 = v.findViewById(R.id.emelPengguna);
        kataLaluan1 = v.findViewById(R.id.kataLaluan);
        nomborTelefon1 = v.findViewById(R.id.nomborTelefon);
        butangKemaskiniPengguna = v.findViewById(R.id.butangKemaskiniPengguna);
        Button butangCariPengguna = v.findViewById(R.id.butangCariPengguna);
        progressBar = v.findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = requireContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);
        spinner = v.findViewById(R.id.spinner);
        final String[] email = new String[1];
        final String[] pass = new String[1];

        List<String> jawatanPengguna1 = new ArrayList<>();
        jawatanPengguna1.add(0, "Pilih Jawatan Pengguna");
        jawatanPengguna1.add("PENGURUSAN");
        jawatanPengguna1.add("JURUTERA JURUTEKNIK");
        jawatanPengguna1.add("PENYELIA JURUTEKNIK");
        jawatanPengguna1.add("JURUTEKNIK");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, jawatanPengguna1);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""));

        initialize(v);

        butangCariPengguna.setOnClickListener(v1 -> {
            String idPengguna = idPengguna1.getText().toString().trim();
            final String[] namaPenuh = {namaPenuh1.getText().toString().trim()};
            final String[] emelPengguna = {emelPengguna1.getText().toString().trim()};
            final String[] kataLaluan = {kataLaluan1.getText().toString().trim()};
            final String[] nomborTelefon = {nomborTelefon1.getText().toString().trim()};
            final String[] jawatanPengguna = {jawatanPengguna2.trim()};

            if (butangKemaskiniPengguna.isEnabled())
            {
                idPengguna1.getText().clear();
                initialize(v);
                idPengguna1.requestFocus();
            }
            else
            {
                if (idPengguna.isEmpty()) {
                    idPengguna1.setError("Id Pengguna perlu diisi!");
                    idPengguna1.requestFocus();
                }
                else
                {
                    db.collection("Pengguna").document(idPengguna)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    v.findViewById(R.id.idPengguna).setEnabled(false);
                                    idPengguna1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.blokbackground));
                                    idPengguna1.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
                                    int i;

                                    namaPenuh[0] = (String) documentSnapshot.get("namaPenuh");
                                    namaPenuh1.setText(namaPenuh[0]);

                                    emelPengguna[0] = (String) documentSnapshot.get("emelPengguna");
                                    emelPengguna1.setText(emelPengguna[0]);
                                    email[0] = emelPengguna[0];

                                    kataLaluan[0] = (String) documentSnapshot.get("kataLaluan");
                                    kataLaluan1.setText(kataLaluan[0]);
                                    pass[0] = kataLaluan[0];

                                    nomborTelefon[0] = (String) documentSnapshot.get("nomborTelefon");
                                    nomborTelefon1.setText(nomborTelefon[0]);

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

                                    spinner.setSelection(i);

                                    if (emelPengguna[0].toLowerCase(Locale.ROOT).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                                    {
                                        Toast.makeText(getContext(), "Pengguna hanya boleh kemaskini diri sendiri di profil pengguna!!!", Toast.LENGTH_LONG).show();
                                        butangKemaskiniPengguna.setEnabled(false);
                                        v.findViewById(R.id.idPengguna).setEnabled(true);
                                    }
                                    else
                                    {
                                        butangKemaskiniPengguna.setEnabled(true);
                                        v.findViewById(R.id.namaPenuh).setEnabled(true);
                                        v.findViewById(R.id.emelPengguna).setEnabled(true);
                                        v.findViewById(R.id.kataLaluan).setEnabled(true);
                                        v.findViewById(R.id.nomborTelefon).setEnabled(true);
                                        namaPenuh1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        namaPenuh1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        emelPengguna1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        emelPengguna1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        kataLaluan1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        kataLaluan1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        nomborTelefon1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
                                        nomborTelefon1.setTextColor(ContextCompat.getColor(requireContext(), R.color.black));
                                        butangKemaskiniPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));
                                    }
                                }
                                else
                                {
                                    Toast.makeText(getContext(), "ID Pengguna tiada dalam pangkalan data!!!", Toast.LENGTH_SHORT).show();
                                    idPengguna1.requestFocus();
                                }
                            });
                }
            }
        });

        butangKemaskiniPengguna.setOnClickListener(v12 -> {
            String idPengguna = idPengguna1.getText().toString().trim();
            String namaPenuh = namaPenuh1.getText().toString().trim();
            String emelPengguna = emelPengguna1.getText().toString().trim();
            String kataLaluan = kataLaluan1.getText().toString().trim();
            String nomborTelefon = nomborTelefon1.getText().toString().trim();
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
                namaPenuh1.setError("Nama Penuh perlu diisi!");
                namaPenuh1.requestFocus();
            }
            else
            {
                if (emelPengguna.isEmpty())
                {
                    emelPengguna1.setError("Emel Pengguna perlu diisi!");
                    emelPengguna1.requestFocus();
                }
                else
                {
                    if (!Patterns.EMAIL_ADDRESS.matcher(emelPengguna).matches())
                    {
                        emelPengguna1.setError("Sila masukkan Email Pengguna yang sah!");
                        emelPengguna1.requestFocus();
                    }
                    else
                    {
                        mAuth.fetchSignInMethodsForEmail(emelPengguna)
                                .addOnCompleteListener(task -> {
                                    boolean isNewUser = Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();

                                    if(!isNewUser && !emelPengguna.equals(email[0]))
                                    {
                                        emelPengguna1.setError("Emel Pengguna telah terdaftar dalam sistem!");
                                        emelPengguna1.requestFocus();
                                    }
                                    else
                                    {
                                        if (kataLaluan.isEmpty())
                                        {
                                            kataLaluan1.setError("Kata Laluan perlu diisi!");
                                            kataLaluan1.requestFocus();
                                        }
                                        else
                                        {
                                            if (kataLaluan.length() < 6)
                                            {
                                                kataLaluan1.setError("Minimum Panjang Kata Laluan mestilah 6 patah perkataan!");
                                                kataLaluan1.requestFocus();
                                            }
                                            else
                                            {
                                                if (kataLaluan.length() > 15)
                                                {
                                                    kataLaluan1.setError("Maksimum Panjang Kata Laluan mestilah 15 patah perkataan!");
                                                    kataLaluan1.requestFocus();
                                                }
                                                else
                                                {
                                                    if (nomborTelefon.isEmpty())
                                                    {
                                                        nomborTelefon1.setError("Nombor Telefon perlu diisi");
                                                        nomborTelefon1.requestFocus();
                                                    }
                                                    else
                                                    {
                                                        if (nomborTelefon.length() > 12 || nomborTelefon.length() < 10)
                                                        {
                                                            nomborTelefon1.setError("Panjang Nombor Telefon mestilah antara 10 hingga 12 nombor!");
                                                            nomborTelefon1.requestFocus();
                                                        }
                                                        else
                                                        {
                                                                progressBar.setVisibility(View.VISIBLE);

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

        return v;
    }

    private void initialize(View v) {
        v.findViewById(R.id.idPengguna).setEnabled(true);
        v.findViewById(R.id.relativeLayout1).setEnabled(false);
        v.findViewById(R.id.relativeLayout2).setEnabled(false);
        v.findViewById(R.id.relativeLayout3).setEnabled(false);
        v.findViewById(R.id.relativeLayout4).setEnabled(false);
        idPengguna1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
        namaPenuh1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        namaPenuh1.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        emelPengguna1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        emelPengguna1.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        kataLaluan1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        kataLaluan1.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        nomborTelefon1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.blokbackground));
        nomborTelefon1.setTextColor(ContextCompat.getColor(requireContext(), R.color.blokteks));
        spinner.setEnabled(false);
        butangKemaskiniPengguna.setEnabled(false);
        butangKemaskiniPengguna.setBackgroundColor(Color.GRAY);
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