package com.baymax.game.helper;

import java.util.Random;

/**
 * 默认每一代向中心点加沙子+1
 * @Created by  Baymax on 2020/9/10.
 * Copyright © 2020/9/10 Hyperspace Technology(Beijing)Co.,Ltd. All rights reserved.
 * @e-mail 761241917@qq.com
 */
public class SandDuneHelper {
    private int[][] sendDune;

    private int maxCount=4;

    private Random random;
    /**
     * 创建游戏
     * @param width
     * @param high
     */
    public  int[][] createGame(int width, int high) {
        sendDune =new int[width][high];
        random=new Random();

        return sendDune;
    }
    public void randomData(int[][] sendDune){
        if (sendDune==null)return;
        for (int i = 0; i < sendDune.length; i++) {
            for (int k = 0; k < sendDune[i].length; k++) {
                sendDune[i][k]=random.nextInt(maxCount);
            }
        }
    }
    /**
     * 计算下一代（模拟在点（sendDune.length/2，sendDune.length/2）处不停滴落沙子）
     * @return
     */
    public int[][] nextSandDune() {
        sendDune[ sendDune.length/2][ sendDune[0].length/2]=3;
        sendDune[ sendDune.length/2][ sendDune[0].length/2]++;

        if (sendDune[ sendDune.length/2][ sendDune[0].length/2]==maxCount){
            sendDune[ sendDune.length/2][ sendDune.length/2]=0;
            sendAway( sendDune.length/2, sendDune.length/2,sendDune);
        }
        return sendDune;
    }

    /**
     * 这里采用递归模拟沙堆分散
     * @param i
     * @param j
     * @param sendDune
     */
    private void sendAway(int i,int j,int[][] sendDune) {
        if (i+1<sendDune.length){
//                     边界判断是否超出右边界
            if (++sendDune[i+1][j]==maxCount){
                sendDune[i+1][j]=0;
                sendAway(i+1,j,sendDune);
            }

        }
        if (i-1>=0){
//                     边界判断是否超出左边界

            if (++sendDune[i-1][j]==maxCount){
                sendDune[i-1][j]=0;
                sendAway(i-1,j,sendDune);
            }
        }
        if (j-1>=0){
//                     边界判断是否超出上边界
            if (++sendDune[i][j-1]==maxCount){
                sendDune[i][j-1]=0;
                sendAway(i,j-1,sendDune);
            }
        }
        if (j+1<sendDune[i].length){
//                     边界判断是否超出下边界
            if (++sendDune[i][j+1]==maxCount){
                sendDune[i][j+1]=0;
                sendAway(i,j+1,sendDune);
            }
        }
    }
}
