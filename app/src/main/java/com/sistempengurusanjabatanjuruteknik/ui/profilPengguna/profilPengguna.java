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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class profilPengguna extends Fragment
{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView namaPenuhTajuk1;
    private TextView jawatanPenggunaTajuk1;
    private EditText idPengguna1;
    private EditText namaPenuh1;
    private EditText emelPengguna1;
    private EditText kataLaluan1;
    private EditText nomborTelefon1;
    private ProgressBar progressBar;
    private Spinner spinner;
    private String jawatanPengguna2;
    SharedPreferences sp;
    SharedPreferences.Editor editor = sp.edit();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profil_pengguna, container, false);

        namaPenuhTajuk1 = v.findViewById(R.id.namaPenuhTajuk);
        jawatanPenggunaTajuk1 = v.findViewById(R.id.jawatanPenggunaTajuk);
        idPengguna1 = v.findViewById(R.id.idPengguna);
        namaPenuh1 = v.findViewById(R.id.namaPenuh);
        emelPengguna1 = v.findViewById(R.id.emelPengguna);
        kataLaluan1 = v.findViewById(R.id.kataLaluan);
        nomborTelefon1 = v.findViewById(R.id.nomborTelefon);
        Button butangKemaskiniProfil = v.findViewById(R.id.butangKemaskiniProfil);
        progressBar = v.findViewById(R.id.progressBar);
        spinner = v.findViewById(R.id.spinner);
        mAuth = FirebaseAuth.getInstance(); // initialize FirebaseAuth
        db = FirebaseFirestore.getInstance();
        sp = getContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

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
        spinner.setEnabled(false);

        String idDigunakan = sp.getString("idPengguna", "");
        idPengguna1.setText(idDigunakan);

        db.collection("Pengguna").document(idDigunakan)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot1) {
                        int i;

                        String namaPenuh = documentSnapshot1.getString("namaPenuh");
                        namaPenuh1.setText(namaPenuh);
                        namaPenuhTajuk1.setText(namaPenuh);

                        String emelPengguna = documentSnapshot1.getString("emelPengguna");
                        emelPengguna1.setText(emelPengguna);

                        String kataLaluan = documentSnapshot1.getString("kataLaluan");
                        kataLaluan1.setText(kataLaluan);

                        String nomborTelefon = documentSnapshot1.getString("nomborTelefon");
                        nomborTelefon1.setText(nomborTelefon);

                        String jawatanPengguna = documentSnapshot1.getString("jawatanPengguna");
                        jawatanPenggunaTajuk1.setText(jawatanPengguna);
                        jawatanPengguna2 = jawatanPengguna;

                        if (jawatanPengguna.equals("PENGURUSAN"))
                        {
                            i = 1;
                        }
                        else if (jawatanPengguna.equals("JURUTERA JURUTEKNIK"))
                        {
                            i = 2;
                        }
                        else if (jawatanPengguna.equals("PENYELIA JURUTEKNIK"))
                        {
                            i = 3;
                        }
                        else
                        {
                            i = 4;
                        }

                        spinner.setSelection(i);
                        return;
                    }
                });

        butangKemaskiniProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idPengguna = idPengguna1.getText().toString().trim();
                String namaPenuh = namaPenuh1.getText().toString().trim();
                String emelPengguna = emelPengguna1.getText().toString().trim();
                String kataLaluan = kataLaluan1.getText().toString().trim();
                String nomborTelefon = nomborTelefon1.getText().toString().trim();

                Map<String, Object> Pengguna = new HashMap<>();
                Pengguna.put("idPengguna", idPengguna);
                Pengguna.put("emelPengguna", emelPengguna);
                Pengguna.put("namaPenuh", namaPenuh);
                Pengguna.put("kataLaluan", kataLaluan);
                Pengguna.put("nomborTelefon", nomborTelefon);
                Pengguna.put("jawatanPengguna", jawatanPengguna2);

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
                                    .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                            boolean isNewUser = task.getResult().getSignInMethods().isEmpty();
                                            boolean isCurrentUser = task.getResult().getSignInMethods().size() == 1;

                                            if (isNewUser || isCurrentUser)
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
                                                                                .update(Pengguna)
                                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                                    @Override
                                                                                    public void onSuccess(Void unused) {
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
                                                                                        startActivity(new Intent(getContext(), getActivity().getClass()));
                                                                                    }
                                                                                });
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            else if(!isNewUser && !isCurrentUser)
                                            {
                                                emelPengguna1.setError("Emel Pengguna telah terdaftar dalam sistem!");
                                                emelPengguna1.requestFocus();
                                                return;
                                            }
                                        }
                                    });
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
        return v;
    }

    public void tukarEmail(String emelPengguna)
    {
        mAuth.getCurrentUser().updateEmail(emelPengguna);

        editor = sp.edit();
        editor.putString("emelPengguna", emelPengguna);
        editor.apply();
    }

    public void tukarPass(String kataLaluan)
    {
        mAuth.getCurrentUser().updatePassword(kataLaluan).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                editor = sp.edit();

                editor.putString("kataLaluan", kataLaluan);
                editor.apply();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("\nError password reset: " + e);
            }
        });
    }
}
