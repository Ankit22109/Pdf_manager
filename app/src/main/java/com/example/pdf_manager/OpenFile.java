package com.example.pdf_manager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.widget.ImageView;

import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileNotFoundException;

import java.io.IOException;

public class OpenFile extends AppCompatActivity {
    private static final int page_index = 1;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_open_file);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();

        Uri uri = Uri.parse(intent.getStringExtra("Uri"));
        String path = uri.getPath();
        File file = new File(path);
//        File destination = new File(file.getPath());
        try {
            Open(file);
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }

    }


    private  void Open(File file) throws IOException {
//        Toast.makeText(this, file.toString(), Toast.LENGTH_LONG).show();
        ImageView ImgView = findViewById(R.id.ImgView);
        try {
            ParcelFileDescriptor FD = new ParcelFileDescriptor(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));
            PdfRenderer render = new PdfRenderer(FD);

            PdfRenderer.Page page  = render.openPage(page_index);


            Bitmap bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_4444);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            ImgView.setImageBitmap(bitmap);
            page.close();
            render.close();

        }catch(FileNotFoundException e){
            Toast.makeText(this, e + file.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

//        page.close();
//        render.close();
    }


}