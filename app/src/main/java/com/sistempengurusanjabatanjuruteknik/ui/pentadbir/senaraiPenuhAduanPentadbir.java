// used to display all information about complaint
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentLaporanAduanKerosakanBinding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class senaraiPenuhAduanPentadbir extends AppCompatActivity {
    private FragmentLaporanAduanKerosakanBinding binding;
    private FirebaseFirestore db;
    SharedPreferences sp;
    int count = 0;
    @SuppressLint("ObsoleteSdkInt")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentLaporanAduanKerosakanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String value = null;
        // terima idAduan daripada senarai aduan
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("lokasi");
        }
        
        db = FirebaseFirestore.getInstance();

        tunjukkanMaklumat(value);

        binding.butangTambahPengesah.setOnClickListener(v -> {
            String idAduan = binding.idAduan.getText().toString().trim();
            String tarikhAduan = binding.tarikhAduan.getText().toString().trim();
            String masaAduan = binding.masaAduan.getText().toString().trim();
            String namaPenuhPengadu = binding.namaPenuhPengadu.getText().toString().trim();
            String idMesin = binding.idMesin.getText().toString().trim();
            String huraianAduan = binding.hurainAduan.getText().toString().trim();
            String idJuruteknik = binding.idJuruteknik.getText().toString().trim();
            String tarikhPenerima = binding.tarikhPenerima.getText().toString().trim();
            String masaPenerima = binding.masaPenerima.getText().toString().trim();
            String huraianPenerima = binding.huraianPenerima.getText().toString().trim();
            String tarikhPengesah = binding.tarikhPengesah.getText().toString().trim();
            String masaPengesah = binding.masaPengesah.getText().toString().trim();
            String idPentadbir = binding.idPentadbir.getText().toString().trim();

            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
            binding.tarikhPengesah.setText(currentDate);

            String currentTime = new SimpleDateFormat("hh:mm:ss", Locale.getDefault()).format(new Date());
            binding.masaPengesah.setText(currentTime);

            Map<String, Object> pengesah = new HashMap<>();
            pengesah.put("idAduan", idAduan);
            pengesah.put("tarikhAduan", tarikhAduan);
            pengesah.put("masaAduan", masaAduan);
            pengesah.put("namaPenuhPengadu", namaPenuhPengadu);
            pengesah.put("idMesin", idMesin);
            pengesah.put("huraianAduan", huraianAduan);
            pengesah.put("idJuruteknik", idJuruteknik);
            pengesah.put("tarikhPenerima", tarikhPenerima);
            pengesah.put("masaPenerima", masaPenerima);
            pengesah.put("huraianPenerima", huraianPenerima);
            pengesah.put("tarikhPengesah", tarikhPengesah);
            pengesah.put("masaPengesah", masaPengesah);
            pengesah.put("idPentadbir", idPentadbir);

            if (idJuruteknik.isEmpty())
            {
                Toast.makeText(senaraiPenuhAduanPentadbir.this, "Juruteknik masih belum menyelesaikan aduan!", Toast.LENGTH_LONG).show();
            }
            else
            {
                binding.progressBar.setVisibility(View.VISIBLE);
                db.collection("AduanKerosakan").document(idAduan)
                                .get()
                                        .addOnSuccessListener(documentSnapshot -> db.collection("AduanKerosakan").document(idAduan)
                                                    .set(pengesah)
                                                    .addOnSuccessListener(unused -> {
                                                        binding.progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(senaraiPenuhAduanPentadbir.this, "Sistem berjaya menambah pengesahan maklumat!", Toast.LENGTH_LONG).show();

                                                        startActivity(new Intent(senaraiPenuhAduanPentadbir.this, utamaPentadbir.class));
                                                    }));
            }
        });

        binding.butangCetakAduan.setOnClickListener(v -> {
            // Permission for sdk between 23 and 29
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(senaraiPenuhAduanPentadbir.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
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

            String idAduan2 = binding.idAduan.getText().toString().trim();
            String tarikhAduan2 = binding.tarikhAduan.getText().toString().trim();
            String masaAduan2 = binding.masaAduan.getText().toString().trim();
            String namaPenuhPengadu2 = binding.namaPenuhPengadu.getText().toString().trim();
            String idMesin2 = binding.idMesin.getText().toString().trim();
            String huraianAduan2 = binding.hurainAduan.getText().toString().trim();
            String idJuruteknik2 = binding.idJuruteknik.getText().toString().trim();
            String tarikhPenerima2 = binding.tarikhPenerima.getText().toString().trim();
            String masaPenerima2 = binding.masaPenerima.getText().toString().trim();
            String huraianPenerima2 = binding.huraianPenerima.getText().toString().trim();
            String tarikhPengesah2 = binding.tarikhPengesah.getText().toString().trim();
            String masaPengesah2 = binding.masaPengesah.getText().toString().trim();
            String idPentadbir2 = binding.idPentadbir.getText().toString().trim();

            if(checkPermissionGranted()){
                ciptaPDF(idAduan2, idMesin2, namaPenuhPengadu2, tarikhAduan2, masaAduan2, huraianAduan2,
                        tarikhPenerima2, masaPenerima2, idJuruteknik2, huraianPenerima2,
                        tarikhPengesah2, masaPengesah2, idPentadbir2);
            }else{
                requestPermission();

                if(checkPermissionGranted()){
                    ciptaPDF(idAduan2, idMesin2, namaPenuhPengadu2, tarikhAduan2, masaAduan2, huraianAduan2,
                            tarikhPenerima2, masaPenerima2, idJuruteknik2, huraianPenerima2,
                            tarikhPengesah2, masaPengesah2, idPentadbir2);
                }
            }


            });
    }

    private void ciptaPDF(String idAduan, String idMesin, String namaPenuhPengadu, String tarikhAduan, String masaAduan, String huraianAduan,
                          String tarikhPenerima, String masaPenerima, String idJuruteknik, String huraianPenerima,
                          String tarikhPengesah, String masaPengesah, String idPentadbir)
    {
        final Dialog paparanPdf = new Dialog(this);
        paparanPdf.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paparanPdf.setContentView(R.layout.paparan_pdf_aduan);
        paparanPdf.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(paparanPdf.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = 1800;
        lp.dimAmount = 10;
        lp.gravity = Gravity.CENTER;
        paparanPdf.getWindow().setAttributes(lp);

        Button butangMuatTurun = paparanPdf.findViewById(R.id.butangMuatTurun);
        TextView tarikhCetak = paparanPdf.findViewById(R.id.tarikhCetak);
        TextView masaCetak = paparanPdf.findViewById(R.id.masaCetak);

        TextView idAduanCetak = paparanPdf.findViewById(R.id.idAduan);
        TextView idMesinCetak = paparanPdf.findViewById(R.id.idMesin);
        TextView tarikhAduanCetak = paparanPdf.findViewById(R.id.tarikhAduan);
        TextView masaAduanCetak = paparanPdf.findViewById(R.id.masaAduan);
        TextView namaPenuhPengaduCetak = paparanPdf.findViewById(R.id.namaPenuhPengadu);
        TextView huraianAduanCetak = paparanPdf.findViewById(R.id.hurainAduan);

        TextView tarikhPenerimaCetak = paparanPdf.findViewById(R.id.tarikhPenerima);
        TextView masaPenerimaCetak = paparanPdf.findViewById(R.id.masaPenerima);
        TextView idJuruteknikCetak = paparanPdf.findViewById(R.id.idJuruteknik);
        TextView huraianPenerimaCetak = paparanPdf.findViewById(R.id.huraianPenerima);

        TextView tarikhPengesahCetak = paparanPdf.findViewById(R.id.tarikhPengesah);
        TextView masaPengesahCetak = paparanPdf.findViewById(R.id.masaPengesah);
        TextView idPentadbirCetak = paparanPdf.findViewById(R.id.idPentadbir);

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        db = FirebaseFirestore.getInstance();

        tarikhCetak.setText(currentDate);
        masaCetak.setText(currentTime);
        idAduanCetak.setText(idAduan);
        idMesinCetak.setText(idMesin);
        tarikhAduanCetak.setText(tarikhAduan);
        masaAduanCetak.setText(masaAduan);
        namaPenuhPengaduCetak.setText(namaPenuhPengadu);
        huraianAduanCetak.setText(huraianAduan);
        tarikhPenerimaCetak.setText(tarikhPenerima);
        masaPenerimaCetak.setText(masaPenerima);
        idJuruteknikCetak.setText(idJuruteknik);
        huraianPenerimaCetak.setText(huraianPenerima);
        tarikhPengesahCetak.setText(tarikhPengesah);
        masaPengesahCetak.setText(masaPengesah);
        idPentadbirCetak.setText(idPentadbir);

        paparanPdf.show();

        butangMuatTurun.setOnClickListener(v1 -> generatePdfFromView(paparanPdf.findViewById(R.id.paparanCetakan)));
    }

    private void tunjukkanMaklumat(String value)
    {
        binding.idAduan.setText(value);
        String idAduan = binding.idAduan.getText().toString().trim();
        final String[] idMesin = {binding.idMesin.getText().toString().trim()};
        final String[] namapenuhPengadu = {binding.namaPenuhPengadu.getText().toString().trim()};
        final String[] tarikhAduan = {binding.tarikhAduan.getText().toString().trim()};
        final String[] masaAduan = {binding.masaAduan.getText().toString().trim()};
        final String[] huraianAduan = {binding.hurainAduan.getText().toString().trim()};
        final String[] idJuruteknik = {binding.idJuruteknik.getText().toString().trim()};
        final String[] tarikhPenerima = {binding.tarikhPenerima.getText().toString().trim()};
        final String[] masaPenerima = {binding.masaPenerima.getText().toString().trim()};
        final String[] huraianPenerima = {binding.huraianPenerima.getText().toString().trim()};
        final String[] tarikhPengesah = {binding.tarikhPengesah.getText().toString().trim()};
        final String[] masaPengesah = {binding.masaPengesah.getText().toString().trim()};
        final String[] idPentadbir = {binding.idPentadbir.getText().toString().trim()};
        sp = getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        binding.butangCetakAduan.setEnabled(false);

        db.collection("AduanKerosakan").document(idAduan)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists())
                    {
                        idMesin[0] = (String) documentSnapshot.get("idMesin");
                        binding.idMesin.setText(idMesin[0]);

                        namapenuhPengadu[0] = (String) documentSnapshot.get("namaPenuhPengadu");
                        binding.namaPenuhPengadu.setText(namapenuhPengadu[0]);

                        tarikhAduan[0] = (String) documentSnapshot.get("tarikhAduan");
                        binding.tarikhAduan.setText(tarikhAduan[0]);

                        masaAduan[0] = (String) documentSnapshot.get("masaAduan");
                        binding.masaAduan.setText(masaAduan[0]);

                        huraianAduan[0] = (String) documentSnapshot.get("huraianAduan");
                        binding.hurainAduan.setText(huraianAduan[0]);

                        if (documentSnapshot.contains("idJuruteknik"))
                        {
                            tarikhPenerima[0] = (String) documentSnapshot.get("tarikhPenerima");
                            binding.tarikhPenerima.setText(tarikhPenerima[0]);

                            masaPenerima[0] = (String) documentSnapshot.get("masaPenerima");
                            binding.masaPenerima.setText(masaPenerima[0]);

                            idJuruteknik[0] = (String) documentSnapshot.get("idJuruteknik");
                            binding.idJuruteknik.setText(idJuruteknik[0]);

                            huraianPenerima[0] = (String) documentSnapshot.get("huraianPenerima");
                            binding.huraianPenerima.setText(huraianPenerima[0]);

                            String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
                            binding.tarikhPengesah.setText(currentDate);

                            String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());
                            binding.masaPengesah.setText(currentTime);
                        }

                        if (documentSnapshot.contains("idPentadbir"))
                        {
                            tarikhPengesah[0] = (String) documentSnapshot.get("tarikhPengesah");
                            binding.tarikhPengesah.setText(tarikhPengesah[0]);

                            masaPengesah[0] = (String) documentSnapshot.get("masaPengesah");
                            binding.masaPengesah.setText(masaPengesah[0]);

                            idPentadbir[0] = (String) documentSnapshot.get("idPentadbir");
                            binding.idPentadbir.setText(idPentadbir[0]);

                            binding.butangTambahPengesah.setEnabled(false);
                            binding.butangTambahPengesah.setBackgroundColor(Color.GRAY);
                            binding.butangCetakAduan.setEnabled(true);
                        }

                        if (!documentSnapshot.contains("idJuruteknik"))
                        {
                            binding.butangTambahPengesah.setEnabled(false);
                            binding.butangCetakAduan.setEnabled(false);
                            binding.butangTambahPengesah.setBackgroundColor(Color.GRAY);
                            binding.butangCetakAduan.setBackgroundColor(Color.GRAY);
                        }
                    }
                });

        binding.idPentadbir.setText(sp.getString("idPengguna", ""));
    }

    private boolean checkPermissionGranted(){
        // Permission has already been granted
        return (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getApplication(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
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
        String idAduan = binding.idAduan.getText().toString().trim();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), idAduan + ".pdf");

        while (file.exists()){
            count++;
            file.renameTo(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), idAduan + "(" + count + ")" + ".pdf"));
        }

        try{
            document.writeTo(Files.newOutputStream(file.toPath()));
            Toast.makeText(senaraiPenuhAduanPentadbir.this, "Berjaya Dimuat Turun! Sila semak fail download telefon anda.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(senaraiPenuhAduanPentadbir.this, "Tidak Dimuat Turun! Sila cuba sebentar lagi.", Toast.LENGTH_LONG).show();
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