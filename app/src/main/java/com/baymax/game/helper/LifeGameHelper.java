package com.baymax.game.helper;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import java.util.List;

/**
 * @Created by  Baymax on 2020/9/7.
 * Copyright © 2020/9/7 Hyperspace Technology(Beijing)Co.,Ltd. All rights reserved.
 * @e-mail 761241917@qq.com
 */
public class LifeGameHelper {
    /**
     * 记录全部过程 (Stack记录会oom......醉了)
     */
//    private  Stack<int[][]> allHabit=new Stack<>();
    /**
     * @param width 宽高
     * @param high 边界index
     */
    private  char cells[][];

    /**
     * 创建生命游戏
     * @param width
     * @param high
     */
    public  char[][] createGame(int width, int high) {
        cells =new char[width][high];
        return cells;
    }

    /**
     * 添加细胞
     * @param x
     * @param y
     */
    public  void addCell(int x, int y) {
        if (x>=0&&x< cells.length&&y>=0&&y< cells[0].length){
            cells[x][y]=3;
        }else {
            Log.i("Cell","超出游戏边界");
        }
    }
    /**
     * 计算下一代
     */
    public  char[][] calculationNextCell(){
        //创建下一代细胞
        char[][] nextCells=new char[cells.length][cells[0].length];
        //根据上一代细胞计算下一代细胞的权重
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j]==3){
                    checkedNextCell(i, j, nextCells);
                }
                if (i>=10&&j>=10){
                    if (nextCells[i-10][j-10]==3){
//                        权重为3为出生
                        nextCells[i-10][j-10]=3;
                    }else if (nextCells[i-10][j-10]==2){
//                        权重为2 和上一代一样保持不变
                        nextCells[i-10][j-10]= cells[i-10][j-10];
                    }else {
//                        其他权重 重置为0
                        nextCells[i-10][j-10]=0;
                    }
                }
            }
        }
//        减少循环次数
        for (int i = nextCells.length-10; i < nextCells.length; i++) {
            for (int j = nextCells[i].length-10; j < nextCells[i].length; j++) {
                if (nextCells[i][j]==3){
                    nextCells[i][j]=3;
                }else if (nextCells[i][j]==2){
                    nextCells[i][j]= cells[i][j];
                }else {
                    nextCells[i][j]=0;
                }
            }
        }
        cells =nextCells;
//        allHabit.push(habit);
        return cells;
    }
    public char[][] lastHabit(){
//        habit = allHabit.pop();
        return cells;
    }

    /**
     * * 对坐标（+i,+j）点细胞 周围的每个点权重+1
     * @param i
     * @param j
     */
    private  void checkedNextCell(int i, int j, char cells[][]) {
        if (i+1<cells.length){
//                     边界判断是否超出右边界
            ++cells[i+1][j];
        }
        if (i-1>=0){
//                     边界判断是否超出左边界

            ++cells[i-1][j];
        }
        if (j-1>=0){
//                     边界判断是否超出上边界
            ++cells[i][j-1];
        }
        if (j+1<cells[i].length){
//                     边界判断是否超出下边界
            ++cells[i][j+1];
        }
        if (i+1<cells.length&&j-1>=0){
//                     边界判断是否超出右上边界
            ++cells[i+1][j-1];
        }
        if (i+1<cells.length&&j+1<cells[i].length){
//                     边界判断是否超出右下边界
            ++cells[i+1][j+1];
        }
        if (i-1>=0&&j+1<cells[i].length){
//                     边界判断是否超出左下边界
            ++cells[i-1][j+1];
        }
        if (i-1>=0&&j-1>=0){
//                     边界判断是否超出左下边界
            ++cells[i-1][j-1];
        }
    }

    public  Rect getRect(int[][] cells) {
        Rect rect = new Rect();
        int minX=Integer.MAX_VALUE,minY=Integer.MAX_VALUE,maxX=0,maxY=0;
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j]==3){
                    if (minX>i){
                        minX=i;
                    }
                    if (minY>j){
                        minY=j;
                    }
                    if (maxX<i){
                        maxX=i;
                    }
                    if (maxY<j){
                        maxY=j;
                    }
                }
            }
        }
        rect.set(minX,minY,maxX,maxY);
        return rect;
    }

    public  void editCell(int[][] cells, List<Point> touchXY) {
        for (Point point : touchXY) {
            if (point.x<cells.length&&point.y<cells[point.x].length){
                cells[point.x][point.y]=cells[point.x][point.y]==3?0:3;
            }
        }
    }
    public  void editCell(int[][] cells, Point point) {
        if (point.x<cells.length&&point.y<cells[point.x].length){
            cells[point.x][point.y]=cells[point.x][point.y]==3?0:3;
        }
    }
}
