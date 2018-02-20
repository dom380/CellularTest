package com.example.dom.cellulartest;

import android.graphics.Canvas;
import android.view.SurfaceHolder;

/**
 * Created by Dom on 12/10/2016.
 */

public class DrawThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private DrawingView drawingView;
    private boolean running;
    private Canvas canvas;

    public DrawThread(SurfaceHolder holder, DrawingView view){
        surfaceHolder = holder;
        drawingView = view;
    }

    @Override
    public void run() {
        while (running){
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    drawingView.postInvalidate();
                }
            } finally {
                if (canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }



    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
