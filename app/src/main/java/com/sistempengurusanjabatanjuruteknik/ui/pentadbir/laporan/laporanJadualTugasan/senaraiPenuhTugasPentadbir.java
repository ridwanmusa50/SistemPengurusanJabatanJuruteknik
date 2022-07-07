package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class senaraiPenuhTugasPentadbir extends AppCompatActivity {
    private EditText idJadual1;
    private EditText tarikhJadual1;
    private ProgressBar progressBar;
    private Button butangCetakTugas;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private RecyclerView recylerview;
    private ArrayList<TugasPenuh> list = new ArrayList<>();
    private ArrayList<String> tugas = new ArrayList<>();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_laporan_senarai_tugas_kerosakan);

        String value = null;
        // terima idJadual daripada senarai jadual
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("lokasi");
        }

        recylerview = findViewById(R.id.recyclerView);
        recylerview.setLayoutManager(new LinearLayoutManager(this));
        penyambungJadualPenuh penyambung = new penyambungJadualPenuh(this, list);
        recylerview.setAdapter(penyambung);

        idJadual1 = findViewById(R.id.idJadual);
        tarikhJadual1 = findViewById(R.id.tarikhJadual);
        butangCetakTugas = findViewById(R.id.butangCetakTugas);
        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tunjukkanMaklumat(value);

        butangCetakTugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(senaraiPenuhTugasPentadbir.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

                String idJadual = idJadual1.getText().toString().trim();
                String tarikhJadual = tarikhJadual1.getText().toString().trim();

                if(checkPermissionGranted()){
                    ciptaPDF(idJadual, tarikhJadual);
                }else{
                    requestPermission();

                    if(checkPermissionGranted()){
                        ciptaPDF(idJadual, tarikhJadual);
                    }
                }
                }
        });
    }

    private void ciptaPDF(String idJadual, String tarikhJadual)
    {
        final Dialog paparanPdf = new Dialog(this);
        paparanPdf.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paparanPdf.setContentView(R.layout.paparan_pdf_tugas);
        paparanPdf.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(paparanPdf.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        paparanPdf.getWindow().setAttributes(lp);

        Button butangMuatTurun = paparanPdf.findViewById(R.id.butangMuatTurun);
        TextView tarikhCetak = paparanPdf.findViewById(R.id.tarikhCetak);
        TextView masaCetak = paparanPdf.findViewById(R.id.masaCetak);

        TextView idJadualCetak = paparanPdf.findViewById(R.id.idJadual);
        TextView tarikhJadualCetak = paparanPdf.findViewById(R.id.tarikhJadual);
        TextView idPenggunaCetak = paparanPdf.findViewById(R.id.idPenggunaCetak);

        TextView tugasJadual1 = paparanPdf.findViewById(R.id.tugasJadual1);
        TextView tugasJadual2 = paparanPdf.findViewById(R.id.tugasJadual2);
        TextView tugasJadual3 = paparanPdf.findViewById(R.id.tugasJadual3);
        TextView tugasJadual4 = paparanPdf.findViewById(R.id.tugasJadual4);
        TextView tugasJadual5 = paparanPdf.findViewById(R.id.tugasJadual5);

        String currentDate = new SimpleDateFormat("dd/MMM/YYYY", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        sp = getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);
        String emel = sp.getString("idPengguna", "");

        tarikhCetak.setText(currentDate);
        masaCetak.setText(currentTime);
        idJadualCetak.setText(idJadual);
        tarikhJadualCetak.setText(tarikhJadual);
        idPenggunaCetak.setText(emel);

        if (list.size() == 1)
        {
            tugasJadual1.setText("" + list.get(0).tugasJadual + " (" + tugas.get(0) + ")");
        }
        else if (list.size() == 2)
        {
            tugasJadual1.setText("" + list.get(0).tugasJadual + " (" + tugas.get(0) + ")");
            tugasJadual2.setText("" + list.get(1).tugasJadual + " (" + tugas.get(1) + ")");
        }
        else if (list.size() == 3)
        {
            tugasJadual1.setText("" + list.get(0).tugasJadual + " (" + tugas.get(0) + ")");
            tugasJadual2.setText("" + list.get(1).tugasJadual + " (" + tugas.get(1) + ")");
            tugasJadual3.setText("" + list.get(2).tugasJadual + " (" + tugas.get(2) + ")");
        }
        else if (list.size() == 4)
        {
            tugasJadual1.setText("" + list.get(0).tugasJadual + " (" + tugas.get(0) + ")");
            tugasJadual2.setText("" + list.get(1).tugasJadual + " (" + tugas.get(1) + ")");
            tugasJadual3.setText("" + list.get(2).tugasJadual + " (" + tugas.get(2) + ")");
            tugasJadual4.setText("" + list.get(3).tugasJadual + " (" + tugas.get(3) + ")");
        }
        else
        {
            tugasJadual1.setText("" + list.get(0).tugasJadual + " (" + tugas.get(0) + ")");
            tugasJadual2.setText("" + list.get(1).tugasJadual + " (" + tugas.get(1) + ")");
            tugasJadual3.setText("" + list.get(2).tugasJadual + " (" + tugas.get(2) + ")");
            tugasJadual4.setText("" + list.get(3).tugasJadual + " (" + tugas.get(3) + ")");
            tugasJadual5.setText("" + list.get(4).tugasJadual + " (" + tugas.get(4) + ")");
        }

        paparanPdf.show();

        butangMuatTurun.setOnClickListener(v1 -> {
                generatePdfFromView(paparanPdf.findViewById(R.id.paparanCetakan));
        });
    }

    private void tunjukkanMaklumat(String value)
    {
        list.clear();
        idJadual1.setText(value);
        String idJadual = idJadual1.getText().toString().trim();
        final String[] tarikhJadual = {tarikhJadual1.getText().toString().trim()};
        db = FirebaseFirestore.getInstance();
        recylerview.setLayoutManager(new LinearLayoutManager(this));
        penyambungJadualPenuh penyambung = new penyambungJadualPenuh(this, list);
        recylerview.setAdapter(penyambung);

        db.collection("JadualTugasan").document(idJadual)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists())
                        {
                            tarikhJadual[0] = (String) documentSnapshot.get("tarikhJadual");
                            tarikhJadual1.setText(tarikhJadual[0]);

                            Map<String, Object> friendsMap = documentSnapshot.getData();
                            for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                                if (entry.getKey().equals("tugasJadual")) {
                                    Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                    int i = 1;
                                    for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                        String in = "" + i;
                                        if (dataEntry.getKey().equals(in)) {
                                            list.add(new TugasPenuh(dataEntry.getValue().toString()));
                                            penyambung.notifyItemInserted(list.size() - 1);
                                        }
                                        i++;
                                    }
                                }
                                if (entry.getKey().equals("statusTugas")) {
                                    Map<String, Object> newFriend1Map = (Map<String, Object>) entry.getValue();
                                    int i = 1;
                                    for (Map.Entry<String, Object> dataEntry1 : newFriend1Map.entrySet()) {
                                        String in = "" + i;
                                        if (dataEntry1.getKey().equals(in)) {
                                            tugas.add(dataEntry1.getValue().toString());
                                        }
                                        i++;
                                    }
                                }
                            }
                        }
                        else {
                            Log.d("TAG", "No such document");
                        }
                    }
                });
    }

    private boolean checkPermissionGranted(){
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            // Permission has already been granted
            return  true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void generatePdfFromView(View view)
    {
        Bitmap bitmap = getBitmapFromView(view);
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo myPageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page myPage = document.startPage(myPageInfo);
        Canvas canvas = myPage.getCanvas();
        canvas.drawBitmap(bitmap, 0, 0, null);
        document.finishPage(myPage);
        String idJadual = idJadual1.getText().toString().trim();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), idJadual + ".pdf");

        try{
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(senaraiPenuhTugasPentadbir.this, "Berjaya Dimuat Turun! Sila semak fail download telefon anda.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(senaraiPenuhTugasPentadbir.this, "Tidak Dimuat Turun! Sila cuba sebentar lagi.", Toast.LENGTH_LONG).show();
        }

        document.close();
    }

    private Bitmap getBitmapFromView(View view)
    {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
        {
            bgDrawable.draw(canvas);
        }
        else
        {
            canvas.drawColor(Color.WHITE);
        }

        view.draw(canvas);
        return returnedBitmap;
    }
}