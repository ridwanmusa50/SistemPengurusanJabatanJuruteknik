package com.sistempengurusanjabatanjuruteknik.ui.juruteknik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tarikhPenerima1.setText(currentDate);

        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        masaPenerima1.setText(currentTime);

        db.collection("AduanKerosakan").document(idAduan)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
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
                            huraianPenerima1.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blokteks));
                            huraianPenerima1.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blokbackground));
                        }
                    }
                });

        if (butangTambahPenerima.isEnabled())
        {
            idJuruteknik1.setText(sp.getString("idPengguna", ""));
        }

        butangTambahPenerima.setOnClickListener(v -> {
            String currentDate1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            tarikhPenerima1.setText(currentDate1);

            String currentTime1 = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
            masaPenerima1.setText(currentTime1);

            String idAduan2 = idAduan1.getText().toString().trim();
            String tarikhAduan2 = tarikhAduan1.getText().toString().trim();
            String masaAduan2 = masaAduan1.getText().toString().trim();
            String namaPenuhPengadu = namaPenuhPengadu1.getText().toString().trim();
            String idMesin2 = idMesin1.getText().toString().trim();
            String huraianAduan2 = huraianAduan1.getText().toString().trim();
            String idJuruteknik2 = idJuruteknik1.getText().toString().trim();
            String tarikhPenerima2 = tarikhPenerima1.getText().toString().trim();
            String masaPenerima2 = masaPenerima1.getText().toString().trim();
            String huraianPenerima2 = huraianPenerima1.getText().toString().trim();

            Map<String, Object> penerima = new HashMap<>();
            penerima.put("idAduan", idAduan2);
            penerima.put("tarikhAduan", tarikhAduan2);
            penerima.put("masaAduan", masaAduan2);
            penerima.put("namaPenuhPengadu", namaPenuhPengadu);
            penerima.put("idMesin", idMesin2);
            penerima.put("huraianAduan", huraianAduan2);
            penerima.put("idJuruteknik", idJuruteknik2);
            penerima.put("tarikhPenerima", tarikhPenerima2);
            penerima.put("masaPenerima", masaPenerima2);
            penerima.put("huraianPenerima", huraianPenerima2);

            if (huraianPenerima2.isEmpty())
            {
                huraianPenerima1.setError("Huraian Tindakan perlu diisi!");
                huraianPenerima1.requestFocus();
            }
            else
            {
                progressBar.setVisibility(View.VISIBLE);
                db.collection("AduanKerosakan").document(idAduan2)
                        .set(penerima)
                        .addOnSuccessListener(unused -> {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(senaraiPenuhAduan.this, "Sistem berjaya menambah maklumat tindakan!", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(senaraiPenuhAduan.this, utamaJuruteknik.class));
                        });
            }
        });
    }
}