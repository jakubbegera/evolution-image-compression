package cz.begera.evolutionaryimagecompression.model;

import android.graphics.Bitmap;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 18.10.17.
 */
public class IterationData {

    private int iterationNumber;
    private Bitmap bitmap;
    private long fitness;

    public IterationData(int iterationNumber, Bitmap bitmap, long fitness) {
        this.iterationNumber = iterationNumber;
        this.bitmap = bitmap;
        this.fitness = fitness;
    }

    public int getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(int iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public long getFitness() {
        return fitness;
    }

    public void setFitness(long fitness) {
        this.fitness = fitness;
    }
}
