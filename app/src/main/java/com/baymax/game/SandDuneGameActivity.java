package com.baymax.game;

import android.os.Bundle;
import android.view.View;


import com.baymax.game.view.SandDuneGameView;

import androidx.appcompat.app.AppCompatActivity;

public class SandDuneGameActivity extends AppCompatActivity {
    SandDuneGameView mSandDuneGameView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sand_dune);
        mSandDuneGameView=findViewById(R.id.mSandDuneGameView);
        setTitle("沙堆模型");
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnStart:
                mSandDuneGameView.start();
                break;
            case R.id.btnPause:
                mSandDuneGameView.setPause(true);
                break;
            case R.id.btnRandom:
                mSandDuneGameView.setRandom();
                break;
        }

    }
}
