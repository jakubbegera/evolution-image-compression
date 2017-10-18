package cz.begera.evolutionaryimagecompression.model;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 18.10.17.
 */
public class Circle extends Shape {

    private int x;
    private int y;
    private int diameter;

    public Circle(int color, int x, int y, int diameter) {
        super(color);
        this.x = x;
        this.y = y;
        this.diameter = diameter;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDiameter() {
        return diameter;
    }

    public void setDiameter(int diameter) {
        this.diameter = diameter;
    }
}
