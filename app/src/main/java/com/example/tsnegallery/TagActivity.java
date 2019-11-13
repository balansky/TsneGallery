package com.example.tsnegallery;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.example.tsnegallery.adapters.TagResultAdapter;
import com.example.tsnegallery.tflite.ObjectDetectionModel;

import java.io.IOException;
import java.util.List;

public class TagActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private TagResultAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageHolder;
    private ObjectDetectionModel detector;
    private Handler handler;
    private HandlerThread handlerThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerResults);
//        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new TagResultAdapter(getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        imageHolder = findViewById(R.id.tagImageView);

    }

    @Override
    protected synchronized void onResume() {
        super.onResume();

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public synchronized void onPause() {

        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
        }

        super.onPause();
    }

    protected synchronized void runInBackground(final Runnable r) {
        if (handler != null) {
            handler.post(r);
        }
    }

    public void chooseImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);

    }

    public void resetImage(View view){

        ImageView tview = findViewById(R.id.tagImageView);
        tview.setImageResource(R.color.colorPrimaryDark);
        mAdapter.removeAll();

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
//                final List<Classifier.Recognition> results = detector.recognizeImage(croppedBitmap);
                mAdapter.removeAll();
                for(int i = 0; i < 3; i++){
                    mAdapter.addItem("" + i);
                }

                ImageView imageView = findViewById(R.id.tagImageView);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
