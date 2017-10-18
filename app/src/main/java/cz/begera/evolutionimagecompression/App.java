package cz.begera.evolutionimagecompression;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.core.CrashlyticsCore;
import com.miguelbcr.ui.rx_paparazzo.RxPaparazzo;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 16.10.17.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(
                this,
                new Crashlytics.Builder().core(
                        new CrashlyticsCore.Builder()
                                .disabled(BuildConfig.DEBUG && isUSBConnected(this))
                                .build()
                ).build(),
                new Answers()
        );
        RxPaparazzo.register(this);
        Timber.plant(new Timber.DebugTree());
    }

    public static boolean isUSBConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        return intent.getExtras().getBoolean("connected");
    }
}
