package com.example.hojadeservicio;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class RegistroF1 extends AppCompatActivity {

    String[] items = {"Facultad de Informatica", "Facultad de Derecho", "Facultad de Psicologia", "Facultad de Ciencias Naturales"};
    AutoCompleteTextView autoCompleteTxtFacultad;
    ArrayAdapter<String> adapterItems;
    Button btnNext1, bfecha, bhora, bfechaAtencion, bhoraSalida;
    EditText efecha, ehora, eFechaAtencion, eHoraSalida, Edificio, EncargadoAr, Telefono, Campus, Dependencia, Area, Atendio;
    private int dia, mes, ano, hora, minutos;
    RadioGroup radioGroupCalidad;
    String calidadServicio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_f1);

        autoCompleteTxtFacultad = findViewById(R.id.auto_complete_txt_facultad);
        adapterItems = new ArrayAdapter<>(this, R.layout.list_item, items);
        autoCompleteTxtFacultad.setAdapter(adapterItems);

        autoCompleteTxtFacultad.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = String.valueOf(parent.getItemAtPosition(position));
                Toast.makeText(getApplicationContext(), "Facultad " + item, Toast.LENGTH_SHORT).show();
            }
        });

        bfecha = findViewById(R.id.bFecha);
        bhora = findViewById(R.id.bHora);
        bfechaAtencion = findViewById(R.id.bFechaAtencion);
        bhoraSalida = findViewById(R.id.bHoraSalida);
        efecha = findViewById(R.id.eFecha);
        ehora = findViewById(R.id.eHora);
        eFechaAtencion = findViewById(R.id.eFechaAtencion);
        eHoraSalida = findViewById(R.id.eHoraSalida);
        Edificio = findViewById(R.id.Edificio);
        EncargadoAr = findViewById(R.id.EncargadoAr);
        Telefono = findViewById(R.id.Telefono);
        Campus = findViewById(R.id.Campus);
        Dependencia = findViewById(R.id.Dependencia);
        Area = findViewById(R.id.Area);
        Atendio = findViewById(R.id.Atendio);
        radioGroupCalidad = findViewById(R.id.radioGroupCalidad);

        bfecha.setOnClickListener(this::onClick);
        bhora.setOnClickListener(this::onClick);
        bfechaAtencion.setOnClickListener(this::onClick);
        bhoraSalida.setOnClickListener(this::onClick);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String currentDateTime = dateFormat.format(calendar.getTime());


        btnNext1 = findViewById(R.id.btnNext1);

        radioGroupCalidad.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = findViewById(checkedId);
                calidadServicio = radioButton.getText().toString();
            }
        });

        btnNext1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fecha = efecha.getText().toString();
                String hora = ehora.getText().toString();
                String fechaAtencion = eFechaAtencion.getText().toString();
                String horaSalida = eHoraSalida.getText().toString();
                String facultad = autoCompleteTxtFacultad.getText().toString();
                String edificio = Edificio.getText().toString();
                String encargadoAr = EncargadoAr.getText().toString();
                String telefono = Telefono.getText().toString();
                String campus = Campus.getText().toString();
                String dependencia = Dependencia.getText().toString();
                String area = Area.getText().toString();
                String atendio = Atendio.getText().toString();

                Intent intent = new Intent(getApplicationContext(), RegistroF2.class);
                intent.putExtra("fecha", fecha);
                intent.putExtra("hora", hora);
                intent.putExtra("fechaAtencion", fechaAtencion);
                intent.putExtra("horaSalida", horaSalida);
                intent.putExtra("facultad", facultad);
                intent.putExtra("edificio", edificio);
                intent.putExtra("encargadoAr", encargadoAr);
                intent.putExtra("telefono", telefono);
                intent.putExtra("campus", campus);
                intent.putExtra("dependencia", dependencia);
                intent.putExtra("area", area);
                intent.putExtra("atendio", atendio);
                intent.putExtra("calidadServicio", calidadServicio);
                startActivity(intent);
            }
        });
    }

    public void onClick(View v) {
        if (v == bfecha || v == bfechaAtencion) {
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            ano = c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    if (v == bfecha) {
                        efecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    } else if (v == bfechaAtencion) {
                        eFechaAtencion.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                    }
                }
            }, ano, mes, dia); // Aquí se corrige el orden de los parámetros
            datePickerDialog.show();
        }
        if (v == bhora || v == bhoraSalida) {
            final Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY); // Usa HOUR_OF_DAY para formato 24 horas
            minutos = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    if (v == bhora) {
                        ehora.setText(hourOfDay + ":" + minute);
                    } else if (v == bhoraSalida) {
                        eHoraSalida.setText(hourOfDay + ":" + minute);
                    }
                }
            }, hora, minutos, true); // Usa formato 24 horas
            timePickerDialog.show();
        }
    }
}



