// used to sign out account and go to utamaAplikasi page
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.logKeluar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;


import com.google.firebase.auth.FirebaseAuth;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.utamaPentadbir;
import com.sistempengurusanjabatanjuruteknik.utamaAplikasi;

public class logKeluar extends Fragment
{
    private FirebaseAuth mAuth;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.log_keluar, container, false);

        mAuth = FirebaseAuth.getInstance();
        sp = requireContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        AlertDialog dialog = new AlertDialog.Builder(requireActivity())
                .setTitle("Log Keluar!!!")
                .setIcon(R.drawable.ic_baseline_exit_24)
                .setMessage("Adakah anda pasti ingin log keluar?")
                .setPositiveButton("YA", (dialog1, which) -> {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.clear();
                    editor.apply();

                    mAuth.signOut();

                    Intent intent = new Intent(getActivity(), utamaAplikasi.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // beritahu aplikasi akan melakukan aktiviti yang baru dan aktiviti semasa akan dibersihkan
                    startActivity(intent);
                })
                .setNegativeButton("TIDAK", (dialog12, which) -> {
                    Intent intent = new Intent(getActivity(), utamaPentadbir.class);
                    startActivity(intent);
                })
                .create();
        dialog.show();

        return v;
    }
}
