package com.example.pdf_manager;


import android.content.Intent;
import android.graphics.Bitmap;

import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.io.File;
import java.io.IOException;
import java.util.Objects;



public class OpenFile extends AppCompatActivity {
    private static final int page_index = 1;


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
//        String path = uri.getPath();
        File file = new File(Objects.requireNonNull(uri.getPath()));
//        File destination = new File(file.getPath());
        try {
            Open(file, uri);
        } catch (IOException e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            throw new RuntimeException(e);
        }

    }
    final static int height = 100;
    final static int width = 100;

    private  void Open(File file, Uri uri) throws IOException {

//
        try {
            ImageView ImgView = findViewById(R.id.ImgView);
           /* ParcelFileDescriptor FD = new ParcelFileDescriptor(ParcelFileDescriptor.open(file,
                    ParcelFileDescriptor.MODE_READ_ONLY)); */
            ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "r");

            PdfRenderer render = new PdfRenderer(descriptor);

            PdfRenderer.Page page = render.openPage(page_index);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            ImgView.setImageBitmap(bitmap);

            page.close();
            render.close();
        }
        catch(Exception e){
            Log.e("Error:", String.valueOf(e.getMessage()));
            e.printStackTrace();
        }



    }


}