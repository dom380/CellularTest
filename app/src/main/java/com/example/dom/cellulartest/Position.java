package com.example.dom.cellulartest;

/**
 * Created by Dom on 12/10/2016.
 */

public class Position {
    public int x;
    public int y;

    public Position() {
        x = 0;
        y = 0;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position add(Position other) {
        return new Position(x + other.x, y + other.y);
    }

    @Override
    public boolean equals(Object ob) {
        if (ob == null) return false;
        if (ob.getClass() != getClass()) return false;
        Position other = (Position) ob;
        return (other.x == x) && (other.y == y);
    }

    @Override
    public int hashCode() {
        return (x * y);
    }

}
