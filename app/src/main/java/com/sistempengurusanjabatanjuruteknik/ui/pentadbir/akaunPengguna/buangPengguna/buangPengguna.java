package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.akaunPengguna.buangPengguna;

import android.content.Context;
import android.content.DialogInterface;
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
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class buangPengguna extends Fragment {
    private EditText idPengguna1;
    private EditText namaPenuh1;
    private EditText emelPengguna1;
    private EditText kataLaluan1;
    private EditText nomborTelefon1;
    private Button butangBuangPengguna;
    private Button butangCariPengguna;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner spinner;
    private String jawatanPengguna2 = "";
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
        butangCariPengguna = v.findViewById(R.id.butangCariPengguna);
        progressBar = v.findViewById(R.id.progressBar);
        spinner = v.findViewById(R.id.spinner);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = getContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        List<String> jawatanPengguna1 = new ArrayList<>();
        jawatanPengguna1.add(0, "Pilih Jawatan Pengguna");
        jawatanPengguna1.add("PENGURUSAN");
        jawatanPengguna1.add("JURUTERA JURUTEKNIK");
        jawatanPengguna1.add("PENYELIA JURUTEKNIK");
        jawatanPengguna1.add("JURUTEKNIK");

        ArrayAdapter<String> dataAdapter;
        dataAdapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, jawatanPengguna1);
        dataAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);

        initialize(v);

        butangCariPengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v1) {
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
                    return;
                }
                else
                {
                    if (idPengguna.isEmpty()) {
                        idPengguna1.setError("Id Pengguna perlu diisi!");
                        idPengguna1.requestFocus();
                        return;
                    }
                    else
                    {
                        db.collection("Pengguna").document(idPengguna)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        if (documentSnapshot.exists()) {
                                            v.findViewById(R.id.idPengguna).setEnabled(false);
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

                                            if (jawatanPengguna[0].equals("PENGURUSAN")) {
                                                i = 1;
                                            } else if (jawatanPengguna[0].equals("JURUTERA JURUTEKNIK")) {
                                                i = 2;
                                            } else if (jawatanPengguna[0].equals("PENYELIA JURUTEKNIK")) {
                                                i = 3;
                                            } else {
                                                i = 4;
                                            }

                                            spinner.setSelection(i);

                                            if (emelPengguna[0].toLowerCase(Locale.ROOT).equals(mAuth.getCurrentUser().getEmail()))
                                            {
                                                Toast.makeText(getContext(), "Pengguna tidak boleh buang akaunnya sendiri!!!", Toast.LENGTH_LONG).show();
                                                butangBuangPengguna.setEnabled(false);
                                                v.findViewById(R.id.idPengguna).setEnabled(true);
                                            }
                                            else
                                            {
                                                butangBuangPengguna.setEnabled(true);
                                                butangBuangPengguna.setBackgroundColor(getResources().getColor(R.color.tema));
                                            }

                                            return;
                                        }
                                        else
                                        {
                                            Toast.makeText(getContext(), "ID Pengguna tiada dalam pangkalan data!!!", Toast.LENGTH_SHORT).show();
                                            idPengguna1.requestFocus();
                                            return;
                                        }
                                    }
                                });
                    }
                }
            }
        });

        butangBuangPengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emelPengguna = emelPengguna1.getText().toString().trim();
                String kataLaluan = kataLaluan1.getText().toString().trim();

                AlertDialog dialog = new AlertDialog.Builder(getContext())
                .setTitle("Adakah anda pasti?")
                .setMessage("Tindakan membuang akaun ini akan membuatkan akaun dikeluarkan dari sistem sepenuhnya"
                        + " dan akaun ini tidak akan dapat mengakses aplikasi lagi.")
                .setPositiveButton("Buang", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        final boolean[] hint = {true};

                        mAuth.signInWithEmailAndPassword(emelPengguna, kataLaluan)
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                mAuth.getCurrentUser().delete();
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(getContext(), "Sistem berjaya membuang pengguna!", Toast.LENGTH_LONG).show();
                                                mAuth.signOut();
                                                hint[0] = true;
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getContext(), "Pengguna sudah tidak lagi boleh mengakses sistem.!", Toast.LENGTH_LONG).show();
                                                        hint[0] = false;
                                                    }
                                                });

                                mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""))
                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                if (hint.equals(true)) {
                                                    startActivity(new Intent(getContext(), getActivity().getClass()));
                                                }
                                            }
                                        });
                    }
                })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create();
                dialog.show();
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
        spinner.setEnabled(false);
        butangBuangPengguna.setEnabled(false);
        butangBuangPengguna.setBackgroundColor(Color.GRAY);
    }
}