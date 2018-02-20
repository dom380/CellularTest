package com.example.dom.cellulartest;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Dom on 12/10/2016.
 */

public class DrawingView extends SurfaceView implements SurfaceHolder.Callback {

    private DrawThread thread;
    private Grid grid;

    public DrawingView(Context context, Grid grid) {
        super(context);
        getHolder().addCallback(this);
        this.grid = grid;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setWillNotDraw(false);
        thread = new DrawThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void draw(Canvas canvas) {
        int gridWidth = grid.getGridWidth();
        int gridHeight = grid.getGridHeight();
        for(int i=0; i<gridHeight; i++){
            for(int j = 0; j<gridWidth; j++){
                grid.getCells()[i][j].draw(canvas);
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        try {
            thread.setRunning(false);
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
