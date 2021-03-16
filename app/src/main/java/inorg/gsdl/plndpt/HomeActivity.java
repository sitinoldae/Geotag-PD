package inorg.gsdl.plndpt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plndpt.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

    }

    @OnClick(R.id.report_grievence_tv)
    public void repost_form(View v) {
        Intent intent = new Intent(HomeActivity.this, ReportFormActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.user_profille)
    public void profile_click() {
        Intent intent = new Intent(HomeActivity.this, UserProfileActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this).setTitle("Really Exit?").setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HomeActivity.super.onBackPressed();
                        quit();
                    }
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
