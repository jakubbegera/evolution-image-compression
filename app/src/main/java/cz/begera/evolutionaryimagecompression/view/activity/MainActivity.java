package cz.begera.evolutionaryimagecompression.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import com.miguelbcr.ui.rx_paparazzo.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo.entities.Response;
import com.miguelbcr.ui.rx_paparazzo.entities.size.CustomMaxSize;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.begera.evolutionaryimagecompression.R;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static int MAX_IMAGE_SIZE = 500;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.git_hub:
                String url = "https://github.com/jakubbegera/evolutionary-image-compression";
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void startCamera(Action1<Response<MainActivity, String>> observer) {
        RxPaparazzo.takeImage(this)
                .useInternalStorage()
                .size(new CustomMaxSize(MAX_IMAGE_SIZE))
                .usingCamera()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public void startGallery(Action1<Response<MainActivity, String>> observer) {
        RxPaparazzo.takeImage(this)
                .useInternalStorage()
                .size(new CustomMaxSize(MAX_IMAGE_SIZE))
                .usingGallery()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }
}
