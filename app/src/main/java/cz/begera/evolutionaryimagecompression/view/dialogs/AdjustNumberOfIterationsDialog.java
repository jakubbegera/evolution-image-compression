package cz.begera.evolutionaryimagecompression.view.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.begera.evolutionaryimagecompression.R;
import timber.log.Timber;

/**
 * Created by Jakub Begera (jakub@easycoreapps.com) on 18.10.17.
 */
public class AdjustNumberOfIterationsDialog extends DialogFragment {

    @BindView(R.id.edt)
    protected EditText edt;
    private Callback callback;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_set_iterations, null);
        ButterKnife.bind(this, v);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v);
        builder.setTitle("Set number of iterations");
        builder.setPositiveButton(getString(R.string.ok), (dialog, which) -> {
            if (callback != null) {
                String s = edt.getText().toString();
                try {
                    callback.onNumberPicked(Integer.parseInt(s));
                } catch (NumberFormatException e) {
                    Timber.i(e);
                    Toast.makeText(getActivity(),
                            String.format("\"%s\" isn't valid number.", s), Toast.LENGTH_LONG
                    ).show();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            if (callback != null) {
                callback.onCancel();
            }
        });
        builder.create();
        return builder.show();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public interface Callback {
        void onNumberPicked(int i);

        void onCancel();
    }


}
