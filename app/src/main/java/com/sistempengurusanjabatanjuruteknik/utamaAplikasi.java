// page to become background slide for login, user contact information and complaint registration

package com.sistempengurusanjabatanjuruteknik;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.sistempengurusanjabatanjuruteknik.ui.pengadu.maklumatKomunikasi.maklumat_pengguna;
import com.sistempengurusanjabatanjuruteknik.ui.pengadu.membuatAduan.aduan_pengguna;

public class utamaAplikasi extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.utama_aplikasi);

        ViewPager viewPager = findViewById(R.id.latarSlide);
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());

        adapter.addFragment(new log_masuk());
        adapter.addFragment(new maklumat_pengguna());
        adapter.addFragment(new aduan_pengguna());

        viewPager.setAdapter(adapter);
    }
}
