// Used to register new schedule

package com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.daftarTugasan;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sistempengurusanjabatanjuruteknik.R;
import com.sistempengurusanjabatanjuruteknik.databinding.FragmentDaftarTugasanPentadbirBinding;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.Tugas;
import com.sistempengurusanjabatanjuruteknik.ui.pentadbir.jadualTugasan.penyambungJadual;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class daftarTugasan extends Fragment implements DatePickerDialog.OnDateSetListener
{
    private FragmentDaftarTugasanPentadbirBinding binding;
    private FirebaseFirestore db; // declare Firebase Firestore variable
    private int max;
    private final ArrayList<Tugas> list = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDaftarTugasanPentadbirBinding.inflate(inflater, container, false);

        db = FirebaseFirestore.getInstance();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        penyambungJadual penyambung = new penyambungJadual(getContext(), list);
        binding.recyclerView.setAdapter(penyambung);

        db.collection("JadualTugasan").get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful())
                            {
                                for (DocumentSnapshot ignored : task.getResult())
                                {
                                    max++;
                                }
                                max = max + 1;

                                if (max < 10) {
                                    binding.idJadual.setText("J00" + max);
                                } else if (max < 100) {
                                    binding.idJadual.setText("J0" + max);
                                } else {
                                    binding.idJadual.setText("J" + max);
                                }
                            }
                            else
                            {
                                max = 1;
                                binding.idJadual.setText("J00" + max);
                            }
                        });

        binding.butangCariTarikh.setOnClickListener(v1 -> tunjukJadual());

        binding.butangTambahSenaraiTugas.setOnClickListener(v12 -> {
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

                    binding.recyclerView.scrollToPosition(list.size() - 1);
                    dialog.dismiss();
                }
            });
            dialog.show();
        });

        binding.butangTambahTugas.setOnClickListener(v13 -> {
            String tarikhJadual = binding.tarikhJadual.getText().toString().trim();
            String idJadual = binding.idJadual.getText().toString().trim();

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
                    binding.progressBar.setVisibility(View.VISIBLE);

                    db.collection("JadualTugasan").document(idJadual)
                            .set(tugasan).addOnSuccessListener(unused -> {
                                binding.progressBar.setVisibility(View.GONE);

                                Toast.makeText(getContext(), "Sistem berjaya menambah jadual!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getContext(), requireActivity().getClass()));
                            })
                            .addOnFailureListener(e -> {
                                binding.progressBar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Sistem gagal menambah jadual! Sila cuba lagi", Toast.LENGTH_LONG).show();
                            });
                }
            }
        });

        return binding.getRoot();
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String tarikh;
        month = month + 1;

        if (dayOfMonth < 10 && month < 10)
        {
            tarikh = "0" + dayOfMonth + "/0" + month + "/" + year;
        }
        else if (month >= 10 && dayOfMonth < 10)
        {
            tarikh = "0" + dayOfMonth + "/" + month + "/" + year;
        }
        else if (month < 10)
        {
            tarikh = "" + dayOfMonth + "/0" + month + "/" + year;
        }
        else
        {
            tarikh = "" + dayOfMonth + "/" + month + "/" + year;
        }

        db = FirebaseFirestore.getInstance();
        String finalTarikh = tarikh;
        db.collection("JadualTugasan")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> snapshotList = queryDocumentSnapshots.getDocuments();
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                        penyambungJadual penyambung = new penyambungJadual(getContext(), list);
                        binding.recyclerView.setAdapter(penyambung);

                        for (DocumentSnapshot snapshot : snapshotList) {
                            if (Objects.equals(snapshot.getString("tarikhJadual"), finalTarikh)) {
                                binding.tarikhJadual.getText().clear();
                                list.clear();
                                penyambung.notifyDataSetChanged();

                                Toast.makeText(getContext(), "Tarikh sudah mempunyai tugasan! Sila pilih tarikh yang lain.", Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                binding.tarikhJadual.setText(finalTarikh);
                            }
                        }
                    }
                    else
                    {
                        binding.tarikhJadual.setText(finalTarikh);
                    }
                });

    }
}
