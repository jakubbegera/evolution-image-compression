package cz.begera.evolutionimagecompression.view.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.begera.evolutionimagecompression.R;

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
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    String s = edt.getText().toString();
                    callback.onNumberPicked(Integer.parseInt(s));
                }
            }
        });
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (callback != null) {
                    callback.onCancel();
                }
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
