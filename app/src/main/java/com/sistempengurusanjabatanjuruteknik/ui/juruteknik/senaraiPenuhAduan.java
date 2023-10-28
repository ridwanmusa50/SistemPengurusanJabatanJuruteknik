package com.sistempengurusanjabatanjuruteknik.ui.juruteknik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.ActivitySenaraiPenuhAduanBinding;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class senaraiPenuhAduan extends AppCompatActivity {
    
    private FirebaseFirestore db;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivitySenaraiPenuhAduanBinding binding = ActivitySenaraiPenuhAduanBinding.inflate(getLayoutInflater());

        String value = null;
        // terima idAduan daripada senarai aduan
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("lokasi");
        }

        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        binding.idAduan.setText(value);
        String idAduan = binding.idAduan.getText().toString().trim();
        final String[] idMesin = {binding.idMesin.getText().toString().trim()};
        final String[] namapenuhPengadu = {binding.namaPenuhPengadu.getText().toString().trim()};
        final String[] tarikhAduan = {binding.tarikhAduan.getText().toString().trim()};
        final String[] masaAduan = {binding.masaAduan.getText().toString().trim()};
        final String[] huraianAduan = {binding.huraianAduan.getText().toString().trim()};
        final String[] idJuruteknik = {binding.idJuruteknik.getText().toString().trim()};
        final String[] tarikhPenerima = {binding.tarikhPenerima.getText().toString().trim()};
        final String[] masaPenerima = {binding.masaPenerima.getText().toString().trim()};
        final String[] huraianPenerima = {binding.huraianPenerima.getText().toString().trim()};

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        binding.tarikhPenerima.setText(currentDate);

        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
        binding.masaPenerima.setText(currentTime);

        db.collection("AduanKerosakan").document(idAduan)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists())
                    {
                        idMesin[0] = (String) documentSnapshot.get("idMesin");
                        binding.idMesin.setText(idMesin[0]);

                        namapenuhPengadu[0] = (String) documentSnapshot.get("namaPenuhPengadu");
                        binding.namaPenuhPengadu.setText(namapenuhPengadu[0]);

                        tarikhAduan[0] = (String) documentSnapshot.get("tarikhAduan");
                        binding.tarikhAduan.setText(tarikhAduan[0]);

                        masaAduan[0] = (String) documentSnapshot.get("masaAduan");
                        binding.masaAduan.setText(masaAduan[0]);

                        huraianAduan[0] = (String) documentSnapshot.get("huraianAduan");
                        binding.huraianAduan.setText(huraianAduan[0]);

                        if (documentSnapshot.contains("idJuruteknik"))
                        {
                            idJuruteknik[0] = (String) documentSnapshot.get("idJuruteknik");
                            binding.idJuruteknik.setText(idJuruteknik[0]);

                            tarikhPenerima[0] = (String) documentSnapshot.get("tarikhPenerima");
                            binding.tarikhPenerima.setText(tarikhPenerima[0]);

                            masaPenerima[0] = (String) documentSnapshot.get("masaPenerima");
                            binding.masaPenerima.setText(masaPenerima[0]);

                            huraianPenerima[0] = (String) documentSnapshot.get("huraianPenerima");
                            binding.huraianPenerima.setText(huraianPenerima[0]);

                            binding.butangTambahPenerima.setEnabled(false);
                            binding.butangTambahPenerima.setBackgroundColor(Color.GRAY);
                            findViewById(R.id.relativeLayout9).setEnabled(false);
                            binding.huraianPenerima.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.blokteks));
                            binding.huraianPenerima.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.blokbackground));
                        }
                    }
                });

        if (binding.butangTambahPenerima.isEnabled())
        {
            binding.idJuruteknik.setText(sp.getString("idPengguna", ""));
        }

        binding.butangTambahPenerima.setOnClickListener(v -> {
            String currentDate1 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            binding.tarikhPenerima.setText(currentDate1);

            String currentTime1 = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
            binding.masaPenerima.setText(currentTime1);

            String idAduan2 = binding.idAduan.getText().toString().trim();
            String tarikhAduan2 = binding.tarikhAduan.getText().toString().trim();
            String masaAduan2 = binding.masaAduan.getText().toString().trim();
            String namaPenuhPengadu = binding.namaPenuhPengadu.getText().toString().trim();
            String idMesin2 = binding.idMesin.getText().toString().trim();
            String huraianAduan2 = binding.huraianAduan.getText().toString().trim();
            String idJuruteknik2 = binding.idJuruteknik.getText().toString().trim();
            String tarikhPenerima2 = binding.tarikhPenerima.getText().toString().trim();
            String masaPenerima2 = binding.masaPenerima.getText().toString().trim();
            String huraianPenerima2 = binding.huraianPenerima.getText().toString().trim();

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
                binding.huraianPenerima.setError("Huraian Tindakan perlu diisi!");
                binding.huraianPenerima.requestFocus();
            }
            else
            {
                binding.progressBar.setVisibility(View.VISIBLE);
                db.collection("AduanKerosakan").document(idAduan2)
                        .set(penerima)
                        .addOnSuccessListener(unused -> {
                            binding.progressBar.setVisibility(View.GONE);
                            Toast.makeText(senaraiPenuhAduan.this, "Sistem berjaya menambah maklumat tindakan!", Toast.LENGTH_LONG).show();

                            startActivity(new Intent(senaraiPenuhAduan.this, utamaJuruteknik.class));
                        });
            }
        });
    }
}