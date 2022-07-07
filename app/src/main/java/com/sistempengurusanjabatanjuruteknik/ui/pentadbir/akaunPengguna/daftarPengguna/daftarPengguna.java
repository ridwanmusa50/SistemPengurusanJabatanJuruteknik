package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.akaunPengguna.daftarPengguna;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot; // memanggil fungsi mengambil maklumat secara banyak dalam Firestore
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.utamaPentadbir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class daftarPengguna extends Fragment
{
    private EditText idPengguna1;
    private EditText namaPenuh1;
    private EditText emelPengguna1;
    private EditText kataLaluan1;
    private EditText kataLaluanSemula1;
    private EditText nomborTelefon1;
    private Button butangDaftarPengguna;
    private ProgressBar progressBar;
    private Spinner spinner;
    private String jawatanPengguna2;
    private int max;
    private FirebaseAuth mAuth; // declare Firebase variable
    private FirebaseFirestore db; // declare Firebase Firestore variable
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daftar_pengguna, container, false);

        idPengguna1 = v.findViewById(R.id.idPengguna);
        namaPenuh1 = v.findViewById(R.id.namaPenuh);
        emelPengguna1 = v.findViewById(R.id.emelPengguna);
        kataLaluan1 = v.findViewById(R.id.kataLaluan);
        kataLaluanSemula1 = v.findViewById(R.id.kataLaluanSemula);
        nomborTelefon1 = v.findViewById(R.id.nomborTelefon);
        butangDaftarPengguna = v.findViewById(R.id.butangDaftarPengguna);
        progressBar = v.findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = getContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);
        spinner = v.findViewById(R.id.spinner);

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

        butangDaftarPengguna.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                String idPengguna = idPengguna1.getText().toString().trim();
                String namaPenuh = namaPenuh1.getText().toString().trim();
                String emelPengguna = emelPengguna1.getText().toString().trim();
                String kataLaluan = kataLaluan1.getText().toString().trim();
                String kataLaluanSemula = kataLaluanSemula1.getText().toString().trim();
                String nomborTelefon = nomborTelefon1.getText().toString().trim();
                String jawatanPengguna = jawatanPengguna2.trim();

                if (namaPenuh.isEmpty()) {
                    namaPenuh1.setError("Nama Penuh perlu diisi!");
                    namaPenuh1.requestFocus();
                    return;
                } else {
                    if (emelPengguna.isEmpty()) {
                        emelPengguna1.setError("Emel Pengguna perlu diisi!");
                        emelPengguna1.requestFocus();
                        return;
                    } else {
                        if (!Patterns.EMAIL_ADDRESS.matcher(emelPengguna).matches()) {
                            emelPengguna1.setError("Sila masukkan Email Pengguna yang sah!");
                            emelPengguna1.requestFocus();
                            return;
                        } else {
                            if (kataLaluan.isEmpty()) {
                                kataLaluan1.setError("Kata Laluan perlu diisi!");
                                kataLaluan1.requestFocus();
                                return;
                            } else {
                                if (kataLaluan.length() < 6) {
                                    kataLaluan1.setError("Minimum Panjang Kata Laluan mestilah 6 patah perkataan!");
                                    kataLaluan1.requestFocus();
                                    return;
                                } else {
                                    if (kataLaluan.length() > 15) {
                                        kataLaluan1.setError("Maksimum Panjang Kata Laluan mestilah 15 patah perkataan!");
                                        kataLaluan1.requestFocus();
                                        return;
                                    } else {
                                        if (kataLaluanSemula.isEmpty()) {
                                            kataLaluanSemula1.setError("Kata Laluan Semula perlu diisi!");
                                            kataLaluanSemula1.requestFocus();
                                            return;
                                        } else {
                                            if (kataLaluanSemula.length() < 6) {
                                                kataLaluanSemula1.setError("Minimum Panjang Kata Laluan Semula mestilah 6 patah perkataan!");
                                                kataLaluanSemula1.requestFocus();
                                                return;
                                            } else {
                                                if (kataLaluanSemula.length() > 15) {
                                                    kataLaluanSemula1.setError("Maksimum Panjang Kata Laluan Semula mestilah 15 patah perkataan!");
                                                    kataLaluanSemula1.requestFocus();
                                                    return;
                                                } else {
                                                    if (!kataLaluanSemula.equals(kataLaluan)) {
                                                        kataLaluanSemula1.setError("Kata Laluan Semula perlu sama seperti Kata Laluan!");
                                                        kataLaluanSemula1.requestFocus();
                                                        return;
                                                    } else {
                                                        if (nomborTelefon.isEmpty()) {
                                                            nomborTelefon1.setError("Nombor Telefon perlu diisi");
                                                            nomborTelefon1.requestFocus();
                                                            return;
                                                        } else {
                                                            if (nomborTelefon.length() > 12 || nomborTelefon.length() < 10) {
                                                                nomborTelefon1.setError("Panjang Nombor Telefon mestilah antara 10 hingga 12 nombor!");
                                                                nomborTelefon1.requestFocus();
                                                                return;
                                                            } else {
                                                                mAuth.signOut();
                                                                progressBar.setVisibility(View.VISIBLE);

                                                                daftarPengguna(emelPengguna, kataLaluan, idPengguna, jawatanPengguna, namaPenuh, nomborTelefon);

                                                                mAuth.signInWithEmailAndPassword(sp.getString("emelPengguna", ""), sp.getString("kataLaluan", ""))
                                                                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                                                            @Override
                                                                            public void onSuccess(AuthResult authResult) {
                                                                                startActivity(new Intent(getContext(), utamaPentadbir.class));
                                                                            }
                                                                        });
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
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals("Pilih Jawatan Pengguna"))
                {
                    jawatanPengguna2 = "Pilih Jawatan Pengguna";
                    idPengguna1.setText("");
                    namaPenuh1.setEnabled(false);
                    emelPengguna1.setEnabled(false);
                    kataLaluan1.setEnabled(false);
                    kataLaluanSemula1.setEnabled(false);
                    nomborTelefon1.setEnabled(false);
                    butangDaftarPengguna.setEnabled(false);
                    butangDaftarPengguna.setBackgroundColor(Color.GRAY);
                    emelPengguna1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                    emelPengguna1.setTextColor(getResources().getColor(R.color.blokteks));
                    kataLaluan1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                    kataLaluan1.setTextColor(getResources().getColor(R.color.blokteks));
                    kataLaluanSemula1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                    kataLaluanSemula1.setTextColor(getResources().getColor(R.color.blokteks));
                    nomborTelefon1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                    nomborTelefon1.setTextColor(getResources().getColor(R.color.blokteks));
                }
                else
                {
                    jawatanPengguna2 = parent.getItemAtPosition(position).toString();
                    namaPenuh1.setEnabled(true);
                    emelPengguna1.setEnabled(true);
                    kataLaluan1.setEnabled(true);
                    kataLaluanSemula1.setEnabled(true);
                    nomborTelefon1.setEnabled(true);
                    butangDaftarPengguna.setEnabled(true);
                    butangDaftarPengguna.setBackgroundColor(getResources().getColor(R.color.tema));
                    emelPengguna1.setBackgroundColor(getResources().getColor(R.color.editTextBG));
                    emelPengguna1.setTextColor(getResources().getColor(R.color.black));
                    kataLaluan1.setBackgroundColor(getResources().getColor(R.color.editTextBG));
                    kataLaluan1.setTextColor(getResources().getColor(R.color.black));
                    kataLaluanSemula1.setBackgroundColor(getResources().getColor(R.color.editTextBG));
                    kataLaluanSemula1.setTextColor(getResources().getColor(R.color.black));
                    nomborTelefon1.setBackgroundColor(getResources().getColor(R.color.editTextBG));
                    nomborTelefon1.setTextColor(getResources().getColor(R.color.black));

                    if (jawatanPengguna2.equals("PENGURUSAN") || jawatanPengguna2.equals("JURUTERA JURUTEKNIK") || jawatanPengguna2.equals("PENYELIA JURUTEKNIK"))
                    {
                        db.collection("Pentadbir")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful())
                                        {
                                            for (DocumentSnapshot document : task.getResult())
                                            {
                                                max++;
                                            }
                                            max = max + 1;

                                            if (max < 10) {
                                                idPengguna1.setText("P00" + max);
                                            } else if (max < 100) {
                                                idPengguna1.setText("P0" + max);
                                            } else {
                                                idPengguna1.setText("P" + max);
                                            }
                                        }
                                        else
                                        {
                                            max = 1;
                                            idPengguna1.setText("P00" + max);
                                        }
                                    }
                                });
                    }
                    else
                    {
                        db.collection("Juruteknik")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful())
                                        {
                                            for (DocumentSnapshot document : task.getResult())
                                            {
                                                max++;
                                            }
                                            max = max + 1;

                                            if (max < 10) {
                                                idPengguna1.setText("M00" + max);
                                            } else if (max < 100) {
                                                idPengguna1.setText("M0" + max);
                                            } else {
                                                idPengguna1.setText("M" + max);
                                            }
                                        }
                                        else
                                        {
                                            max = 1;
                                            idPengguna1.setText("M00" + max);
                                        }
                                    }
                                });
                    }
                    max = 0;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                idPengguna1.setText("");
                namaPenuh1.setEnabled(false);
                emelPengguna1.setEnabled(false);
                kataLaluan1.setEnabled(false);
                kataLaluanSemula1.setEnabled(false);
                nomborTelefon1.setEnabled(false);
                butangDaftarPengguna.setEnabled(false);
                butangDaftarPengguna.setBackgroundColor(Color.GRAY);
                emelPengguna1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                emelPengguna1.setTextColor(getResources().getColor(R.color.blokteks));
                kataLaluan1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                kataLaluan1.setTextColor(getResources().getColor(R.color.blokteks));
                kataLaluanSemula1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                kataLaluanSemula1.setTextColor(getResources().getColor(R.color.blokteks));
                nomborTelefon1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                nomborTelefon1.setTextColor(getResources().getColor(R.color.blokteks));
            }
        });

        return v;
    }

    private void daftarPengguna(String emelPengguna, String kataLaluan, String idPengguna, String jawatanPengguna, String namaPenuh, String nomborTelefon)
    {
        db = FirebaseFirestore.getInstance();
        Map<String, Object> Pengguna = new HashMap<>();
        Pengguna.put("idPengguna", idPengguna);
        Pengguna.put("emelPengguna", emelPengguna);
        Pengguna.put("namaPenuh", namaPenuh);
        Pengguna.put("kataLaluan", kataLaluan);
        Pengguna.put("nomborTelefon", nomborTelefon);
        Pengguna.put("jawatanPengguna", jawatanPengguna);

        mAuth.createUserWithEmailAndPassword(emelPengguna, kataLaluan)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        db.collection("Pengguna").document(idPengguna)
                                .set(Pengguna)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        if (jawatanPengguna.equals("JURUTEKNIK")) {
                                            // menukar maklumat idPengguna dalam objek Pengguna kepada idJuruteknik
                                            Pengguna.remove("idPengguna");
                                            Pengguna.put("idJuruteknik", idPengguna);

                                            db.collection("Juruteknik").document(idPengguna)
                                                    .set(Pengguna)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(), "Sistem berjaya menambah pengguna!", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), "Sistem gagal menambah pengguna! Sila cuba lagi", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    });
                                        } else {
                                            Pengguna.remove("idPengguna");
                                            Pengguna.put("idPentadbir", idPengguna);

                                            db.collection("Pentadbir").document(idPengguna)
                                                    .set(Pengguna)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            Toast.makeText(getContext(), "Sistem berjaya menambah pengguna!", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getContext(), "Sistem gagal menambah pengguna! Sila cuba lagi", Toast.LENGTH_LONG).show();
                                                            progressBar.setVisibility(View.GONE);
                                                        }
                                                    });
                                        }
                                        mAuth.signOut();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Sistem gagal menambah pengguna! Sila cuba lagi", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);

                        emelPengguna1.setError("Emel telah wujud dalam sistem");
                        emelPengguna1.requestFocus();
                        return;
                    }
                });
    }
}
