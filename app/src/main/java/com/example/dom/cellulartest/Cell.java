package com.example.dom.cellulartest;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * Created by Dom on 12/10/2016.
 */

public class Cell extends Drawable implements Comparable<Cell> {

    protected Position position;
    protected CellType cellType = CellType.FLOOR;
    private int neighbourValue;
    private Paint paint = new Paint();
    double gScore = 999999;
    double hScore = 0.0;
    double fScore = 999999;
    Cell parent;

    public Cell() {
        position = new Position();
        neighbourValue = 0;
    }

    public Cell(int x, int y) {
        position = new Position(x, y);
        neighbourValue = 0;
    }

    public Cell(int x, int y, CellType type) {
        position = new Position(x, y);
        cellType = type;
        neighbourValue = 0;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public CellType getCellType() {
        return cellType;
    }

    public void setCellType(CellType cellType) {
        this.cellType = cellType;
    }

    public int getNeighbourValue() {
        return neighbourValue;
    }

    public void setNeighbourValue(int neighbourValue) {
        this.neighbourValue = neighbourValue;
    }


    public void updateScore(double g_score, double h_score, Cell parent) {
        gScore = g_score;
        hScore = h_score;
        this.parent = parent != null ? parent : this.parent;
        fScore = gScore + hScore;

    }

    public void reset() {
        gScore = 999999;
        hScore = 0.0;
        fScore = 999999;
        parent = null;
    }

    @Override
    public void draw(Canvas canvas) {
        switch (cellType) {
            case ROCK:
                paint.setARGB(255, 107, 124, 130);
                break;
            case WALL:
                paint.setARGB(255, 255, 0, 0);
                break;
            case FLOOR:
                paint.setARGB(255, 255, 255, 255);
                break;
            case START:
                paint.setARGB(255, 0, 255, 0);
                break;
            case GOAL:
                paint.setARGB(255, 255, 0, 0);
                break;
            case PATH:
                paint.setARGB(255, 23, 163, 209);
                break;
            case VISITED:
                paint.setARGB(125, 255, 255, 0);
                break;
        }
        Rect r = new Rect(25 * position.x, 25 * position.y, 25 + (25 * position.x), 25 + (25 * position.y));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(r, paint);
        paint.setARGB(255, 0, 0, 0);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(r, paint);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public int compareTo(Cell o) {
        if (o.fScore > fScore) return -1;
        if (o.fScore < fScore) return 1;
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o.getClass() != getClass()) return false;
        Cell other = (Cell) o;
        if (other.fScore != fScore && other.gScore != gScore && other.hScore != hScore)
            return false;
        return position.equals(other.position);
    }
}
