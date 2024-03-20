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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.pdf.PdfDocument;
import android.graphics.pdf.PdfDocument.*;

import com.example.pdf_manager.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FloatingActionButton create_btn = findViewById(R.id.create);
        FloatingActionButton edit_btn = findViewById(R.id.edit);

        edit_btn.setOnClickListener(v ->{
            FileSelector();

        });

        create_btn.setOnClickListener(v ->{
            CreateFile();
        });


    }

    private void makePermissonRequest(){
        if(checkPermission()){
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }else{
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
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, 101);
        } catch (Exception e) {
            Toast.makeText(this, "Please install file manager", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult ( int resultCode, int requestCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == 101 && requestCode == Activity.RESULT_OK && data != null) {

            Uri uri = data.getData();
//            String path = uri.getPath();
//            File file = new File(path);

            Intent send = new Intent(getApplicationContext(), OpenFile.class);
            send.putExtra("Uri", uri.toString());

            startActivityForResult(send, 102);


            }
        if (resultCode == 103 && requestCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = uri.getPath();
            File file = new File(path);
            Paint title = new Paint();
            // create a new document
            PdfDocument document = new PdfDocument();

            // create a page description
            PageInfo pageInfo = new PageInfo.Builder(100, 100, 1).create();

            title.setTextSize(18);
            title.setColor(ContextCompat.getColor(this, R.color.black));
            title.setTextAlign(Paint.Align.CENTER);

            // start a page
            Page page = document.startPage(pageInfo);
            Canvas canvas = page.getCanvas();

            canvas.drawText("This is sample document which we have created.", 396, 560, title);
            // draw something on the page
//            View content = getContentView();
//            content.draw(page.getCanvas());

            // finish the page
            document.finishPage(page);

            // write the document content
            try {
                document.writeTo(new FileOutputStream(file));
            } catch (IOException e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                throw new RuntimeException(e);
            }

            // close the document
            document.close();

        }

    }

    public void CreateFile(){
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