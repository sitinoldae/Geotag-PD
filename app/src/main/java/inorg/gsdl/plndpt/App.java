package inorg.gsdl.plndpt;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import java.util.Timer;
import java.util.TimerTask;

public class App extends MultiDexApplication {

    private static LogoutListener logoutListener = null;
    private static Timer timer = null;
    private static Sharedpreferences mpref;
    private static Context context;

    @Override
    public void onCreate() {
        mpref = Sharedpreferences.getUserDataObj(this);
        super.onCreate();
        MultiDex.install(this);
        context = getApplicationContext();
    }


    public static void userSessionStart() {
        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (logoutListener != null) {
                    logoutListener.onSessionLogout();
                    Log.d("App", "Session Destroyed");
                    mpref.setLogged_in(false);
                    SharedPreferences preferences = context.getSharedPreferences("com.waterflood.gsdl_app", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.clear();
                    editor.apply();
                }
            }
        }, (1000 * 30 * 2));
    }

    public static void resetSession() {
        userSessionStart();
    }

    public static void registerSessionListener(LogoutListener listener) {
        logoutListener = listener;
    }
}