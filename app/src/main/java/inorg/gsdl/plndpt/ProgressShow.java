package inorg.gsdl.plndpt;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.plndpt.R;

public class ProgressShow {

    public static ProgressDialog progressSimple;


    public static void showProgress(Context context) {

        try {

            progressSimple = new ProgressDialog(context, R.style.MyAlertDialogStyle);
            progressSimple.setMessage("Please wait ...");
            progressSimple.setCancelable(false);
            //  progressSimple.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopProgress(Context context) {
        try {
            progressSimple.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
