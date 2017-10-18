package cz.begera.evolutionaryimagecompression.model;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 18.10.17.
 */
public abstract class Shape {

    private int color;

    protected Shape(int color) {
        this.color = color;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
