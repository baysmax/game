package com.baymax.game;

import android.os.Bundle;
import android.view.View;


import com.baymax.game.view.LifeGameView;

import androidx.appcompat.app.AppCompatActivity;

public class LifeGameActivity extends AppCompatActivity {
    LifeGameView mHab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_life_game);
        mHab=findViewById(R.id.mHab);
        setTitle("生命游戏");
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnNext:
                mHab.next();
                break;
            case R.id.btnStart:
                mHab.start();
                break;
            case R.id.btnPause:
                mHab.setPause(true);
                break;
        }
    }
}
