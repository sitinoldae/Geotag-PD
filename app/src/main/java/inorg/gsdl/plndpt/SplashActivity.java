package inorg.gsdl.plndpt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plndpt.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {
ApplicationUtility applicationUtility;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (visibility -> {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        hideNavigationBar();
                    }
                });
        setContentView(R.layout.activity_splash);
        applicationUtility=new ApplicationUtility();
        Timer t = new Timer();
        boolean checkConnection = new ApplicationUtility().checkConnection(SplashActivity.this);
        int splace_time = 1500;
        if (checkConnection) {
            t.schedule(new splash(), splace_time);
        } else {
            applicationUtility.showSnack(SplashActivity.this,"connection not found...please check internet connection");
        }
    }


    private void hideNavigationBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    class splash extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(() ->
            applicationUtility.showSnack(SplashActivity.this,"Welcome to GeoTag Planning"));
            Intent i = new Intent(SplashActivity.this, LoginSelector.class);
            finish();
            startActivity(i);
        }
    }
}
