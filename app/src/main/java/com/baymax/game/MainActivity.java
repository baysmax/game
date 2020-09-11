package com.baymax.game;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Created by  Baymax on 2020/9/10.
 * Copyright © 2020/9/10 Hyperspace Technology(Beijing)Co.,Ltd. All rights reserved.
 * @e-mail 761241917@qq.com
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLifeGame:
                startActivity(new Intent(this,LifeGameActivity.class));
                break;
            case R.id.btnSandDune:
                startActivity(new Intent(this,SandDuneGameActivity.class));
                break;
        }
    }
}
