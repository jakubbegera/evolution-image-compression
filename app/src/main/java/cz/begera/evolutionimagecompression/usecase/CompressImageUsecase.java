package cz.begera.evolutionimagecompression.usecase;

import android.graphics.Bitmap;
import android.graphics.Color;

import java.util.LinkedList;

import cz.begera.evolutionimagecompression.model.Circle;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 18.10.17.
 */
public class CompressImageUsecase implements Usecase<Bitmap> {

    private final Bitmap bitmap;
    private final boolean animate;
    private final int numberOfIterations;
    private int[][] pixOriginal;
    private int[][] pix;
    private LinkedList<Circle> shapes = new LinkedList<>();
    private int iteration;

    public CompressImageUsecase(Bitmap bitmap, boolean animate, int numberOfIterations) {
        this.bitmap = bitmap;
        this.animate = animate;
        this.numberOfIterations = numberOfIterations;
    }

    @Override
    public Subscription execute(Observer<Bitmap> observer) {
        return Observable
                .create((Observable.OnSubscribe<Bitmap>) subscriber -> {

                    Bitmap bitmap = this.bitmap.copy(Bitmap.Config.ARGB_8888, true);

                    pixOriginal = new int[bitmap.getWidth()][bitmap.getHeight()];
                    pix = new int[bitmap.getWidth()][bitmap.getHeight()];

                    for (int i = 0; i < pixOriginal.length; i++, iteration++) {
                        for (int j = 0; j < pixOriginal[i].length; j++) {
                            pixOriginal[i][j] = bitmap.getPixel(i, j);
                        }
                    }

                    for (int i = 0; i < numberOfIterations; i++) {
                        doIteration();
                        if (animate && i % 50 == 0) {
                            saveToBitmap(bitmap);
                            subscriber.onNext(bitmap);
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    saveToBitmap(bitmap);
                    subscriber.onNext(bitmap);
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private boolean doIteration() {
        long fitnessOrigin = computeFitness();
        for (int i = 0; i < 100; i++) {
            Circle circle = generateRandomShape();
            int[][] newPix = applyShape(pix, circle);
            long fitnessNew = computeFitness(newPix);
            if (fitnessNew < fitnessOrigin) {
                pix = newPix;
                return true;
            }
        }
        return false;
    }

    private Circle generateRandomShape() {
        int x = randomCoordinateX();
        int y = randomCoordinateY();
        int diameter = randomDiameter();

        return new Circle(pixOriginal[x][y], x, y, diameter);
    }

    private int[][] applyShape(int[][] pix, Circle shape) {

        // make copy
        int[][] out = new int[pix.length][pix[0].length];
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                out[i][j] = pix[i][j];
            }
        }

        // apply circle
        for (int i = Math.max(shape.getX() - shape.getDiameter(), 0);
             i < shape.getX() + shape.getDiameter() && i < out.length; i++) {
            for (int j = Math.max(shape.getY() - shape.getDiameter(), 0);
                 j < shape.getY() + shape.getDiameter() && j < out[i].length; j++) {

                double distanceFromCenter = Math.sqrt(
                        (i - shape.getX()) * (i - shape.getX()) +
                                (j - shape.getY()) * (j - shape.getY())
                );
                if (distanceFromCenter > shape.getDiameter()) {
                    continue;
                }

                int red = (int) ((Color.red(out[i][j]) + Color.red(shape.getColor())) / 2.0);
                int green = (int) ((Color.green(out[i][j]) + Color.green(shape.getColor())) / 2.0);
                int blue = (int) ((Color.blue(out[i][j]) + Color.blue(shape.getColor())) / 2.0);

                out[i][j] = Color.argb(0, red, green, blue);
            }
        }

        return out;
    }

    private long computeFitness(int[][] pix1, int[][] pix2) {
        long l = 0;
        for (int i = 0; i < pix1.length; i++) {
            for (int j = 0; j < pix1[i].length; j++) {
                l += Math.abs(Color.red(pix1[i][j]) - Color.red(pix2[i][j]));
                l += Math.abs(Color.green(pix1[i][j]) - Color.green(pix2[i][j]));
                l += Math.abs(Color.blue(pix1[i][j]) - Color.blue(pix2[i][j]));
            }
        }
        return l;
    }

    private long computeFitness(int[][] pix) {
        return computeFitness(pixOriginal, pix);
    }

    private long computeFitness() {
        return computeFitness(pixOriginal, pix);
    }


    private int randomCoordinateX() {
        return (int) (Math.random() * pixOriginal.length);
    }

    private int randomCoordinateY() {
        return (int) (Math.random() * pixOriginal[0].length);
    }

    private int randomDiameter() {
        int min = Math.min(pixOriginal.length, pixOriginal[0].length);
        int maxDiameter = min / 12;
        double iterationCoefficient = 1 - (double) (iteration/2) / (double) numberOfIterations;
        return (int) (Math.random() * maxDiameter * iterationCoefficient);
    }

    private void saveToBitmap(Bitmap bitmap) {
        for (int i = 0; i < pix.length; i++) {
            for (int j = 0; j < pix[0].length; j++) {
                bitmap.setPixel(i, j, pix[i][j]);
            }
        }
    }
}
