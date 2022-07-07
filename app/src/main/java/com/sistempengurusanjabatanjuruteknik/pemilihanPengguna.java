package com.sistempengurusanjabatanjuruteknik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.loginJuruteknik;
import com.sistempengurusanjabatanjuruteknik.ui.pengadu.fungsiPengadu;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.loginPentadbir;

public class pemilihanPengguna extends AppCompatActivity {
    private Button pengadu;
    private Button juruteknik;
    private Button pengurus;
    private long keluarAplikasi;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pemilihan_pengguna);

        // cipta pengendali bagi butang TETAMU bergerak ke fungsiPengadu antara muka
        pengadu = findViewById(R.id.butangTetamu);
        pengadu.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pemilihanPengguna.this, fungsiPengadu.class); // pergerakan dari pemilihanPengguna antara muka ke fungsiPengguna antara muka
                startActivity(intent);
            }
        });

        // cipta pengendali bagi butang AHLI bergerak ke loginJuruteknik antara muka
        juruteknik = findViewById(R.id.butangAhli);
        juruteknik.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pemilihanPengguna.this, loginJuruteknik.class); // pergerakan dari pemilihanPengguna antara muka ke loginJuruteknik antara muka
                startActivity(intent);
            }
        });

        // cipta pengendali bagi butang PENGURUS bergerak ke loginJuruteknik antara muka
        pengurus = findViewById(R.id.butangPentadbir);
        pengurus.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(pemilihanPengguna.this, loginPentadbir.class); // pergerakan dari pemilihanPengguna antara muka ke loginJuruteknik antara muka
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