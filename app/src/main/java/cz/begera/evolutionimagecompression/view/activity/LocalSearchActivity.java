package cz.begera.evolutionimagecompression.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.begera.evolutionimagecompression.R;
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

    @BindView(R.id.imv_original)
    protected ImageView imvOriginal;

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

    }

}
