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

                    for (int i = 0; i < pixOriginal.length; i++) {
                        for (int j = 0; j < pixOriginal[i].length; j++) {
                            int pixel = bitmap.getPixel(i, j);
                            pixOriginal[i][j] = Color.red(pixel);
                        }
                    }

                    for (int i = 0; i < numberOfIterations; i++) {
                        doIteration();
                        if (animate) {
                            saveToBitmap(bitmap);
                            subscriber.onNext(bitmap);
                            try {
                                Thread.sleep(2);
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

                out[i][j] = (int) ((out[i][j] + shape.getColor()) / 2.0);
            }
        }

        return out;
    }

    private long computeFitness(int[][] pix1, int[][] pix2) {
        long l = 0;
        for (int i = 0; i < pix1.length; i++) {
            for (int j = 0; j < pix1[i].length; j++) {
                l += Math.abs(pix1[i][j] - pix2[i][j]);
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
//        int i20 = (int) (Math.random() * (min / 20.0));
//        int i50 = (int) (Math.random() * (min / 50.0));
//        return (int) ((i20 + i50) / 2.0);

        int maxDiameter = min / 12;
        return (int) (Math.random() * maxDiameter);

//        double d1 = Math.random() * (min / 10);
//        double d2 = Math.random() * (min / 30);
//        return (int) ((d1 + d2) / 2.0);

//        return (int) ((Math.random() * 78) % pixOriginal.length % 30);
    }

    private void saveToBitmap(Bitmap bitmap) {
        for (int i = 0; i < pix.length; i++) {
            for (int j = 0; j < pix[0].length; j++) {
                bitmap.setPixel(i, j, Color.argb(0, pix[i][j], pix[i][j], pix[i][j]));
            }
        }
    }
}
