package com.sistempengurusanjabatanjuruteknik.ui.pengadu.membuatAduan;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sistempengurusanjabatanjuruteknik.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class aduan_pengguna extends Fragment {
    private EditText idAduan1;
    private EditText tarikhAduan1;
    private EditText masaAduan1;
    private EditText namaPenuhPengadu1;
    private EditText idMesin1;
    private EditText huraianAduan1;
    private ProgressBar progressBar;
    private FirebaseFirestore db; // declare Firestore variable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_aduan_pengguna, container, false);

        idAduan1 = v.findViewById(R.id.idAduan);
        tarikhAduan1 = v.findViewById(R.id.tarikhAduan);
        masaAduan1 = v.findViewById(R.id.masaAduan);
        namaPenuhPengadu1 = v.findViewById(R.id.namaPenuhPengadu);
        idMesin1 = v.findViewById(R.id.idMesin);
        huraianAduan1 = v.findViewById(R.id.hurainAduan);
        Button butangTambahAduan = v.findViewById(R.id.butangTambahAduan);
        progressBar = v.findViewById(R.id.progressBar);
        db = FirebaseFirestore.getInstance(); // initialize Firestore
        FirebaseMessaging.getInstance().subscribeToTopic("all");

        db.collection("AduanKerosakan")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            idAduan1.setText("DOC-MAINT-" + (task.getResult().size() + 1) + " RO");
                        }
                        if (task.getResult().isEmpty())
                        {
                            idAduan1.setText("DOC-MAINT-1 RO");
                        }
                    }
                });

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tarikhAduan1.setText(currentDate);

        String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
        masaAduan1.setText(currentTime);

        butangTambahAduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahAduan();
            }
        });
        return v;
    }

    private void tambahAduan()
    {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        tarikhAduan1.setText(currentDate);

        String currentTime = new SimpleDateFormat("HH:mm a", Locale.getDefault()).format(new Date());
        masaAduan1.setText(currentTime);

        String idAduan = idAduan1.getText().toString().trim();
        String tarikhAduan = tarikhAduan1.getText().toString().trim();
        String masaAduan = masaAduan1.getText().toString().trim();
        String namaPenuhPengadu = namaPenuhPengadu1.getText().toString().trim();
        String idMesin = idMesin1.getText().toString().trim();
        String huraianAduan = huraianAduan1.getText().toString().trim();

        Map<String, Object> aduan = new HashMap<>();
        aduan.put("idAduan", idAduan);
        aduan.put("tarikhAduan", tarikhAduan);
        aduan.put("masaAduan", masaAduan);
        aduan.put("namaPenuhPengadu", namaPenuhPengadu);
        aduan.put("idMesin", idMesin);
        aduan.put("huraianAduan", huraianAduan);

        if (namaPenuhPengadu.isEmpty())
        {
            namaPenuhPengadu1.setError("Nama Penuh Pengadu perlu diisi!");
            namaPenuhPengadu1.requestFocus();
        }
        else
        {
            if (idMesin.isEmpty())
            {
                idMesin1.setError("Id Mesin perlu diisi!");
                idMesin1.requestFocus();
            }
            else if (idMesin.length() > 8)
            {
                idMesin1.setError("Id Mesin perlu kurang daripada 9 patah perkataan!");
                idMesin1.requestFocus();
            }
            else
            {
                if (huraianAduan.isEmpty())
                {
                    huraianAduan1.setError("Huraian Tugas perlu diisi!");
                    huraianAduan1.requestFocus();
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);

                    final Toast[] toast = new Toast[1];

                    db.collection("AduanKerosakan").document(idAduan)
                            .set(aduan)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    progressBar.setVisibility(View.GONE);
                                    toast[0] = Toast.makeText(getContext(), "Sistem berjaya menghantar aduan!", Toast.LENGTH_LONG);
                                    toast[0].setGravity(Gravity.CENTER, 0, 0);
                                    toast[0].show();

                                    // kod untuk mengeluarkan pemberitahuan kepada pengadu
                                    String line = "Tugas bagi id, " + idAduan + " telah berjaya dibuat.\nTarikh aduan dibuat pada " + tarikhAduan + " dan jam " + masaAduan + ".";
                                    FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",
                                            "Peti Masuk",
                                            line,
                                            getActivity().getApplicationContext(), getActivity());
                                    notificationsSender.SendNotifications();
                                    getActivity().finish();
                                    startActivity(getActivity().getIntent());
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Gagal untuk daftar data aduan kerosakan: " + e);
                                    progressBar.setVisibility(View.GONE);
                                    toast[0] = Toast.makeText(getContext(), "Sistem gagal menghantar aduan! Sila cuba lagi", Toast.LENGTH_LONG);
                                    toast[0].setGravity(Gravity.CENTER, 0, 0);
                                    toast[0].show();
                                }
                            });

                    Map<String, Object> pengadu = new HashMap<>();
                    pengadu.put("namaPenuhPengadu", namaPenuhPengadu);

                    db.collection("Pengadu").document(idAduan)
                            .set(pengadu)
                            .addOnFailureListener(new OnFailureListener() {
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Gagal untuk daftar data pengadu: " + e);
                                }
                            });
                }
            }
        }
    }
}