package cz.begera.evolutionaryimagecompression.view.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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
import cz.begera.evolutionaryimagecompression.R;
import cz.begera.evolutionaryimagecompression.model.IterationData;
import cz.begera.evolutionaryimagecompression.usecase.CompressImageUsecase;
import cz.begera.evolutionaryimagecompression.view.dialogs.AdjustNumberOfIterationsDialog;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;
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
    @BindView(R.id.prb_compress_iterations)
    protected MaterialProgressBar prbCompressIterations;
    @BindView(R.id.chart)
    protected LineChart chart;
    @BindView(R.id.txv_progress)
    protected TextView txvProgress;
    @BindView(R.id.txv_iterations)
    protected TextView txvIterations;
    @BindView(R.id.btn_adjust_iterations)
    protected Button btnAdjustIterations;
    @BindView(R.id.btn_stop)
    protected Button btnStop;
    @BindView(R.id.btn_start)
    protected Button btnStart;

    LineDataSet dataSet;
    LineData lineData;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private int iterations;

    public static void start(Context context, String originalPicturePath) {
        context.startActivity(new Intent(context, LocalSearchActivity.class)
                .putExtra(EXTRA_ORIGINAL_PICTURE_PATH, originalPicturePath));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_search);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        String originalPicturePath = getIntent().getStringExtra(EXTRA_ORIGINAL_PICTURE_PATH);
        updateNumberOfIterations(3000);

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

        btnAdjustIterations.setOnClickListener(v -> {
            AdjustNumberOfIterationsDialog dialog = new AdjustNumberOfIterationsDialog();
            dialog.setCallback(new AdjustNumberOfIterationsDialog.Callback() {
                @Override
                public void onNumberPicked(int i) {
                    compositeSubscription.clear();
                    updateNumberOfIterations(i);
                    initCompressImage(bitmap);
                }

                @Override
                public void onCancel() {

                }
            });
            dialog.show(getSupportFragmentManager(), "AdjustNumberOfIterationsDialog");
        });

        btnStop.setOnClickListener(v -> {
            compositeSubscription.clear();
            btnStop.setVisibility(View.GONE);
            btnStart.setVisibility(View.VISIBLE);
        });
        btnStart.setOnClickListener(v -> {
            initCompressImage(bitmap);
        });
}

    @Override
    protected void onDestroy() {
        compositeSubscription.clear();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initCompressImage(Bitmap bitmap) {
        lineData = null;
        prbCompressIterations.setMax(iterations);
        prbCompressIterations.setProgress(0);
        btnStop.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.GONE);

        Subscription subscription = new CompressImageUsecase(bitmap, true, iterations)
                .execute(new Observer<IterationData>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Timber.e(e, "BW image init fail");
                        prbCompress.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onNext(IterationData iteration) {
                        imvCompress.setImageBitmap(iteration.getBitmap().copy(Bitmap.Config.ARGB_8888, false));
                        prbCompress.setVisibility(View.INVISIBLE);
                        addChartEntry(iteration.getIterationNumber(), (float) iteration.getFitness());
                        prbCompressIterations.setProgress(iteration.getIterationNumber());
                        txvProgress.setText(getString(R.string.progress,
                                (iteration.getIterationNumber() / (double) iterations) * 100.0,
                                iteration.getIterationNumber(), iterations
                        ));
                        if (iteration.getIterationNumber() == iterations) {
                            btnStop.setVisibility(View.GONE);
                            btnStart.setVisibility(View.VISIBLE);
                        }
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

    private void updateNumberOfIterations(int iterations) {
        this.iterations = iterations;
        txvIterations.setText(getString(R.string.number_of_iterations, iterations));
    }
}
