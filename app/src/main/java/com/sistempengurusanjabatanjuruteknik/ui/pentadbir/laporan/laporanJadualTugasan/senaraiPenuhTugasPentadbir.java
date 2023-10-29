// Used to generate full report of technician tasks
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanJadualTugasan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentLaporanSenaraiTugasKerosakanBinding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class senaraiPenuhTugasPentadbir extends AppCompatActivity {
    
    private FragmentLaporanSenaraiTugasKerosakanBinding binding;
    
    private FirebaseFirestore db;
    private final ArrayList<TugasPenuh> list = new ArrayList<>();
    private final ArrayList<String> tugas = new ArrayList<>();
    int count = 0;

    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = FragmentLaporanSenaraiTugasKerosakanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String value = null;
        // terima idJadual daripada senarai jadual
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("lokasi");
        }

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        penyambungJadualPenuh penyambung = new penyambungJadualPenuh(list);
        binding.recyclerView.setAdapter(penyambung);

        db = FirebaseFirestore.getInstance();

        tunjukkanMaklumat(value);

        binding.butangCetakTugas.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(senaraiPenuhTugasPentadbir.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                }
            }

            // Permission storage for sdk 30 or above
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                if (!Environment.isExternalStorageManager()){
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                        startActivityIfNeeded(intent, 101);
                    }catch (Exception e)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        startActivityIfNeeded(intent, 101);
                    }
                }
            }

            String idJadual = binding.idJadual.getText().toString().trim();
            String tarikhJadual = binding.tarikhJadual.getText().toString().trim();

            if(checkPermissionGranted()){
                ciptaPDF(idJadual, tarikhJadual);
            }else{
                requestPermission();

                if(checkPermissionGranted()){
                    ciptaPDF(idJadual, tarikhJadual);
                }
            }
            });
    }

    @SuppressLint("SetTextI18n")
    private void ciptaPDF(String idJadual, String tarikhJadual)
    {
        final Dialog paparanPdf = new Dialog(this);
        paparanPdf.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paparanPdf.setContentView(R.layout.paparan_pdf_tugas);
        paparanPdf.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(Objects.requireNonNull(paparanPdf.getWindow()).getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1800;
        lp.dimAmount = 10;
        lp.gravity = Gravity.CENTER;
        paparanPdf.getWindow().setAttributes(lp);

        Button butangMuatTurun = paparanPdf.findViewById(R.id.butangMuatTurun);
        TextView tarikhCetak = paparanPdf.findViewById(R.id.tarikhCetak);
        TextView masaCetak = paparanPdf.findViewById(R.id.masaCetak);

        TextView idJadualCetak = paparanPdf.findViewById(R.id.idJadual);
        TextView tarikhJadualCetak = paparanPdf.findViewById(R.id.tarikhJadual);

        TextView tugasJadual1 = paparanPdf.findViewById(R.id.tugasJadual1);
        TextView tugasJadual2 = paparanPdf.findViewById(R.id.tugasJadual2);
        TextView tugasJadual3 = paparanPdf.findViewById(R.id.tugasJadual3);
        TextView tugasJadual4 = paparanPdf.findViewById(R.id.tugasJadual4);
        TextView tugasJadual5 = paparanPdf.findViewById(R.id.tugasJadual5);

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        db = FirebaseFirestore.getInstance();

        tarikhCetak.setText(currentDate);
        masaCetak.setText(currentTime);
        idJadualCetak.setText(idJadual);
        tarikhJadualCetak.setText(tarikhJadual);

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

        butangMuatTurun.setOnClickListener(v1 -> generatePdfFromView(paparanPdf.findViewById(R.id.paparanCetakan)));
    }

    /** @noinspection unchecked*/
    private void tunjukkanMaklumat(String value)
    {
        list.clear();
        binding.idJadual.setText(value);
        String idJadual = binding.idJadual.getText().toString().trim();
        final String[] tarikhJadual = {binding.tarikhJadual.getText().toString().trim()};
        db = FirebaseFirestore.getInstance();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        penyambungJadualPenuh penyambung = new penyambungJadualPenuh(list);
        binding.recyclerView.setAdapter(penyambung);

        db.collection("JadualTugasan").document(idJadual)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists())
                    {
                        tarikhJadual[0] = (String) documentSnapshot.get("tarikhJadual");
                        binding.tarikhJadual.setText(tarikhJadual[0]);

                        Map<String, Object> friendsMap = documentSnapshot.getData();
                        assert friendsMap != null;
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
                });
    }

    private boolean checkPermissionGranted(){
        // Permission has already been granted
        return (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
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
        String idJadual = binding.idJadual.getText().toString().trim();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), idJadual + ".pdf");

        while (file.exists()){
            count++;
            String newFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + idJadual+ "(" + count + ")" + ".pdf";
            File newFile = new File(newFilePath);
            if (file.renameTo(newFile)) {
                file = newFile;
            }
        }

        try{
            if (file.createNewFile()) {
                document.writeTo(Files.newOutputStream(file.toPath()));
                Toast.makeText(senaraiPenuhTugasPentadbir.this, "Berjaya Dimuat Turun! Sila semak fail download telefon anda.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(senaraiPenuhTugasPentadbir.this, "Tidak Berjaya Mencipta Fail! Sila cuba sebentar lagi.", Toast.LENGTH_LONG).show();
            }
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