package com.baymax.game;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;


import com.baymax.game.view.SandDuneGameView;

import androidx.appcompat.app.AppCompatActivity;

public class SandDuneGameActivity extends AppCompatActivity {
    SandDuneGameView mSandDuneGameView;
    SeekBar seekBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sand_dune);
        setTitle("沙堆模型");
        mSandDuneGameView=findViewById(R.id.mSandDuneGameView);
        seekBar=findViewById(R.id.seekBar);
        seekBar.setProgress(mSandDuneGameView.getTime_in_frame());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i("seekBar","seekBar progress="+progress);
                mSandDuneGameView.setTime_in_frame(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
            case R.id.btnChange:
                mSandDuneGameView.setIsDrawChange(!mSandDuneGameView.isDrawChange());
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSandDuneGameView!=null){
            mSandDuneGameView.close();
        }

    }
}
