package cz.begera.evolutionimagecompression.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.begera.evolutionimagecompression.R;
import cz.begera.evolutionimagecompression.usecase.CompressImageUsecase;
import cz.begera.evolutionimagecompression.usecase.GenerateBWImageUsecase;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 16.10.17.
 */
public class LocalSearchActivity extends AppCompatActivity {

    private static String EXTRA_ORIGINAL_PICTURE_PATH = "EXTRA_ORIGINAL_PICTURE_PATH";

    public static void start(Context context, String originalPicturePath) {
        context.startActivity(new Intent(context, LocalSearchActivity.class)
                .putExtra(EXTRA_ORIGINAL_PICTURE_PATH, originalPicturePath));
    }

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    @BindView(R.id.imv_original)
    protected ImageView imvOriginal;

    @BindView(R.id.imv_original_bw)
    protected ImageView imvOriginalBw;
    @BindView(R.id.prb_original_bw)
    protected ProgressBar prbOriginalBW;

    @BindView(R.id.imv_compress)
    protected ImageView imvCompress;
    @BindView(R.id.prb_compress)
    protected ProgressBar prbCompress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_search);
        ButterKnife.bind(this);

        String originalPicturePath = getIntent().getStringExtra(EXTRA_ORIGINAL_PICTURE_PATH);

        Picasso.with(this)
                .load(new File(originalPicturePath))
                .into(imvOriginal, new Callback() {
                    @Override
                    public void onSuccess() {
                        Timber.i("Picasso load ok");
                    }

                    @Override
                    public void onError() {
                        Timber.e("Picasso load ko");
                    }
                });
        initBwImage(originalPicturePath);

    }

    @Override
    protected void onDestroy() {
        compositeSubscription.clear();
        super.onDestroy();
    }

    private void initBwImage(String picturePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        Subscription subscription = new GenerateBWImageUsecase(bitmap).execute(new Observer<Bitmap>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "BW image init fail");
                prbOriginalBW.setVisibility(View.GONE);
            }

            @Override
            public void onNext(Bitmap bitmap) {
                imvOriginalBw.setImageBitmap(bitmap);
                initCompressImage(bitmap);
                prbOriginalBW.setVisibility(View.GONE);

            }
        });
        compositeSubscription.add(subscription);
    }

    private void initCompressImage(Bitmap bitmap) {
        Subscription subscription = new CompressImageUsecase(bitmap, true, 2000).execute(new Observer<Bitmap>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Timber.e(e, "BW image init fail");
                prbCompress.setVisibility(View.GONE);
            }

            @Override
            public void onNext(Bitmap bitmap) {
                imvCompress.setImageBitmap(bitmap);
                prbCompress.setVisibility(View.GONE);
            }
        });
        compositeSubscription.add(subscription);
    }
}
