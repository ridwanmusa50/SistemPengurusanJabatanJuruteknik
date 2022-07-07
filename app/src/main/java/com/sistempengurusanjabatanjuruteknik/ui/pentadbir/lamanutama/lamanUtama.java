package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.lamanutama;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.Aduan;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.senaraiPenuhAduanPentadbir;
import com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class lamanUtama extends Fragment implements com.sistempengurusanjabatanjuruteknik.ui.penyambungSenaraiAduan.OnAduanListener
{
    private SwipeRefreshLayout refresh, refreshCarta;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private penyambungSenaraiAduan penyambungSenaraiAduan;
    private ArrayList<Aduan> list;
    private AnyChartView cartaBilanganAduan;
    private String[] bulan = {"Jul", "Ogo", "Sep", "Okt", "Nov", "Dis"};
    private int[] aduan = {0, 0, 0, 0, 0, 0};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_laman_utama_pentadbir, container, false);

        recyclerView = v.findViewById(R.id.senaraiAduan);
        db = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        refresh = v.findViewById(R.id.refresh);
        refreshCarta = v.findViewById(R.id.refreshCarta);
        list = new ArrayList<>();
        penyambungSenaraiAduan = new penyambungSenaraiAduan(getContext(), list, this::onAduanClick);
        recyclerView.setAdapter(penyambungSenaraiAduan);
        cartaBilanganAduan = v.findViewById(R.id.cartaBilanganAduanPertama);

        cartaGenerate();

        refreshCarta.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cartaBilanganAduan.clear();
                cartaGenerate();
                refreshCarta.setRefreshing(false);
            }
        });

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                db.collection("AduanKerosakan").orderBy("idAduan", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null)
                                {
                                    Log.e("Firestore bermasalah", error.getMessage());
                                    return;
                                }

                                for (DocumentChange dc : value.getDocumentChanges())
                                {
                                    if (dc.getType() == DocumentChange.Type.ADDED)
                                    {
                                        // Tugas selesai sahaja dipamer
                                        if (!dc.getDocument().contains("idPentadbir"))
                                        {
                                            list.add(dc.getDocument().toObject(Aduan.class));
                                        }
                                    }
                                    penyambungSenaraiAduan.notifyDataSetChanged();
                                }
                            }
                        });
                refresh.setRefreshing(false);
            }
        });

        db.collection("AduanKerosakan").orderBy("idAduan", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null)
                        {
                            Log.e("Firestore bermasalah", error.getMessage());
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges())
                        {
                            if (dc.getType() == DocumentChange.Type.ADDED)
                            {
                                // Tugas selesai sahaja dipamer
                                if (!dc.getDocument().contains("idPentadbir"))
                                {
                                    list.add(dc.getDocument().toObject(Aduan.class));
                                }
                            }
                            penyambungSenaraiAduan.notifyDataSetChanged();
                        }
                    }
                });

        return v;
    }

    // aduan ditekan
    @Override
    public void onAduanClick(int position) {
        Intent intent = new Intent(getContext(), senaraiPenuhAduanPentadbir.class);
        intent.putExtra("lokasi", list.get(position).getIdAduan());
        startActivity(intent);
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

    private void cartaGenerate()
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
