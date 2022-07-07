package com.sistempengurusanjabatanjuruteknik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class mulaAplikasi extends AppCompatActivity {

    private ImageView logoSyarikat, splash;
    private TextView labelNamaSyarikat, labelNamaSistemPendek;
    private LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mula_aplikasi);

        logoSyarikat = findViewById(R.id.logoSyarikat);
        splash = findViewById(R.id.img);
        lottieAnimationView = findViewById(R.id.lottie);
        labelNamaSyarikat = findViewById(R.id.labelNamaSistem);
        labelNamaSistemPendek = findViewById(R.id.labelNamaSistemPendek);

        // gerakkan semua objek ke luar skrin selepas 5 saat
        splash.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);
        logoSyarikat.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
        labelNamaSyarikat.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);
        labelNamaSistemPendek.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() { // pengendali jenis melambatkan proses
            @Override
            public void run() {
//                if(mUser != null)
//                {
//                    startActivity(new Intent(mulaAplikasi.this, pemilihanPengguna.class));
//                    finish(); // keluar aplikasi apabila page selepasnya menekan butang kembali
//                }
//                else
//                {
                    startActivity(new Intent(mulaAplikasi.this, pemilihanPengguna.class));
                    finish(); // keluar aplikasi apabila page selepasnya menekan butang kembali
//                }

            }
        }, 5000); // masa bergerak page sebanyak 5 saat

    }
}