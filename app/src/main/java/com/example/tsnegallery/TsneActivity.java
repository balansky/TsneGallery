package com.example.tsnegallery;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class TsneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tsne);
        TextView tv = findViewById(R.id.tsneTestView);
        tv.setText(stringFromJNI());
        float[] init_x = {1, 2, 3, 4, 5, 6};
        float[] init_y = {1, 2, 3, 4};

        initTsneJNI(init_x, init_y);

        float[] x  = {};
        float[] ret = runTsneJNI(2, 3, 0, x);

        double ll = 2.;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyTsneJNI();
    }

    public native String stringFromJNI();

    public native float[] runTsneJNI(int n, int dim, int init_from_y, float[] x);

    public native void initTsneJNI(float[] x, float[] y);

    public native void destroyTsneJNI();

    static{
        System.loadLibrary("tsne");
    }

}
