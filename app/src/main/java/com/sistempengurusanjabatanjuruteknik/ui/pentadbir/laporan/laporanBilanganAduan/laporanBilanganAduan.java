package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.laporan.laporanBilanganAduan;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sistempengurusanjabatanjuruteknik.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class laporanBilanganAduan extends Fragment
{
    private AnyChartView cartaBilanganAduan;
    private String[] bulan = {"Jul", "Ogo", "Sep", "Okt", "Nov", "Dis"};
    private int[] aduan = {0, 0, 0, 0, 0, 0};
    private FirebaseFirestore db;
    private Button butangCetakCarta;
    private SwipeRefreshLayout refreshCarta;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laporan_bilangan_aduan, container, false);

        db = FirebaseFirestore.getInstance();
        cartaBilanganAduan = v.findViewById(R.id.cartaBilanganAduanPertama);
        butangCetakCarta = v.findViewById(R.id.butangCetakCarta);
        refreshCarta = v.findViewById(R.id.refreshCarta);

        cartaBilanganGenerate();

        refreshCarta.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cartaBilanganAduan.clear();
                cartaBilanganGenerate();
                refreshCarta.setRefreshing(false);
            }
        });

        butangCetakCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, PackageManager.PERMISSION_GRANTED);

                if(checkPermissionGranted()){
                    ciptaPDF();
                }else{
                    requestPermission();

                    if(checkPermissionGranted()){
                        ciptaPDF();
                    }
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
        if((ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            // Permission has already been granted
            return  true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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

        Button butangMuatTurun = paparanPdf.findViewById(R.id.butangMuatTurun);
        TextView tarikhCetak = paparanPdf.findViewById(R.id.tarikhCetak);
        TextView masaCetak = paparanPdf.findViewById(R.id.masaCetak);
        AnyChartView cartaBilanganAduanPdf = paparanPdf.findViewById(R.id.cartaBilanganAduanPertama);

        Pie pie1 = AnyChart.pie();
        List<DataEntry> dataEntries1 = new ArrayList<>();

        for (int i = 0; i < bulan.length; i++)
        {
            dataEntries1.add(new ValueDataEntry(bulan[i], aduan[i]));
        }
        pie1.data(dataEntries1);
        pie1.title("Bilangan Aduan Mesin Bagi Jul - Dis");
        cartaBilanganAduanPdf.setChart(pie1);

        String currentDate = new SimpleDateFormat("dd/MMM/YYYY", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());

        tarikhCetak.setText(currentDate);
        masaCetak.setText(currentTime);
        paparanPdf.show();

        butangMuatTurun.setOnClickListener(v1 -> {
            generatePdfFromView(paparanPdf.findViewById(R.id.paparanCetakan));
        });

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
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),  "L001.pdf");

        try{
            document.writeTo(new FileOutputStream(file));
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
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            int size = task.getResult().size();
                            Matcher jul1, ogo1, sep1, okt1, nov1, dis1;

                            for (int i = 0; i < size; i ++)
                            {
                                jul1 = jul.matcher((CharSequence) task.getResult().getDocuments().get(i).get("tarikhAduan"));
                                while (jul1.find()) {
                                    aduan[0] = aduan[0] + 1;
                                    break;
                                }

                                ogo1 = ogo.matcher((CharSequence) task.getResult().getDocuments().get(i).get("tarikhAduan"));
                                while (ogo1.find()) {
                                    aduan[1] = aduan[1] + 1;
                                    break;
                                }

                                sep1 = sep.matcher((CharSequence) task.getResult().getDocuments().get(i).get("tarikhAduan"));
                                while (sep1.find()) {
                                    aduan[2] = aduan[2] + 1;
                                    break;
                                }

                                okt1 = okt.matcher((CharSequence) task.getResult().getDocuments().get(i).get("tarikhAduan"));
                                while (okt1.find()) {
                                    aduan[3] = aduan[3] + 1;
                                    break;
                                }

                                nov1 = nov.matcher((CharSequence) task.getResult().getDocuments().get(i).get("tarikhAduan"));
                                while (nov1.find()) {
                                    aduan[4] = aduan[4] + 1;
                                    break;
                                }

                                dis1 = dis.matcher((CharSequence) task.getResult().getDocuments().get(i).get("tarikhAduan"));
                                while (dis1.find()) {
                                    aduan[5] = aduan[5] + 1;
                                    break;
                                }
                            }

                            setupChartView();
                        }
                    }
                });
    }
}
