package inorg.gsdl.plndpt;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

class BaseActivity extends AppCompatActivity implements LogoutListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(App.getApplicationTheme());
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Set Listener to receive events
        App.registerSessionListener(this);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        //reset session when user interact
        App.resetSession();

    }

    @Override
    public void onSessionLogout() {
        // Do You Task on session out
    }

}