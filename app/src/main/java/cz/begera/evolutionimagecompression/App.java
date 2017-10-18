package cz.begera.evolutionimagecompression;

import android.app.Application;

import com.miguelbcr.ui.rx_paparazzo.RxPaparazzo;

import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 16.10.17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RxPaparazzo.register(this);
        Timber.plant(new Timber.DebugTree());
    }
}
