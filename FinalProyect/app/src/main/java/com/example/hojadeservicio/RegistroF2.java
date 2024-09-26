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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistroF2 extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final int SIGNATURE_REQUEST_CODE = 1;
    private static final String TAG = "RegistroF2";
    private String firmaBase64;

    Button btnCrearPdf, buttonAddObservation, buttonNoObservations, btnFirma;
    EditText editTextProblematica;
    EditText editTextSolucion;
    LinearLayout layoutObservations;
    List<EditText> observationEditTexts = new ArrayList<>();

    private String fecha, hora, fechaAtencion, horaSalida, facultad, edificio, encargadoAr, telefono, campus, dependencia, area, atendio, calidadServicio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_f2);

        btnCrearPdf = findViewById(R.id.btnCrearPdf);
        buttonAddObservation = findViewById(R.id.buttonAddObservation);
        buttonNoObservations = findViewById(R.id.buttonNoObservations);
        editTextProblematica = findViewById(R.id.editTextProblematica);
        editTextSolucion = findViewById(R.id.editTextSolucion);
        layoutObservations = findViewById(R.id.layoutObservations);
        btnFirma = findViewById(R.id.btnFirma);

        Intent intent = getIntent();
        fecha = intent.getStringExtra("fecha");
        hora = intent.getStringExtra("hora");
        fechaAtencion = intent.getStringExtra("fechaAtencion");
        horaSalida = intent.getStringExtra("horaSalida");
        facultad = intent.getStringExtra("facultad");
        edificio = intent.getStringExtra("edificio");
        encargadoAr = intent.getStringExtra("encargadoAr");
        telefono = intent.getStringExtra("telefono");
        campus = intent.getStringExtra("campus");
        dependencia = intent.getStringExtra("dependencia");
        area = intent.getStringExtra("area");
        atendio = intent.getStringExtra("atendio");
        calidadServicio = intent.getStringExtra("calidadServicio");

        buttonAddObservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addObservationField();
            }
        });

        buttonNoObservations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearObservations();
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

        btnFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistroF2.this, FirmaActivity.class);
                startActivityForResult(intent, SIGNATURE_REQUEST_CODE);
            }
        });
    }

    private void addObservationField() {
        EditText editText = new EditText(this);
        editText.setHint("Observación");
        layoutObservations.addView(editText);
        observationEditTexts.add(editText);
    }

    private void clearObservations() {
        layoutObservations.removeAllViews();
        observationEditTexts.clear();
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
        }
    }

    public void crearPDF(View view) {
        String problematica = editTextProblematica.getText().toString();
        String solucion = editTextSolucion.getText().toString();

        List<String> observaciones = new ArrayList<>();
        for (EditText editText : observationEditTexts) {
            String obs = editText.getText().toString();
            if (!obs.isEmpty()) {
                observaciones.add(obs);
            }
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateTime = sdf.format(new Date());
        String fileName = "HojaDeServicio_" + currentDateTime + ".pdf";
        String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName;

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            // Fuente
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Logo
            InputStream logoInputStream = getResources().openRawResource(R.drawable.uaq);  // Cambia 'R.drawable.uaq' al ID correcto de tu recurso de imagen
            byte[] logoBytes = getBytesFromInputStream(logoInputStream);
            ImageData imageData = ImageDataFactory.create(logoBytes);
            Image logo = new Image(imageData).scaleToFit(80, 80);

            // Título y logo
            Table headerTable = new Table(2);
            headerTable.setWidth(UnitValue.createPercentValue(100));
            headerTable.addCell(new Cell().add(logo).setBorder(null).setTextAlignment(TextAlignment.LEFT));
            headerTable.addCell(new Cell().add(new Paragraph("UNIVERSIDAD AUTÓNOMA DE QUERÉTARO\nCOORDINACIÓN GENERAL DE SERVICIOS DE INFORMATIZACIÓN\n\nHOJA DE SERVICIO")
                    .setFont(boldFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)).setBorder(null));
            document.add(headerTable);


            // Tabla de recepción del servicio
            float[] columnWidths = {1, 2, 1, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

            table.addCell(new Cell(1, 4).add(new Paragraph("RECEPCIÓN DEL SERVICIO:").setFont(boldFont)));

            table.addCell(new Cell().add(new Paragraph("Fecha de solicitud:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(fecha).setFont(regularFont)));  // Datos de fecha de solicitud
            table.addCell(new Cell().add(new Paragraph("Fecha atención:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(fechaAtencion).setFont(regularFont)));  // Datos de fecha de atención

            table.addCell(new Cell().add(new Paragraph("Hora de llegada:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(hora).setFont(regularFont)));  // Datos de hora de llegada
            table.addCell(new Cell().add(new Paragraph("Hora de salida:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(horaSalida).setFont(regularFont)));

            table.addCell(new Cell().add(new Paragraph("Campus:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(campus).setFont(regularFont)));  // Datos de campus
            table.addCell(new Cell().add(new Paragraph("Dependencia:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(dependencia).setFont(regularFont)));  // Datos de dependencia

            table.addCell(new Cell().add(new Paragraph("Edificio:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(edificio).setFont(regularFont)));  // Datos de edificio
            table.addCell(new Cell().add(new Paragraph("Área:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(area).setFont(regularFont)));  // Datos del área

            table.addCell(new Cell().add(new Paragraph("Encargado de área:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(encargadoAr).setFont(regularFont)));  // Datos del encargado de área
            table.addCell(new Cell().add(new Paragraph("Teléfono:").setFont(boldFont)));
            table.addCell(new Cell().add(new Paragraph(telefono).setFont(regularFont)));  // Datos de teléfono

            table.addCell(new Cell().add(new Paragraph("Atendió:").setFont(boldFont)));
            table.addCell(new Cell(1, 3).add(new Paragraph(atendio).setFont(regularFont)));  // Datos de quien atendió

            document.add(table);

            // Problemática y Solución
            Table problematicaSolucionTable = new Table(new float[]{1, 1});
            problematicaSolucionTable.setWidth(UnitValue.createPercentValue(100));

            Cell problematicaCell = new Cell().add(new Paragraph("Problemática:").setFont(boldFont))
                    .add(new Paragraph(problematica).setFont(regularFont))
                    .setHeight(100);
            problematicaSolucionTable.addCell(problematicaCell);

            Cell solucionCell = new Cell().add(new Paragraph("Solución:").setFont(boldFont))
                    .add(new Paragraph(solucion).setFont(regularFont))
                    .setHeight(100);
            problematicaSolucionTable.addCell(solucionCell);

            document.add(problematicaSolucionTable);

            // Observaciones
            if (!observaciones.isEmpty()) {
                Table observacionesTable = new Table(new float[]{1});
                observacionesTable.setWidth(UnitValue.createPercentValue(100));
                Cell observacionesCell = new Cell().add(new Paragraph("Observaciones:").setFont(boldFont));
                for (String obs : observaciones) {
                    observacionesCell.add(new Paragraph(obs).setFont(regularFont));
                }
                observacionesTable.addCell(observacionesCell);
                document.add(observacionesTable);
            }

            // Calificación del servicio
            Table ratingTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
            ratingTable.setWidth(UnitValue.createPercentValue(100));
            ratingTable.addCell(new Cell().add(new Paragraph("Bueno").setFont(regularFont)).setTextAlignment(TextAlignment.CENTER));
            ratingTable.addCell(new Cell().add(new Paragraph("Malo").setFont(regularFont)).setTextAlignment(TextAlignment.CENTER));
            ratingTable.addCell(new Cell().add(new Paragraph("Regular").setFont(regularFont)).setTextAlignment(TextAlignment.CENTER));

            // Marcar la opción seleccionada
            switch (calidadServicio) {
                case "Bueno":
                    ratingTable.getCell(0, 0).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                    break;
                case "Malo":
                    ratingTable.getCell(0, 1).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                    break;
                case "Regular":
                    ratingTable.getCell(0, 2).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY);
                    break;
            }

            document.add(new Paragraph("\nCALIFICAR CALIDAD DEL SERVICIO RECIBIDO:").setFont(boldFont));
            document.add(ratingTable);

            // Añadir la firma al PDF
            if (firmaBase64 != null) {
                byte[] decodedString = Base64.decode(firmaBase64, Base64.DEFAULT);
                Bitmap firmaBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                Image firmaImage = new Image(ImageDataFactory.create(getBytesFromBitmap(firmaBitmap)));
                firmaImage.scaleToFit(300, 250);  // Ajusta el tamaño de la firma (ancho: 150, alto: 50)
                document.add(new Paragraph("\nFirma:").setFont(boldFont));
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

    private byte[] getBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}



