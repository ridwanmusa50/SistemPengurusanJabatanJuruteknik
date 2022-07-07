package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.logKeluar;

import android.content.Context;
import android.content.DialogInterface;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.pemilihanPengguna;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.utamaPentadbir;

public class logKeluar extends Fragment
{
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    SharedPreferences sp;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.log_keluar, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = getContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Log Keluar!!!")
                .setIcon(R.drawable.ic_baseline_exit_24)
                .setMessage("Adakah anda pasti ingin log keluar?")
                .setPositiveButton("YA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.commit();

                        mAuth.signOut();

                        Intent intent = new Intent(getActivity(), pemilihanPengguna.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // beritahu aplikasi akan melakukan aktiviti yang baru dan aktiviti semasa akan dibersihkan
                        startActivity(intent);
                    }
                })
                .setNegativeButton("TIDAK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), utamaPentadbir.class);
                        startActivity(intent);
                    }
                })
                .create();
        dialog.show();

        return v;
    }
}
