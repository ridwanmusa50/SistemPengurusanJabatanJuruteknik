// used to display a report for number of complain using pie chart.
package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanBilanganAduan;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class laporanBilanganAduan extends Fragment
{
    private AnyChartView cartaBilanganAduan;
    private final String[] bulan = {"Jul", "Ogo", "Sep", "Okt", "Nov", "Dis"};
    private final int[] aduan = {0, 0, 0, 0, 0, 0};
    private FirebaseFirestore db;
    private SwipeRefreshLayout refreshCarta;
    SharedPreferences sp;

    @SuppressLint("ObsoleteSdkInt")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laporan_bilangan_aduan, container, false);

        db = FirebaseFirestore.getInstance();
        cartaBilanganAduan = v.findViewById(R.id.cartaBilanganAduanPertama);
        Button butangCetakCarta = v.findViewById(R.id.butangCetakCarta);
        refreshCarta = v.findViewById(R.id.refreshCarta);

        cartaBilanganGenerate();

        refreshCarta.setOnRefreshListener(() -> {
            cartaBilanganAduan.clear();
            cartaBilanganGenerate();
            refreshCarta.setRefreshing(false);
        });

        butangCetakCarta.setOnClickListener(v1 -> {
            // Permission for sdk between 23 and 29
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                }
            }

            // Permission storage for sdk 30 or above
            if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.R){
                if (!Environment.isExternalStorageManager()){
                    try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse(String.format("package:%s", requireContext().getPackageName())));
                        requireActivity().startActivityIfNeeded(intent, 101);
                    }catch (Exception e)
                    {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                        requireActivity().startActivityIfNeeded(intent, 101);
                    }
                }
            }

            if(checkPermissionGranted()){
                ciptaPDF();
            }else{
                requestPermission();

                if(checkPermissionGranted()){
                    ciptaPDF();
                }
            }
        });

        return v;
    }

    private void  setupChartView()
    {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();

        for (int i = 0; i < bulan.length; i++)
        {
            dataEntries.add(new ValueDataEntry(bulan[i], aduan[i]));
        }
        pie.data(dataEntries);
        pie.title("Bilangan Aduan Mesin Bagi Jul - Dis");
        cartaBilanganAduan.setChart(pie);
    }

    private boolean checkPermissionGranted(){
        // Permission has already been granted
        return (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions((Activity) requireContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void ciptaPDF()
    {
        final Dialog paparanPdf = new Dialog(getContext());
        paparanPdf.requestWindowFeature(Window.FEATURE_NO_TITLE);
        paparanPdf.setContentView(R.layout.paparan_pdf_carta);
        paparanPdf.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(paparanPdf.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        paparanPdf.getWindow().setAttributes(lp);
        sp = requireContext().getSharedPreferences("AkaunDigunakan", Context.MODE_PRIVATE);

        Button butangMuatTurun = paparanPdf.findViewById(R.id.butangMuatTurun);
        TextView tarikhCetak = paparanPdf.findViewById(R.id.tarikhCetak);
        TextView masaCetak = paparanPdf.findViewById(R.id.masaCetak);
        AnyChartView cartaBilanganAduanPdf = paparanPdf.findViewById(R.id.cartaBilanganAduanPertama);
        TextView idPenggunaCetak = paparanPdf.findViewById(R.id.idPenggunaCetak);

        Pie pie1 = AnyChart.pie();
        List<DataEntry> dataEntries1 = new ArrayList<>();

        for (int i = 0; i < bulan.length; i++)
        {
            dataEntries1.add(new ValueDataEntry(bulan[i], aduan[i]));
        }
        pie1.data(dataEntries1);
        pie1.title("Bilangan Aduan Mesin Bagi Jul - Dis");
        cartaBilanganAduanPdf.setChart(pie1);

        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm a", Locale.getDefault()).format(new Date());

        tarikhCetak.setText(currentDate);
        masaCetak.setText(currentTime);
        idPenggunaCetak.setText(sp.getString("idPengguna", ""));
        paparanPdf.show();

        butangMuatTurun.setOnClickListener(v1 -> generatePdfFromView(paparanPdf.findViewById(R.id.paparanCetakan)));

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
        String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),  currentDate + ".pdf");

        try{
            document.writeTo(Files.newOutputStream(file.toPath()));
            Toast.makeText(getContext(), "Berjaya Dimuat Turun! Sila semak fail download telefon anda.", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Tidak Dimuat Turun! Sila cuba sebentar lagi.", Toast.LENGTH_LONG).show();
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

    private void cartaBilanganGenerate()
    {
        db = FirebaseFirestore.getInstance();

        Pattern jul = Pattern.compile("Jul");
        Pattern ogo = Pattern.compile("Aug");
        Pattern sep = Pattern.compile("Sep");
        Pattern okt = Pattern.compile("Oct");
        Pattern nov = Pattern.compile("Nov");
        Pattern dis = Pattern.compile("Dec");

        db.collection("AduanKerosakan")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful())
                    {
                        int size = task.getResult().size();
                        Matcher jul1, ogo1, sep1, okt1, nov1, dis1;

                        for (int i = 0; i < size; i ++)
                        {
                            jul1 = jul.matcher((CharSequence) Objects.requireNonNull(task.getResult().getDocuments().get(i).get("tarikhAduan")));
                            while (jul1.find()) {
                                aduan[0] = aduan[0] + 1;
                            }

                            ogo1 = ogo.matcher((CharSequence) Objects.requireNonNull(task.getResult().getDocuments().get(i).get("tarikhAduan")));
                            while (ogo1.find()) {
                                aduan[1] = aduan[1] + 1;
                            }

                            sep1 = sep.matcher((CharSequence) Objects.requireNonNull(task.getResult().getDocuments().get(i).get("tarikhAduan")));
                            while (sep1.find()) {
                                aduan[2] = aduan[2] + 1;
                            }

                            okt1 = okt.matcher((CharSequence) Objects.requireNonNull(task.getResult().getDocuments().get(i).get("tarikhAduan")));
                            while (okt1.find()) {
                                aduan[3] = aduan[3] + 1;
                            }

                            nov1 = nov.matcher((CharSequence) Objects.requireNonNull(task.getResult().getDocuments().get(i).get("tarikhAduan")));
                            while (nov1.find()) {
                                aduan[4] = aduan[4] + 1;
                            }

                            dis1 = dis.matcher((CharSequence) Objects.requireNonNull(task.getResult().getDocuments().get(i).get("tarikhAduan")));
                            while (dis1.find()) {
                                aduan[5] = aduan[5] + 1;
                            }
                        }

                        setupChartView();
                    }
                });
    }
}
