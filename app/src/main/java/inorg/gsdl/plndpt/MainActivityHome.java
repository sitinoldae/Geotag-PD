package inorg.gsdl.plndpt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plndpt.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivityHome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_mainhome);
        findViewById(R.id.user_profille).setOnLongClickListener(view -> {
            Intent intent = new Intent(MainActivityHome.this, CapturePictureActivity.class);
            startActivity(intent);
            return true;
        });

        ButterKnife.bind(this);

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.report_grievence_tv)
    public void repost_form(View v) {
        Intent intent = new Intent(MainActivityHome.this, ReportFormActivity.class);

        startActivity(intent);
    }


    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.user_profille)
    public void profile_click() {
        Intent intent = new Intent(MainActivityHome.this, UserProfileActivity.class);
        startActivity(intent);
    }
    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.progress)
    public void progress() {
        Intent intent = new Intent(MainActivityHome.this, ProgressActivity.class);
        startActivity(intent);
    }


    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this).setTitle("Really Exit?").setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    MainActivityHome.super.onBackPressed();
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
