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

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.begera.evolutionimagecompression.R;
import cz.begera.evolutionimagecompression.model.IterationData;
import cz.begera.evolutionimagecompression.usecase.CompressImageUsecase;
import rx.Observer;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 16.10.17.
 */
public class LocalSearchActivity extends AppCompatActivity {

    private static String EXTRA_ORIGINAL_PICTURE_PATH = "EXTRA_ORIGINAL_PICTURE_PATH";
    @BindView(R.id.imv_original)
    protected ImageView imvOriginal;
    @BindView(R.id.imv_compress)
    protected ImageView imvCompress;
    @BindView(R.id.prb_compress)
    protected ProgressBar prbCompress;
    @BindView(R.id.chart)
    protected LineChart chart;
    LineDataSet dataSet;
    LineData lineData;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    public static void start(Context context, String originalPicturePath) {
        context.startActivity(new Intent(context, LocalSearchActivity.class)
                .putExtra(EXTRA_ORIGINAL_PICTURE_PATH, originalPicturePath));
    }

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

        Bitmap bitmap = BitmapFactory.decodeFile(originalPicturePath);

        initCompressImage(bitmap);

    }

    @Override
    protected void onDestroy() {
        compositeSubscription.clear();
        super.onDestroy();
    }

    private void initCompressImage(Bitmap bitmap) {


        Subscription subscription = new CompressImageUsecase(bitmap, true, 4000)
                .execute(new Observer<IterationData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "BW image init fail");
                        prbCompress.setVisibility(View.GONE);
                    }

                    @Override
                    public void onNext(IterationData iteration) {
                        imvCompress.setImageBitmap(iteration.getBitmap());
                        prbCompress.setVisibility(View.GONE);
                        addChartEntry(iteration.getIterationNumber(), (float) iteration.getFitness());
                    }
                });
        compositeSubscription.add(subscription);
    }

    private void addChartEntry(int x, float y) {
        if (lineData == null) {
            dataSet = new LineDataSet(new ArrayList<Entry>(), "Fitness"); // add entries to dataset
            dataSet.setDrawFilled(true);
            dataSet.setHighlightEnabled(false);
            lineData = new LineData(dataSet);
            chart.setData(lineData);
            chart.getLegend().setEnabled(false);
            chart.getAxisRight().setEnabled(false);
            chart.getDescription().setEnabled(false);

        }

        lineData.addEntry(new Entry(x, y), 0);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }
}
