package com.sistempengurusanjabatanjuruteknik.ui.juruteknik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class senaraiPenuhAduan extends AppCompatActivity {
    private EditText idAduan1;
    private EditText idMesin1;
    private EditText namaPenuhPengadu1;
    private EditText tarikhAduan1;
    private EditText masaAduan1;
    private EditText huraianAduan1;
    private EditText masaPenerima1;
    private EditText tarikhPenerima1;
    private EditText idJuruteknik1;
    private EditText huraianPenerima1;
    private ProgressBar progressBar;
    private Button butangTambahPenerima;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_senarai_penuh_aduan);

        String value = null;
        // terima idAduan daripada senarai aduan
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("lokasi");
        }

        idAduan1 = findViewById(R.id.idAduan);
        idMesin1 = findViewById(R.id.idMesin);
        namaPenuhPengadu1 = findViewById(R.id.namaPenuhPengadu);
        tarikhAduan1 = findViewById(R.id.tarikhAduan);
        masaAduan1 = findViewById(R.id.masaAduan);
        huraianAduan1 = findViewById(R.id.huraianAduan);
        masaPenerima1 = findViewById(R.id.masaPenerima);
        tarikhPenerima1 = findViewById(R.id.tarikhPenerima);
        idJuruteknik1 = findViewById(R.id.idJuruteknik);
        huraianPenerima1 = findViewById(R.id.huraianPenerima);
        progressBar = findViewById(R.id.progressBar);
        butangTambahPenerima = findViewById(R.id.butangTambahPenerima);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        idAduan1.setText(value);
        String idAduan = idAduan1.getText().toString().trim();
        final String[] idMesin = {idMesin1.getText().toString().trim()};
        final String[] namapenuhPengadu = {namaPenuhPengadu1.getText().toString().trim()};
        final String[] tarikhAduan = {tarikhAduan1.getText().toString().trim()};
        final String[] masaAduan = {masaAduan1.getText().toString().trim()};
        final String[] huraianAduan = {huraianAduan1.getText().toString().trim()};
        final String[] idJuruteknik = {idJuruteknik1.getText().toString().trim()};
        final String[] tarikhPenerima = {tarikhPenerima1.getText().toString().trim()};
        final String[] masaPenerima = {masaPenerima1.getText().toString().trim()};
        final String[] huraianPenerima = {huraianPenerima1.getText().toString().trim()};

        String currentDate = new SimpleDateFormat("dd/MMM/YYYY", Locale.getDefault()).format(new Date());
        tarikhPenerima1.setText(currentDate);

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        masaPenerima1.setText(currentTime);

        db.collection("AduanKerosakan").document(idAduan)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            idMesin[0] = (String) documentSnapshot.get("idMesin");
                            idMesin1.setText(idMesin[0]);

                            namapenuhPengadu[0] = (String) documentSnapshot.get("namaPenuhPengadu");
                            namaPenuhPengadu1.setText(namapenuhPengadu[0]);

                            tarikhAduan[0] = (String) documentSnapshot.get("tarikhAduan");
                            tarikhAduan1.setText(tarikhAduan[0]);

                            masaAduan[0] = (String) documentSnapshot.get("masaAduan");
                            masaAduan1.setText(masaAduan[0]);

                            huraianAduan[0] = (String) documentSnapshot.get("huraianAduan");
                            huraianAduan1.setText(huraianAduan[0]);

                            if (documentSnapshot.contains("idJuruteknik"))
                            {
                                idJuruteknik[0] = (String) documentSnapshot.get("idJuruteknik");
                                idJuruteknik1.setText(idJuruteknik[0]);

                                tarikhPenerima[0] = (String) documentSnapshot.get("tarikhPenerima");
                                tarikhPenerima1.setText(tarikhPenerima[0]);

                                masaPenerima[0] = (String) documentSnapshot.get("masaPenerima");
                                masaPenerima1.setText(masaPenerima[0]);

                                huraianPenerima[0] = (String) documentSnapshot.get("huraianPenerima");
                                huraianPenerima1.setText(huraianPenerima[0]);

                                butangTambahPenerima.setEnabled(false);
                                butangTambahPenerima.setBackgroundColor(Color.GRAY);
                                findViewById(R.id.relativeLayout9).setEnabled(false);
                                huraianPenerima1.setTextColor(getResources().getColor(R.color.blokteks));
                                huraianPenerima1.setBackgroundColor(getResources().getColor(R.color.blokbackground));
                            }
                        }
                    }
                });

        if (butangTambahPenerima.isEnabled())
        {
            idJuruteknik1.setText(sp.getString("idPengguna", ""));
        }

        butangTambahPenerima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentDate = new SimpleDateFormat("dd/MMM/YYYY", Locale.getDefault()).format(new Date());
                tarikhPenerima1.setText(currentDate);

                String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                masaPenerima1.setText(currentTime);

                String idAduan = idAduan1.getText().toString().trim();
                String tarikhAduan = tarikhAduan1.getText().toString().trim();
                String masaAduan = masaAduan1.getText().toString().trim();
                String namaPenuhPengadu = namaPenuhPengadu1.getText().toString().trim();
                String idMesin = idMesin1.getText().toString().trim();
                String huraianAduan = huraianAduan1.getText().toString().trim();
                String idJuruteknik = idJuruteknik1.getText().toString().trim();
                String tarikhPenerima = tarikhPenerima1.getText().toString().trim();
                String masaPenerima = masaPenerima1.getText().toString().trim();
                String huraianPenerima = huraianPenerima1.getText().toString().trim();

                Map<String, Object> penerima = new HashMap<>();
                penerima.put("idAduan", idAduan);
                penerima.put("tarikhAduan", tarikhAduan);
                penerima.put("masaAduan", masaAduan);
                penerima.put("namaPenuhPengadu", namaPenuhPengadu);
                penerima.put("idMesin", idMesin);
                penerima.put("huraianAduan", huraianAduan);
                penerima.put("idJuruteknik", idJuruteknik);
                penerima.put("tarikhPenerima", tarikhPenerima);
                penerima.put("masaPenerima", masaPenerima);
                penerima.put("huraianPenerima", huraianPenerima);

                if (huraianPenerima.isEmpty())
                {
                    huraianPenerima1.setError("Huraian Tindakan perlu diisi!");
                    huraianPenerima1.requestFocus();
                    return;
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);
                    db.collection("AduanKerosakan").document(idAduan)
                            .set(penerima)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(senaraiPenuhAduan.this, "Sistem berjaya menambah maklumat tindakan!", Toast.LENGTH_LONG).show();

                                    startActivity(new Intent(senaraiPenuhAduan.this, utamaJuruteknik.class));
                                }
                            });
                }
            }
        });
    }
}