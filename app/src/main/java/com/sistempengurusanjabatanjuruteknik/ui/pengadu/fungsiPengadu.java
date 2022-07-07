package com.sistempengurusanjabatanjuruteknik.ui.pengadu;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.pengadu.maklumatKomunikasi.maklumatKomunikasi;
import com.sistempengurusanjabatanjuruteknik.ui.pengadu.membuatAduan.membuatAduan;

public class fungsiPengadu extends AppCompatActivity {
    private Button butangMaklumatHubungan;
    private Button butangAduan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fungsi_pengadu);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.tema_bar));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);

        butangMaklumatHubungan = findViewById(R.id.butangMaklumatKomukasi);
        butangAduan = findViewById(R.id.butangAduan);

        butangMaklumatHubungan.setOnClickListener(new View.OnClickListener() { // pengendali jenis tekan butang
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fungsiPengadu.this, maklumatKomunikasi.class); // pergerakan dari pemilihanPengguna antara muka ke fungsiPengguna antara muka
                startActivity(intent);
            }
        });

        butangAduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fungsiPengadu.this, membuatAduan.class); // pergerakan dari pemilihanPengguna antara muka ke fungsiPengguna antara muka
                startActivity(intent);
            }
        });
    }
}