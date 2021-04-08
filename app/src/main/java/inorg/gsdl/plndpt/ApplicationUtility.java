package inorg.gsdl.plndpt;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.onurkagan.ksnack_lib.MinimalKSnack.MinimalKSnack;
import com.onurkagan.ksnack_lib.MinimalKSnack.MinimalKSnackStyle;

public class ApplicationUtility {

    ConnectivityManager connectivityManager;
    NetworkInfo info;

    public boolean checkConnection(Context context) {
        boolean flag = false;
        try {
            connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            info = connectivityManager.getActiveNetworkInfo();

            if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                System.out.println(info.getTypeName());
                flag = true;
            }
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                System.out.println(info.getTypeName());
                flag = true;
            }
        } catch (Exception exception) {
            System.out.println("Exception at network connection....."
                    + exception);
        }
        return flag;
    }

    public void showSnack(Activity activity, String message) {
        MinimalKSnack minimalKSnack = new MinimalKSnack(activity);
        minimalKSnack
                .setMessage(message) // message
                .setStyle(MinimalKSnackStyle.STYLE_INFO) // style
                .setDuration(1500) // you can use for auto close.
                // bottom align option.
                .show();
    }
}
