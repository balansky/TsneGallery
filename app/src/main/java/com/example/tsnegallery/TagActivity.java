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
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.example.tsnegallery.adapters.TagResultAdapter;
import com.example.tsnegallery.tflite.ObjectDetectionModel;
import android.util.Log;
import android.widget.TextView;


import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;


public class TagActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TF_OD_API_MODEL_FILE = "detect.tflite";
    private static final String TF_OD_API_LABELS_FILE = "file:///android_asset/labelmap.txt";
    private static final int TF_OD_API_INPUT_SIZE = 300;
    private static final boolean TF_OD_API_IS_QUANTIZED = true;
    private int PICK_IMAGE_REQUEST = 1;
    private RecyclerView recyclerView;
    private TagResultAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageHolder;
    private TextView inferenceTime;
    private ObjectDetectionModel detector;
    private Handler handler;
    private HandlerThread handlerThread;


    @Override
    protected synchronized void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        recyclerView = findViewById(R.id.recyclerResults);
//        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new TagResultAdapter(getApplicationContext());
        recyclerView.setAdapter(mAdapter);

        imageHolder = findViewById(R.id.tagImageView);
        inferenceTime = findViewById(R.id.inferenceTime);

        try{

            detector = new ObjectDetectionModel(getAssets(), TF_OD_API_MODEL_FILE,
                    TF_OD_API_LABELS_FILE, TF_OD_API_INPUT_SIZE,  TF_OD_API_IS_QUANTIZED);
        }catch(final IOException e){
            Log.e(TAG, e.toString());
            finish();
        }

        handlerThread = new HandlerThread("inference");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

    }

    @Override
    protected synchronized void onResume() {
        super.onResume();
    }

    @Override
    public synchronized void onDestroy() {

        handlerThread.quitSafely();

        try {
            handlerThread.join();
            handlerThread = null;
            handler = null;
        } catch (final InterruptedException e) {
            Log.e(TAG, e.toString());
        }

        super.onDestroy();
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
        inferenceTime.setText("0 ms");

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                imageHolder.setImageBitmap(bitmap);

                runInBackground(
                        new Runnable() {
                            @Override
                            public void run() {
                                final long startTime = SystemClock.uptimeMillis();
                                List<ObjectDetectionModel.Recognition> results =
                                        detector.recognizeImage(bitmap);
                                long lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.updateResults(results);
                                                inferenceTime.setText(lastProcessingTimeMs + " ms");
                                            }
                                        }
                                );
                            }
                        }
                );

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
