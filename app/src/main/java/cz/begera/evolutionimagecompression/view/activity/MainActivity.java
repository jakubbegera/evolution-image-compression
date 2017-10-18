package cz.begera.evolutionimagecompression.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.miguelbcr.ui.rx_paparazzo.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo.entities.Response;
import com.miguelbcr.ui.rx_paparazzo.entities.size.OriginalSize;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.begera.evolutionimagecompression.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_local_search_camera)
    protected Button btnLocalSearchCamera;
    @BindView(R.id.btn_local_search_gallery)
    protected Button btnLocalSearchGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        btnLocalSearchCamera.setOnClickListener(v -> {
            startCamera(response -> {
                LocalSearchActivity.start(MainActivity.this, response.data());
            });
        });

        btnLocalSearchGallery.setOnClickListener(v -> {
            startGallery(response -> {
                LocalSearchActivity.start(MainActivity.this, response.data());
            });
        });

    }

    public void startCamera(Action1<Response<MainActivity, String>> observer) {
        RxPaparazzo.takeImage(this)
                .useInternalStorage()
                .size(new OriginalSize())
                .usingCamera()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void startGallery(Action1<Response<MainActivity, String>> observer) {
        RxPaparazzo.takeImage(this)
                .useInternalStorage()
                .size(new OriginalSize())
                .usingGallery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
