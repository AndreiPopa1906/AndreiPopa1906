package Compusory;

import java.awt.*;
import java.io.Serializable;


public class CoordPair implements Serializable {
    private static final long serialVersionUID = 1L;
    int x1;
    int x2;
    int y1;
    int y2;
    int type;
    public CoordPair(int x1, int y1, int x2, int y2, int type) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.type = type;
    }

    public int getX1() {
        return x1;
    }

    public int getX2() {
        return x2;
    }

    public int getY1() {
        return y1;
    }

    public int getY2() {
        return y2;
    }

    @Override
    public String toString() {
        return "CoordPair{" +
                "x1=" + x1 +
                ", y1=" + y1 +
                ", x2=" + x2 +
                ", y2=" + y2 +
                ", type=" + type +
                '}';
    }

    /**
     * Gets the Point object if the other end of a stick.
     * @param x coordonate.
     * @param y coordonate.
     * @return a Point object.
     */
    public Point getOtherEnd(int x, int y) {
        if (this.x1 == x && this.y1 == y) {
            return new Point(this.x2, this.y2);
        } else if (this.x2 == x && this.y2 == y) {
            return new Point(this.x1, this.y1);
        } else {
            return null;
        }
    }

    /**
     * Chexks for conectivity.
     * @param p Point object.
     * @return true if the Point object connects to another point of the current CoordPair object.
     */
    public boolean connects(Point p) {
        return (this.x1 == p.x && this.y1 == p.y) || (this.x2 == p.x && this.y2 == p.y);
    }

    /**
     * Checks for conectivity.
     * @param x coordonate.
     * @param y coordonate.
     * @return true if the Point object(only the coordonates) connects to another point of the current CoordPair object.
     */
    public boolean isConnectedTo(int x, int y) {
        return (this.x1 == x && this.y1 == y) || (this.x2 == x && this.y2 == y);
    }

}
