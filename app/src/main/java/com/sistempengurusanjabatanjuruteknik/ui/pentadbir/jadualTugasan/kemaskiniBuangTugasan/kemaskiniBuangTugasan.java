// Used for add, modify and delete the task in the schedule.

package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.kemaskiniBuangTugasan;

import android.app.DatePickerDialog;
import android.app.Dialog;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.Tugas;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.penyambungJadual;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class kemaskiniBuangTugasan extends Fragment implements DatePickerDialog.OnDateSetListener
{
    private EditText tarikhJadual1;
    private EditText idJadual1;
    private Button butangKemaskiniTugas1;
    private Button butangBuangTugas1;
    private Button butangTambahSenaraiTugas1;
    private RecyclerView recylerview;
    private FirebaseFirestore db; // declare Firebase Firestore variable
    private ProgressBar progressBar;
    private final ArrayList<Tugas> list = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_kemaskini_buang_tugasan_pentadbir, container, false);

        tarikhJadual1 = v.findViewById(R.id.tarikhJadual);
        idJadual1 = v.findViewById(R.id.idJadual);
        Button butangCariTarikh1 = v.findViewById(R.id.butangCariTarikh);
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

        butangCariTarikh1.setOnClickListener(v1 -> tunjukJadual());

        butangTambahSenaraiTugas1.setOnClickListener(v12 -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.tambah_kemaskini_jadual);

            EditText tugasJadual1 = dialog.findViewById(R.id.tugasJadual);
            Button butangTambah = dialog.findViewById(R.id.butangTambah);

            butangTambah.setOnClickListener(v121 -> {
                String tugasJadual = tugasJadual1.getText().toString().trim();

                if (tugasJadual.isEmpty())
                {
                    tugasJadual1.setError("Tugasan perlu diisi!");
                    tugasJadual1.requestFocus();
                }
                else
                {
                    list.add(new Tugas(tugasJadual));
                    penyambung.notifyItemInserted(list.size() - 1);

                    recylerview.scrollToPosition(list.size() - 1);
                    dialog.dismiss();
                }
            });
            dialog.show();
        });

        butangBuangTugas1.setOnClickListener(v13 -> {
            String idJadual = idJadual1.getText().toString().trim();

            AlertDialog dialog = new AlertDialog.Builder(requireContext())
                    .setTitle("Adakah anda pasti?")
                    .setMessage("Tindakan membuang jadual ini akan membuatkan jadual dikeluarkan dari sistem sepenuhnya")
                    .setPositiveButton("Buang", (dialog1, which) -> {
                        progressBar.setVisibility(View.VISIBLE);

                        db.collection("JadualTugasan").document(idJadual)
                                .delete()
                                        .addOnSuccessListener(unused -> {
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(getContext(), "Sistem berjaya membuang jadual!", Toast.LENGTH_LONG).show();
                                            startActivity(new Intent(getContext(), requireActivity().getClass()));
                                        });
                    })
                    .setNegativeButton("Tidak", (dialog12, which) -> dialog12.dismiss())
                    .create();
            dialog.show();
        });

        butangKemaskiniTugas1.setOnClickListener(v14 -> {
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
            }
            else
            {
                if (list.isEmpty())
                {
                    Toast.makeText(getContext(), "Senarai tugas perlu dimasukkan!", Toast.LENGTH_LONG).show();
                }
                else {
                    progressBar.setVisibility(View.VISIBLE);

                    db.collection("JadualTugasan").document(idJadual)
                            .update(tugasan).addOnSuccessListener(unused -> {
                                progressBar.setVisibility(View.GONE);

                                Toast.makeText(getContext(), "Sistem berjaya mengemaskini jadual!", Toast.LENGTH_LONG).show();

                                startActivity(new Intent(getContext(), requireActivity().getClass()));
                            })
                            .addOnFailureListener(e -> {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Sistem gagal mengemaskini jadual! Sila cuba lagi", Toast.LENGTH_LONG).show();
                            });
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
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot snapshot : snapshotList) {
                            if (Objects.equals(snapshot.getString("tarikhJadual"), tarikh)) {
                                tarikhJadual1.setText(tarikh);
                                String idJadual = snapshot.getString("idJadual");

                                idJadual1.setText(idJadual);
                                butangTambahSenaraiTugas1.setEnabled(true);
                                butangBuangTugas1.setEnabled(true);
                                butangKemaskiniTugas1.setEnabled(true);
                                butangTambahSenaraiTugas1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));
                                butangKemaskiniTugas1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));
                                butangBuangTugas1.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.tema));

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
                        }
                    }
                    else
                    {
                        idJadual1.getText().clear();

                        Toast.makeText(getContext(), "Tarikh tidak mempunyai sebarang tugas!", Toast.LENGTH_LONG).show();
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
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Map<String, Object> friendsMap = document.getData();
                            assert friendsMap != null;
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
                });
    }
}