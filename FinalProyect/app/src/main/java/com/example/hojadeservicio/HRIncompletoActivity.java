package com.example.hojadeservicio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class HRIncompletoActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String TAG = "HRIncompletoActivity";
    private static final int SIGNATURE_REQUEST_CODE = 1;

    private EditText eFecha, eHora, eEncargado;
    private Button bFecha, bHora, btnCrearPdf, btnFirma;
    private int dia, mes, ano, hora, minutos;

    private String firmaBase64;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hr_incompleto);

        eFecha = findViewById(R.id.eFecha);
        eHora = findViewById(R.id.eHora);
        eEncargado = findViewById(R.id.eEncargado);
        bFecha = findViewById(R.id.bFecha);
        bHora = findViewById(R.id.bHora);
        btnFirma = findViewById(R.id.btnFirma);
        btnCrearPdf = findViewById(R.id.btnCrearPdf);

        bFecha.setOnClickListener(this::onClick);
        bHora.setOnClickListener(this::onClick);

        btnFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HRIncompletoActivity.this, FirmaActivity.class);
                startActivityForResult(intent, SIGNATURE_REQUEST_CODE);
            }
        });

        btnCrearPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermission()) {
                    crearPDF(v);
                } else {
                    requestPermission();
                }
            }
        });
    }

    public void onClick(View v) {
        if (v == bFecha) {
            final Calendar c = Calendar.getInstance();
            dia = c.get(Calendar.DAY_OF_MONTH);
            mes = c.get(Calendar.MONTH);
            ano = c.get(Calendar.YEAR);
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    eFecha.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                }
            }, ano, mes, dia);
            datePickerDialog.show();
        } else if (v == bHora) {
            final Calendar c = Calendar.getInstance();
            hora = c.get(Calendar.HOUR_OF_DAY);
            minutos = c.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    eHora.setText(hourOfDay + ":" + minute);
                }
            }, hora, minutos, true);
            timePickerDialog.show();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else {
            int writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                    crearPDF(null);
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                    crearPDF(null);
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGNATURE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                firmaBase64 = data.getStringExtra("firma");
            }
        } else if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    Toast.makeText(this, "Permiso concedido", Toast.LENGTH_SHORT).show();
                    crearPDF(null);
                } else {
                    Toast.makeText(this, "Permiso denegado", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void crearPDF(View view) {
        String fecha = eFecha.getText().toString();
        String hora = eHora.getText().toString();
        String encargado = eEncargado.getText().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = sdf.format(new Date());
        String fileName = "HR_INCOMPLETO_" + currentDateTime + ".pdf";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Fuente
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            document.add(new Paragraph("Se le comunica que el personal del área de Informatización atendió la solicitud de revisión que Usted requirió, pero no se encontró a nadie en el lugar; por lo cual se le solicita llamar a la extensión 3270 para atender su reporte nuevamente.").setFont(regularFont));


            // Tabla con fecha, hora y encargado
            float[] columnWidths = {1, 1, 1};
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            table.addCell(new Cell().add(new Paragraph("Fecha:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph("Hora:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph("Encargado:").setFont(boldFont)));

            table.addCell(new Cell().add(new Paragraph(fecha).setFont(regularFont)));
            table.addCell(new Cell().add(new Paragraph(hora).setFont(regularFont)));
            table.addCell(new Cell().add(new Paragraph(encargado).setFont(regularFont)));

            document.add(table);

            // Espacio
            document.add(new Paragraph("\n\n"));

            // Firma
            if (firmaBase64 != null) {
                byte[] decodedString = Base64.decode(firmaBase64, Base64.DEFAULT);
                Bitmap firmaBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Image firmaImage = new Image(ImageDataFactory.create(getBytesFromBitmap(firmaBitmap)));
                firmaImage.scaleToFit(200, 100); // Escalar la firma a un tamaño más pequeño
                document.add(new Paragraph("Firma:").setFont(boldFont));
                document.add(firmaImage);
            }

            document.close();

            Toast.makeText(this, "PDF creado correctamente en " + filePath, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al crear el PDF: " + e.getMessage());
            Toast.makeText(this, "Error al crear el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Error al crear el PDF: " + e.getMessage());
            Toast.makeText(this, "Error al crear el PDF: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
}


