// Used to display animation at early of opening the application
package com.sistempengurusanjabatanjuruteknik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.sistempengurusanjabatanjuruteknik.ui.juruteknik.utamaJuruteknik;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.utamaPentadbir;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class mulaAplikasi extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mula_aplikasi);

        ImageView logoSyarikat = findViewById(R.id.logoSyarikat);
        ImageView splash = findViewById(R.id.img);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lottie);
        TextView labelNamaSyarikat = findViewById(R.id.labelNamaSistem);
        TextView labelNamaSistemPendek = findViewById(R.id.labelNamaSistemPendek);
        sp = getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        // gerakkan semua objek ke luar skrin selepas 5 saat
        splash.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);
        logoSyarikat.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
        lottieAnimationView.animate().translationY(3000).setDuration(1000).setStartDelay(4000);
        labelNamaSyarikat.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);
        labelNamaSistemPendek.animate().translationY(-3000).setDuration(1000).setStartDelay(4000);

        Handler handler = new Handler();
        // pengendali jenis melambatkan proses
        handler.postDelayed(() -> {
            if(!sp.getString("idPengguna", "").isEmpty())
            {
                Pattern juruteknik = Pattern.compile("M");
                Pattern pentadbir = Pattern.compile("P");

                String idPengguna = sp.getString("idPengguna", "");

                Matcher idPenggunaJuruteknik = juruteknik.matcher((CharSequence) idPengguna);
                while (idPenggunaJuruteknik.find()) {
                    startActivity(new Intent(mulaAplikasi.this, utamaJuruteknik.class));
                }

                Matcher idPenggunaPentadbir = pentadbir.matcher((CharSequence) idPengguna);
                while (idPenggunaPentadbir.find()) {
                    startActivity(new Intent(mulaAplikasi.this, utamaPentadbir.class));
                }
            }
            else {
                startActivity(new Intent(mulaAplikasi.this, utamaAplikasi.class));
                finish(); // keluar aplikasi apabila page selepasnya menekan butang kembali
            }
        }, 5000); // masa bergerak page sebanyak 5 saat

    }
}