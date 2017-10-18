package cz.begera.evolutionaryimagecompression.usecase;

import android.graphics.Bitmap;
import android.graphics.Color;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 18.10.17.
 */
public class GenerateBWImageUsecase implements Usecase<Bitmap> {

    private final Bitmap bitmap;

    public GenerateBWImageUsecase(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Subscription execute(Observer<Bitmap> observer) {
        return Observable
                .create((Observable.OnSubscribe<Bitmap>) subscriber -> {

                    Bitmap bitmap = this.bitmap.copy(Bitmap.Config.ARGB_8888, true);
                    for (int i = 0; i < bitmap.getWidth(); i++) {
                        for (int j = 0; j < bitmap.getHeight(); j++) {
                            int pixel = bitmap.getPixel(i, j);
                            int redValue = Color.red(pixel);
                            int blueValue = Color.blue(pixel);
                            int greenValue = Color.green(pixel);
                            int rbValue = (int) ((redValue + blueValue + greenValue) / 3.0);
                            bitmap.setPixel(i, j, Color.argb(0, rbValue, rbValue, rbValue));
                        }
                    }

                    subscriber.onNext(bitmap);
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

}
