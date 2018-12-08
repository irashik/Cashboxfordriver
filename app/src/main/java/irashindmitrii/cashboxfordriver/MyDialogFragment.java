package irashindmitrii.cashboxfordriver;

/**
 * Created by dmitrii on 30.10.17.
 * класс для создания диалогового окна:
 * создания окна при попытке закрыть программу.
 *
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class MyDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Закрыть приложение?")
               .setCancelable(true)
               .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                System.exit(10);
            }
        })
               .setNegativeButton("Cancel", null);
        return builder.create();
    }
}

