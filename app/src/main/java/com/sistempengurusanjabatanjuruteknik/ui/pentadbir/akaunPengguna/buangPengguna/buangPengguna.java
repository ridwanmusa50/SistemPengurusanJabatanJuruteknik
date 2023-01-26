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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class buangPengguna extends Fragment {
    private EditText idPengguna1;
    private EditText namaPenuh1;
    private EditText emelPengguna1;
    private EditText kataLaluan1;
    private EditText nomborTelefon1;
    private Button butangBuangPengguna;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner spinner;
    private final String jawatanPengguna2 = "";
    private FirebaseFirestore db;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_buang_pengguna, container, false);

        idPengguna1 = v.findViewById(R.id.idPengguna);
        namaPenuh1 = v.findViewById(R.id.namaPenuh);
        emelPengguna1 = v.findViewById(R.id.emelPengguna);
        kataLaluan1 = v.findViewById(R.id.kataLaluan);
        nomborTelefon1 = v.findViewById(R.id.nomborTelefon);
        butangBuangPengguna = v.findViewById(R.id.butangBuangPengguna);
        Button butangCariPengguna = v.findViewById(R.id.butangCariPengguna);
        progressBar = v.findViewById(R.id.progressBar);
        spinner = v.findViewById(R.id.spinner);
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
        spinner.setAdapter(dataAdapter);

        initialize(v);

        butangCariPengguna.setOnClickListener(v1 -> {
            String idPengguna = idPengguna1.getText().toString().trim();
            final String[] namaPenuh = {namaPenuh1.getText().toString().trim()};
            final String[] emelPengguna = {emelPengguna1.getText().toString().trim()};
            final String[] kataLaluan = {kataLaluan1.getText().toString().trim()};
            final String[] nomborTelefon = {nomborTelefon1.getText().toString().trim()};
            final String[] jawatanPengguna = {jawatanPengguna2.trim()};

            if (butangBuangPengguna.isEnabled())
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

                                    kataLaluan[0] = (String) documentSnapshot.get("kataLaluan");
                                    kataLaluan1.setText(kataLaluan[0]);

                                    nomborTelefon[0] = (String) documentSnapshot.get("nomborTelefon");
                                    nomborTelefon1.setText(nomborTelefon[0]);

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

                                    spinner.setSelection(i);

                                    if (emelPengguna[0].toLowerCase(Locale.ROOT).equals(Objects.requireNonNull(mAuth.getCurrentUser()).getEmail()))
                                    {
                                        Toast.makeText(getContext(), "Pengguna tidak boleh buang akaunnya sendiri!!!", Toast.LENGTH_LONG).show();
                                        butangBuangPengguna.setEnabled(false);
                                        v.findViewById(R.id.idPengguna).setEnabled(true);
                                    }
                                    else
                                    {
                                        butangBuangPengguna.setEnabled(true);
                                        butangBuangPengguna.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));
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

        butangBuangPengguna.setOnClickListener(v12 -> {
            String emelPengguna = emelPengguna1.getText().toString().trim();
            String kataLaluan = kataLaluan1.getText().toString().trim();

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
                                    progressBar.setVisibility(View.GONE);
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

        return v;
    }

    private void initialize(View v) {
        v.findViewById(R.id.idPengguna).setEnabled(true);
        v.findViewById(R.id.relativeLayout1).setEnabled(false);
        v.findViewById(R.id.relativeLayout2).setEnabled(false);
        v.findViewById(R.id.relativeLayout3).setEnabled(false);
        v.findViewById(R.id.relativeLayout4).setEnabled(false);
        spinner.setEnabled(false);
        butangBuangPengguna.setEnabled(false);
        idPengguna1.setBackground(ContextCompat.getDrawable(requireContext(), R.color.editTextBG));
        butangBuangPengguna.setBackgroundColor(Color.GRAY);
    }
}