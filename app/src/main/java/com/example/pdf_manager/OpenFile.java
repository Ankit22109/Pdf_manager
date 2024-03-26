package com.example.pdf_manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;

class OnSwipeTouchListener implements View.OnTouchListener {
    private final GestureDetector gestureDetector;

    public OnSwipeTouchListener(Context context) {
        gestureDetector = new GestureDetector(context, new GestureListener());
    }



    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;


        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();
            if (Math.abs(diffX) > Math.abs(diffY) &&
                    Math.abs(diffX) > SWIPE_THRESHOLD &&
                    Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    try {
                        onSwipeRight();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    try {
                        onSwipeLeft();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            }
            return false;
        }
    }

    public void onSwipeRight() throws IOException {
        // Override this method to handle right swipe
    }

    public void onSwipeLeft() throws IOException {
        // Override this method to handle left swipe
    }
}


public class OpenFile extends AppCompatActivity {



    private static int page_index = 0;
    private static int page_count;
    public static ImageView ImgView;
    @SuppressLint("ClickableViewAccessibility")
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
        try {
            Open(uri, page_index = 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ImgView.setOnTouchListener(new OnSwipeTouchListener(this){

            @Override
            public void onSwipeRight() throws IOException {
                if(page_index == 0){
                    return;
                }

                page_index -= 1;
                Open(uri, page_index);

            }

            @Override
            public void onSwipeLeft() throws IOException {
                if(page_count-1 == page_index){
                    return;
                }
                page_index += 1;
                Open(uri, page_index);
            }
        });



    }


    private  void Open(Uri uri, int page_index) throws IOException {

//
        try {
            ImgView = findViewById(R.id.ImgView);
//            PdfRenderer render = Renderer(uri);
            PdfRenderer.Page page = page(page_index, uri);
            Bitmap bitmap = convert_to_img(page);
//            bitmap.
            ImgView.setImageBitmap(bitmap);
            Stop(uri);
        }
        catch(Exception e){
            Log.e("Error:", String.valueOf(e.getMessage()));
            e.getCause();
        }

    }

    private PdfRenderer Renderer(Uri uri) throws Exception{
        ParcelFileDescriptor descriptor = getContentResolver().openFileDescriptor(uri, "r");
        assert descriptor != null;
        PdfRenderer render = new PdfRenderer(descriptor);
        page_count = render.getPageCount();
        return render;
    }

    private PdfRenderer.Page page(int page_index, Uri uri) throws Exception {
        PdfRenderer render = Renderer(uri);
        PdfRenderer.Page page = render.openPage(page_index);
        return page;
    }

    private Bitmap convert_to_img(PdfRenderer.Page page){
        Bitmap bitmap = Bitmap.createBitmap(720, 1424, Bitmap.Config.ARGB_8888);
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        return bitmap;
    }

    private void Stop(Uri uri) throws Exception {
        PdfRenderer.Page page = page(page_index, uri);
        PdfRenderer render = Renderer(uri);
        Bitmap bitmap = convert_to_img(page);
        bitmap.recycle();
        render.close();
        page.close();
    }

     @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(Integer.parseInt(android.os.Build.VERSION.SDK) < 5
        && keyCode == KeyEvent.KEYCODE_BACK
        && event.getRepeatCount() == 0){
//            Log.d("CDA", "onKeyDown Called");
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