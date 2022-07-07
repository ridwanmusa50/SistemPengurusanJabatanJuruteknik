package com.sistempengurusanjabatanjuruteknik.ui.pentadbir;

import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.ActivityUtamaPentadbirBinding;

public class utamaPentadbir extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityUtamaPentadbirBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUtamaPentadbirBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarUtamaPentadbir.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_profilPengguna, R.id.nav_lamanUtama,
                R.id.nav_daftarTugasan, R.id.nav_kemaskiniBuangTugasan,
                R.id.nav_daftarPengguna, R.id.nav_kemaskiniPengguna, R.id.nav_buangPengguna,
                R.id.nav_laporanAduanKerosakan, R.id.nav_laporanJadualTugasan, R.id.nav_laporanBilanganAduan,
                R.id.nav_logKeluar)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_utama_pentadbir);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_utama_pentadbir);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

}