package com.example.pdf_manager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.pdf_manager.databinding.ActivityOpenFileBinding;
import com.github.barteksc.pdfviewer.PDFView;
import android.view.KeyEvent;

public class OpenFile extends AppCompatActivity {


    private static @NonNull ActivityOpenFileBinding binding;
    private static int page_index = 0;
    public static PDFView pdfView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityOpenFileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View view = binding.getRoot();
        setContentView(view);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        Intent intent = getIntent();

        Uri uri = Uri.parse(intent.getStringExtra("Uri"));
        assert uri != null;
        binding.pdfView.fromUri(uri).load();



    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(Integer.parseInt(android.os.Build.VERSION.SDK) < 5
        && keyCode == KeyEvent.KEYCODE_BACK
        && event.getRepeatCount() == 0){
            onBackPressed();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


}