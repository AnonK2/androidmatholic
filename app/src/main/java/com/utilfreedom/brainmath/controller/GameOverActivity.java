package com.utilfreedom.brainmath.controller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.utilfreedom.brainmath.R;

// IGNORE THIS FILE!!!
public class GameOverActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog_game_over);

        // taking the device window size in pixels
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        // creating new size and set window size
        getWindow().setLayout((int) (width * 0.65), (int) (height * 0.4)); // 65% of device width and 40% of device height
    }
}
