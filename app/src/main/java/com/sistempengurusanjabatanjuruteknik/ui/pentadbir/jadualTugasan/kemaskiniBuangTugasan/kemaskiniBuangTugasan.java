package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.kemaskiniBuangTugasan;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.Tugas;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.penyambungJadual;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class kemaskiniBuangTugasan extends Fragment implements DatePickerDialog.OnDateSetListener
{
    private EditText tarikhJadual1;
    private EditText idJadual1;
    private Button butangCariTarikh1;
    private Button butangKemaskiniTugas1;
    private Button butangBuangTugas1;
    private Button butangTambahSenaraiTugas1;
    private RecyclerView recylerview;
    private FirebaseFirestore db; // declare Firebase Firestore variable
    private ProgressBar progressBar;
    private ArrayList<Tugas> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kemaskini_buang_tugasan_pentadbir, container, false);

        tarikhJadual1 = v.findViewById(R.id.tarikhJadual);
        idJadual1 = v.findViewById(R.id.idJadual);
        butangCariTarikh1 = v.findViewById(R.id.butangCariTarikh);
        butangKemaskiniTugas1 = v.findViewById(R.id.butangKemaskiniTugas);
        butangBuangTugas1 = v.findViewById(R.id.butangBuangTugas);
        butangTambahSenaraiTugas1 = v.findViewById(R.id.butangTambahSenaraiTugas);
        recylerview = v.findViewById(R.id.recyclerView);
        db = FirebaseFirestore.getInstance();
        progressBar = v.findViewById(R.id.progressBar);

        recylerview.setLayoutManager(new LinearLayoutManager(getContext()));
        penyambungJadual penyambung = new penyambungJadual(getContext(), list);
        recylerview.setAdapter(penyambung);

        initialize();

        butangCariTarikh1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tunjukJadual();
            }
        });

        butangTambahSenaraiTugas1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.tambah_kemaskini_jadual);

                EditText tugasJadual1 = dialog.findViewById(R.id.tugasJadual);
                Button butangTambah = dialog.findViewById(R.id.butangTambah);

                butangTambah.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tugasJadual = tugasJadual1.getText().toString().trim();

                        if (tugasJadual.isEmpty())
                        {
                            tugasJadual1.setError("Tugasan perlu diisi!");
                            tugasJadual1.requestFocus();
                            return;
                        }
                        else
                        {
                            list.add(new Tugas(tugasJadual));
                            penyambung.notifyItemInserted(list.size() - 1);

                            recylerview.scrollToPosition(list.size() - 1);
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });

        butangBuangTugas1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idJadual = idJadual1.getText().toString().trim();

                AlertDialog dialog = new AlertDialog.Builder(getContext())
                        .setTitle("Adakah anda pasti?")
                        .setMessage("Tindakan membuang jadual ini akan membuatkan jadual dikeluarkan dari sistem sepenuhnya")
                        .setPositiveButton("Buang", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.setVisibility(View.VISIBLE);

                                db.collection("JadualTugasan").document(idJadual)
                                        .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(getContext(), "Sistem berjaya membuang jadual!", Toast.LENGTH_LONG).show();
                                                        startActivity(new Intent(getContext(), getActivity().getClass()));
                                                    }
                                                });
                            }
                        })
                        .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }
        });

        butangKemaskiniTugas1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tarikhJadual = tarikhJadual1.getText().toString().trim();
                String idJadual = idJadual1.getText().toString().trim();

                Map<String, Object> tugasan = new HashMap<>();
                tugasan.put("idJadual", idJadual);
                tugasan.put("tarikhJadual", tarikhJadual);

                Map<String, Object> tugasJadual2 = new HashMap<>();
                Map<String, String> statusTugas2 = new HashMap<>();

                for (int i = 0; i < list.size(); i++)
                {
                    tugasJadual2.put("" + (i+1), list.get(i).tugasJadual);
                    statusTugas2.put("" + (i+1), "TIDAK SELESAI");
                }

                tugasan.put("tugasJadual", tugasJadual2);
                tugasan.put("statusTugas", statusTugas2);

                if (tarikhJadual.isEmpty())
                {
                    Toast.makeText(getContext(), "Tarikh jadual perlu dimasukkan!", Toast.LENGTH_LONG).show();
                    return;
                }
                else
                {
                    if (list.isEmpty())
                    {
                        Toast.makeText(getContext(), "Senarai tugas perlu dimasukkan!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    else {
                        progressBar.setVisibility(View.VISIBLE);

                        db.collection("JadualTugasan").document(idJadual)
                                .update(tugasan).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        progressBar.setVisibility(View.GONE);

                                        Toast.makeText(getContext(), "Sistem berjaya mengemaskini jadual!", Toast.LENGTH_LONG).show();

                                        startActivity(new Intent(getContext(), getActivity().getClass()));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Sistem gagal mengemaskini jadual! Sila cuba lagi", Toast.LENGTH_LONG).show();
                                    }
                                });
                    }
                }
            }
        });

        return v;
    }

    private void tunjukJadual()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() +24*60*60*1000);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String[] bulan= {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        String haribulan = "";

        for (int i = 0; i < bulan.length; i++)
        {
            if (month == i)
            {
                haribulan = bulan[i];
                break;
            }
        }

        String tarikh;

        if (dayOfMonth < 10)
        {
            tarikh = "0" + dayOfMonth + "/" + haribulan + "/" + year;
        }
        else
        {
            tarikh = "" + dayOfMonth + "/" + haribulan + "/" + year;
        }

        tarikhJadual1.getText().clear();
        list.clear();

        db = FirebaseFirestore.getInstance();
        db.collection("JadualTugasan")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @SuppressLint("ResourceAsColor")
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                            for (DocumentSnapshot snapshot : snapshotList) {
                                if (snapshot.getString("tarikhJadual").equals(tarikh)) {
                                    tarikhJadual1.setText(tarikh);
                                    String idJadual = snapshot.getString("idJadual");

                                    idJadual1.setText(idJadual);
                                    butangTambahSenaraiTugas1.setEnabled(true);
                                    butangBuangTugas1.setEnabled(true);
                                    butangKemaskiniTugas1.setEnabled(true);
                                    butangTambahSenaraiTugas1.setBackgroundColor(getResources().getColor(R.color.tema));
                                    butangKemaskiniTugas1.setBackgroundColor(getResources().getColor(R.color.tema));
                                    butangBuangTugas1.setBackgroundColor(getResources().getColor(R.color.tema));

                                    paparkanTugas();
                                    break;
                                }
                            }

                            if (tarikhJadual1.getText().toString().isEmpty())
                            {
                                penyambungJadual penyambung = new penyambungJadual(getContext(), list);
                                idJadual1.getText().clear();
                                initialize();
                                recylerview.setAdapter(penyambung);

                                Toast.makeText(getContext(), "Tarikh tidak mempunyai sebarang tugas! Sila pilih tarikh yang lain.", Toast.LENGTH_LONG).show();
                                return;
                            }
                        }
                        else
                        {
                            idJadual1.getText().clear();

                            Toast.makeText(getContext(), "Tarikh tidak mempunyai sebarang tugas!", Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                });
    }

    private void initialize()
    {
        butangKemaskiniTugas1.setEnabled(false);
        butangBuangTugas1.setEnabled(false);
        butangBuangTugas1.setBackgroundColor(Color.GRAY);
        butangKemaskiniTugas1.setBackgroundColor(Color.GRAY);
        butangTambahSenaraiTugas1.setEnabled(false);
        butangTambahSenaraiTugas1.setBackgroundColor(Color.GRAY);
    }

    private void paparkanTugas()
    {
        String idJadual = idJadual1.getText().toString().trim();
        db = FirebaseFirestore.getInstance();
        recylerview.setLayoutManager(new LinearLayoutManager(getContext()));
        penyambungJadual penyambung = new penyambungJadual(getContext(), list);
        recylerview.setAdapter(penyambung);

        db.collection("JadualTugasan").document(idJadual)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> friendsMap = document.getData();
                        for (Map.Entry<String, Object> entry : friendsMap.entrySet()) {
                            if (entry.getKey().equals("tugasJadual")) {
                                Map<String, Object> newFriend0Map = (Map<String, Object>) entry.getValue();
                                int i = 1;
                                for (Map.Entry<String, Object> dataEntry : newFriend0Map.entrySet()) {
                                    String in = "" + i;
                                    if (dataEntry.getKey().equals(in)) {
                                        list.add(new Tugas(dataEntry.getValue().toString()));
                                        penyambung.notifyItemInserted(list.size() - 1);
                                    }
                                    i++;
                                }
                            }
                        }
                    } else {
                        Log.d("TAG", "No such document");
                    }
                } else {
                    Log.d("TAG", "get failed with ", task.getException());
                }
            }
        });
    }
}