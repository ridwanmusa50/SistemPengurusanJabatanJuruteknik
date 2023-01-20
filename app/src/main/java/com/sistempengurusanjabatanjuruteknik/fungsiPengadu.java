package com.sistempengurusanjabatanjuruteknik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sistempengurusanjabatanjuruteknik.ui.pengadu.maklumatKomunikasi.maklumat_pengguna;

public class fungsiPengadu extends AppCompatActivity {
    private Button butangMaklumatKomunikasi;
    private Button butangAduan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fungsi_pengadu);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // cipta pengendali bagi butang MAKLUMAT KOMUKASI bergerak ke maklumatKomunikasi antara muka
        butangMaklumatKomunikasi = findViewById(R.id.butangMaklumatKomukasi);
        butangMaklumatKomunikasi.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fungsiPengadu.this, maklumat_pengguna.class); // pergerakan dari pemilihanPengguna antara muka ke fungsiPengguna antara muka
                startActivity(intent);
            }
        });

        // cipta pengendali bagi butang MAKLUMAT KOMUKASI bergerak ke maklumatKomunikasi antara muka
        butangAduan = findViewById(R.id.butangAduan);
        butangAduan.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fungsiPengadu.this, com.sistempengurusanjabatanjuruteknik.ui.pengadu.membuatAduan.aduan_pengguna.class); // pergerakan dari pemilihanPengguna antara muka ke fungsiPengguna antara muka
                startActivity(intent);
            }
        });
    }
}