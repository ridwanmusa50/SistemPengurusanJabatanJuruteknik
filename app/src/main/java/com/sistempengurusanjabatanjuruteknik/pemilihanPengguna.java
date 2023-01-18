package com.sistempengurusanjabatanjuruteknik;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.sistempengurusanjabatanjuruteknik.ui.pengadu.fungsiPengadu;
import com.sistempengurusanjabatanjuruteknik.ui.login;

public class pemilihanPengguna extends AppCompatActivity {
    private long keluarAplikasi;
    private Toast backToast;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pemilihan_pengguna);

        // cipta pengendali bagi butang TETAMU bergerak ke fungsiPengadu antara muka
        Button pengadu = findViewById(R.id.butangTetamu);
        pengadu.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pemilihanPengguna.this, fungsiPengadu.class); // pergerakan dari pemilihanPengguna antara muka ke fungsiPengguna antara muka
                startActivity(intent);
            }
        });

        // cipta pengendali bagi butang AHLI bergerak ke login antara muka
        Button logMasuk = findViewById(R.id.butangLogMasuk);
        logMasuk.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pemilihanPengguna.this, login.class); // pergerakan dari pemilihanPengguna antara muka ke login antara muka
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        if (keluarAplikasi + 2000 > System.currentTimeMillis())
        {
            backToast.cancel();
            super.onBackPressed();
            return;
        }
        else
        {
            backToast = Toast.makeText(getBaseContext(), "Tekan kembali lagi untuk keluar aplikasi", Toast.LENGTH_SHORT);
            backToast.show();
        }

        keluarAplikasi = System.currentTimeMillis();
    }
}