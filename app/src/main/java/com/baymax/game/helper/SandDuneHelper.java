package com.baymax.game.helper;

import java.util.Random;

/**
 * 默认每一代向中心点加沙子+1
 * @Created by  Baymax on 2020/9/10.
 * @e-mail 761241917@qq.com
 */
public class SandDuneHelper {
    private char[][] sendDune;
    private char[][] lastSendDune;



    private int maxCount=4;

    private Random random;

    /**
     * 创建游戏
     * @param width
     * @param high
     */
    public  char[][] createGame(int width, int high) {
        sendDune =new char[width][high];
        lastSendDune=new char[width][high];
        random=new Random();

        return sendDune;
    }
    public void randomData(char[][] sendDune){
        if (sendDune==null)return;
        for (int i = 0; i < sendDune.length; i++) {
            for (int k = 0; k < sendDune[i].length; k++) {
                sendDune[i][k]= (char) random.nextInt(maxCount);
            }
        }
    }
    /**
     * 递归实现
     * 计算下一代（模拟在点（sendDune.length/2，sendDune.length/2）处不停滴落沙子）
     * @return
     */
    public char[][] nextSandDune() {
        for (int i = 0; i < sendDune.length; i++) {
            for (int j = 0; j < sendDune[i].length; j++) {
                if (sendDune[i][j]==0)continue;
                lastSendDune[i][j]=sendDune[i][j];
            }
        }

        sendDune[ sendDune.length/2][ sendDune[0].length/2]=3;
        sendDune[ sendDune.length/2][ sendDune[0].length/2]++;

        if (sendDune[ sendDune.length/2][ sendDune[0].length/2]==maxCount){
            sendDune[ sendDune.length/2][ sendDune.length/2]=0;
            sendAway( sendDune.length/2, sendDune.length/2,sendDune);
        }

        return sendDune;
    }

    /**
     *
     * 循环实现  (貌似是有问题的)
     * 计算下一代（模拟在点（sendDune.length/2，sendDune.length/2）处不停滴落沙子）
     * @return
     */
    public char[][] nextSandDuneLoop() {
        for (int i = 0; i < sendDune.length; i++) {
            for (int j = 0; j < sendDune[i].length; j++) {
                if (sendDune[i][j]==0)continue;
                lastSendDune[i][j]=sendDune[i][j];
            }
        }

        sendDune[ sendDune.length/2][ sendDune[0].length/2]=3;
        sendDune[ sendDune.length/2][ sendDune[0].length/2]++;
        for (int i = 0; i < sendDune.length; i++) {
            for (int j = 0; j < sendDune[i].length; j++) {
//                isBreak=false;
                if (sendDune[i][j]>=maxCount){
                    sendDune[i][j]= (char) (sendDune[i][j]-maxCount);
                    if (i+1<sendDune.length){
//                     边界判断是否超出右边界
                        ++sendDune[i+1][j];
                    }
                    if (i-1>=0){
//                     边界判断是否超出左边界
                        ++sendDune[i-1][j];
                    }
                    if (j+1<sendDune[i].length){
//                     边界判断是否超出下边界
                        ++sendDune[i][j+1];
                    }
                    if (j-1>=0){
//                     边界判断是否超出上边界
                        ++sendDune[i][j-1];
                    }
                    if (i-2>0)i=i-2;
                }
            }
        }

        return sendDune;
    }

    /**
     * 这里采用递归模拟沙堆分散
     * @param i
     * @param j
     * @param sendDune
     */
    private void sendAway(int i,int j,char[][] sendDune) {
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

    /**
     * 这里采用递归模拟沙堆分散
     * @param i
     * @param j
     * @param sendDune
     */
    private void sendAwayLoop(int i,int j,int[][] sendDune) {

        if (i+1<sendDune.length){
//                     边界判断是否超出右边界
            if (++sendDune[i+1][j]==maxCount){
                sendDune[i+1][j]=0;
                sendAwayLoop(i+1,j,sendDune);
            }

        }
        if (i-1>=0){
//                     边界判断是否超出左边界

            if (++sendDune[i-1][j]==maxCount){
                sendDune[i-1][j]=0;
                sendAwayLoop(i-1,j,sendDune);
            }
        }
        if (j-1>=0){
//                     边界判断是否超出上边界
            if (++sendDune[i][j-1]==maxCount){
                sendDune[i][j-1]=0;
                sendAwayLoop(i,j-1,sendDune);
            }
        }
        if (j+1<sendDune[i].length){
//                     边界判断是否超出下边界
            if (++sendDune[i][j+1]==maxCount){
                sendDune[i][j+1]=0;
                sendAwayLoop(i,j+1,sendDune);
            }
        }
    }


    public char[][] getLastSendDune() {
        return lastSendDune;
    }
}
