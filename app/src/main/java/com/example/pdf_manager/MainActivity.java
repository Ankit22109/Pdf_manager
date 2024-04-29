package com.example.pdf_manager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri = getIntent().getData();
        if(uri != null){
            Intent send = new Intent(getApplicationContext(), OpenFile.class);
            send.putExtra("Uri", uri.toString());
            startActivity(send);
        }else{

        }



        FloatingActionButton create_btn = findViewById(R.id.create);
        FloatingActionButton edit_btn = findViewById(R.id.edit);

        makePermissionRequest();

        edit_btn.setOnClickListener(v -> {
            FileSelector();

        });

        create_btn.setOnClickListener(v -> {
            CreateFile();
        });


    }

    private void makePermissionRequest() {
        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }


    private void FileSelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
//        intent.hasFileDescriptors();
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, 101);
        } catch (Exception e) {
            Toast.makeText(this, "Please install file manager", Toast.LENGTH_SHORT).show();
        }
    }

    private final int Pdf_page_width = 595; // 8.26 Inch
    private final int Pdf_page_height = 842;

    @Override
    protected void onActivityResult(int resultCode, int requestCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == 101 && requestCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();

            assert uri != null;


            Intent send = new Intent(getApplicationContext(), OpenFile.class);
            send.putExtra("Uri", uri.toString());

            startActivityForResult(send, 102);

        }

        if (resultCode == 103 && requestCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            assert uri != null;
            PdfDocument pdf_doc = new PdfDocument();

            try {
                OutputStream out = getContentResolver().openOutputStream(uri);
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(Pdf_page_width, Pdf_page_height, 1).create();


                PdfDocument.Page page = pdf_doc.startPage(pageInfo);


                pdf_doc.finishPage(page);
                pdf_doc.writeTo(out);
                pdf_doc.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        }

    }

    public void CreateFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");

        intent.addCategory(Intent.CATEGORY_OPENABLE);


        try {
            startActivityForResult(intent, 103);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();

        }
    }

}