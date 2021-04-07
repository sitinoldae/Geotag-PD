package inorg.gsdl.plndpt;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class App extends Application {

   private static LogoutListener logoutListener = null;
   private static Timer timer = null;
   private static Sharedpreferences mpref;
    private static Context context;

   @Override
   public void onCreate() {
       mpref = Sharedpreferences.getUserDataObj(this);
       super.onCreate();
       context=getApplicationContext();
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
           },  (1000 * 60 * 2) );
       }

       public static void resetSession() {
           userSessionStart();
       }

       public static void registerSessionListener(LogoutListener listener) {
           logoutListener = listener;
       }
}