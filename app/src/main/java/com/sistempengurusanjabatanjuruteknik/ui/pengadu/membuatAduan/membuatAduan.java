package com.sistempengurusanjabatanjuruteknik.ui.pengadu.membuatAduan;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.sistempengurusanjabatanjuruteknik.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class membuatAduan extends AppCompatActivity
{
    private EditText idAduan1;
    private EditText tarikhAduan1;
    private EditText masaAduan1;
    private EditText namaPenuhPengadu1;
    private EditText idMesin1;
    private EditText huraianAduan1;
    private Button butangTambahAduan;
    private ProgressBar progressBar;
    private FirebaseFirestore db; // declare Firestore variable
    private FirebaseAuth mAuth; // declare Firebase variable

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membuat_aduan);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.tema_bar));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayHomeAsUpEnabled(true);

        idAduan1 = findViewById(R.id.idAduan);
        tarikhAduan1 = findViewById(R.id.tarikhAduan);
        masaAduan1 = findViewById(R.id.masaAduan);
        namaPenuhPengadu1 = findViewById(R.id.namaPenuhPengadu);
        idMesin1 = findViewById(R.id.idMesin);
        huraianAduan1 = findViewById(R.id.hurainAduan);
        butangTambahAduan = findViewById(R.id.butangTambahAduan);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance(); // initialize FirebaseAuth
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

        String currentDate = new SimpleDateFormat("dd/MMM/YYYY", Locale.getDefault()).format(new Date());
        tarikhAduan1.setText(currentDate);

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        masaAduan1.setText(currentTime);

        butangTambahAduan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahAduan();
            }
        });
    }

    private void tambahAduan()
    {
        String currentDate = new SimpleDateFormat("dd/MMM/YYYY", Locale.getDefault()).format(new Date());
        tarikhAduan1.setText(currentDate);

        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
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
            return;
        }
        else
        {
            if (idMesin.isEmpty())
            {
                idMesin1.setError("Id Mesin perlu diisi!");
                idMesin1.requestFocus();
                return;
            }
            else if (idMesin.length() > 8)
            {
                idMesin1.setError("Id Mesin perlu kurang daripada 9 patah perkataan!");
                idMesin1.requestFocus();
                return;
            }
            else
            {
                if (huraianAduan.isEmpty())
                {
                    huraianAduan1.setError("Huraian Tugas perlu diisi!");
                    huraianAduan1.requestFocus();
                    return;
                }
                else
                {
                    progressBar.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword("pengadu@gmail.com", "kw11_mppg0301")
                            .addOnCompleteListener(membuatAduan.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful())
                                    {
                                        db.collection("AduanKerosakan").document(idAduan)
                                                .set(aduan)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(membuatAduan.this, "Sistem berjaya menghantar aduan!", Toast.LENGTH_LONG).show();

                                                        // kod untuk mengeluarkan pemberitahuan kepada pengadu
                                                        String line = "Tugas bagi id, " + idAduan + " telah berjaya dibuat.\nTarikh aduan dibuat pada " + tarikhAduan + " dan jam " + masaAduan + ".";
                                                        FcmNotificationsSender notificationsSender = new FcmNotificationsSender("/topics/all",
                                                                "Peti Masuk",
                                                                line,
                                                                getApplicationContext(), membuatAduan.this);
                                                        notificationsSender.SendNotifications();
                                                        finish();
                                                        startActivity(getIntent());
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Gagal untuk daftar data aduan kerosakan: ", task.getException());
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(membuatAduan.this, "Sistem gagal menghantar aduan! Sila cuba lagi", Toast.LENGTH_LONG).show();
                                                    }
                                                });

                                        Map<String, Object> pengadu = new HashMap<>();
                                        pengadu.put("namaPenuhPengadu", namaPenuhPengadu);

                                        db.collection("Pengadu").document(idAduan)
                                                .set(pengadu)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Gagal untuk daftar data pengadu: ", task.getException());
                                                    }
                                                });
                                    }
                                    else
                                    {
                                        Log.w(TAG, "Gagal untuk log masuk: ", task.getException());
                                    }
                                }
                            });
                    mAuth.signOut();
                }
            }
        }
    }
}