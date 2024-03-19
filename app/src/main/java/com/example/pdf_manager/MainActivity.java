package com.example.pdf_manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;

import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.pdf.PdfDocument;

import com.example.pdf_manager.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

//    private static ActivityMainBinding onCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "Please install file manager", Toast.LENGTH_SHORT).show();

        FloatingActionButton create_btn = findViewById(R.id.create);
        FloatingActionButton edit_btn = findViewById(R.id.edit);

        edit_btn.setOnClickListener(v ->{
            FileSelector();
        });

        create_btn.setOnClickListener(v ->{
            CreateFile();
        });




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
            String path = uri.getPath();
            File file = new File(path);
            Intent send = new Intent(MainActivity.this, OpenFile.class);
            send.putExtra("Uri", uri);
            send.putExtra("file", file.toString());
            send.putExtra("path", path);
            startActivityForResult(send, 102);
            }
        }

    public void CreateFile(){
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.setType("application/pdf");
        PdfDocument Pdf_doc = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(100,
                100, 1).create();
        PdfDocument.Page page = Pdf_doc.startPage(pageInfo);

        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(intent, 103);
        } catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
        }
    }





}