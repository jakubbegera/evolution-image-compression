package cz.begera.evolutionimagecompression.usecase;

import rx.Observer;
import rx.Subscription;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 18.10.17.
 */
public interface Usecase<T> {

    Subscription execute(Observer<T> observer);

}
