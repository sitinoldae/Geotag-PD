package inorg.gsdl.plndpt;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.messaging.Constants;

public class LogoutService extends Service {
        public static CountDownTimer timer;
        private Sharedpreferences mpref;

    @Override
    public void onCreate(){
        super.onCreate();
        mpref = Sharedpreferences.getUserDataObj(this);
        timer = new CountDownTimer(1 * 1 * 1000, 1000) {
                public void onTick(long millisUntilFinished) {
                   //Some code
                    Log.v(Constants.TAG, "App Running in background");
                }

                public void onFinish() {
                    Log.v(Constants.TAG, "App Resumed");
                    // Code for Logout
                    mpref.setLogged_in(false);
                    SharedPreferences preferences = getSharedPreferences("com.waterflood.gsdl_app", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                    stopSelf();
                }
             };
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}