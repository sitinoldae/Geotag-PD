package inorg.gsdl.plndpt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plndpt.R;

public class LoginSelector extends AppCompatActivity {
    Button login, signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        signup = findViewById(R.id.signup);
        login = findViewById(R.id.login);


        login.setOnClickListener(view -> {
            Intent intent = new Intent(LoginSelector.this, LoginActivity.class);
            startActivity(intent);
        });
        signup.setOnClickListener(view -> {
            Intent intent = new Intent(LoginSelector.this, RegisterationActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this).setTitle("Really Exit?").setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    LoginSelector.super.onBackPressed();
                    quit();
                }).create().show();
    }

    public void quit() {
        Intent start = new Intent(Intent.ACTION_MAIN);
        start.addCategory(Intent.CATEGORY_HOME);
        start.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(start);
    }
}