package com.example.plndpt;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserProfileActivity extends AppCompatActivity {

    private static String dateString;
    @BindView(R.id.display_one_name)
    AppCompatEditText name_et;

    @BindView(R.id.display_two_email)
    AppCompatEditText email_et;

    @BindView(R.id.display_three_mobile)
    AppCompatEditText mobile_et;

    @BindView(R.id.edit_update_profile)
    Button update_edut_btn;
    long date;
    private Sharedpreferences mpref;
    private KeyListener listener;
    private ProgressShow progressShow;
    private ApiInterface edit_profile_service;
    private String name, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_user_profile);

        ButterKnife.bind(this);

        mpref = Sharedpreferences.getUserDataObj(this);

        update_edut_btn.setVisibility(View.GONE);

        edit_profile_service = ApiClient.getClient().create(ApiInterface.class);

//        ActionBar actionBar = getActionBar();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("User Profile");

        date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        dateString = sdf.format(date);
        Log.d("check_date", dateString);

        mobile_et.setText(mpref.get_User_Mobile_verif());
        listener = mobile_et.getKeyListener();
        mobile_et.setKeyListener(null);
        email_et.setText(mpref.get_user_email_verif());
        listener = email_et.getKeyListener();
        email_et.setKeyListener(null);
        name_et.setText(mpref.get_user_name_verif());
        listener = name_et.getKeyListener();
        name_et.setKeyListener(null);

        final_update();
    }

    private void final_update() {

        progressShow.showProgress(UserProfileActivity.this);

        Call<JsonElement> get_log_in_data = edit_profile_service.get_Log_IN_Info(mpref.get_User_Mobile_verif());

        get_log_in_data.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                progressShow.stopProgress(UserProfileActivity.this);

                if (response.isSuccessful()) {
                    JsonElement jsonElement_login = response.body();
                    String log_in_string = String.valueOf(jsonElement_login);
                    try {
                        JSONObject jjaray_go_now = new JSONObject(log_in_string);
                        String cargo_check_now = jjaray_go_now.getString("Cargo");

                        Log.d("_cargo_val", cargo_check_now);

                        if (cargo_check_now.equals("0")) {

                        } else {
                            String user_name_now = jjaray_go_now.getString("Name");
                            String user_email_now = jjaray_go_now.getString("Email");
                            String user_mobile_now = jjaray_go_now.getString("Mobile");

                            // Intent new_intent = new Intent(UserProfileActivity.this, MainActivity.class);
                            mpref.set_user_name_verif(user_name_now);
                            mpref.set_user_email_verif(user_email_now);
                            mpref.set_User_Mobile_verif(user_mobile_now);
                            mpref.set_user_id(String.valueOf(1));

                            Log.d("whole_data_check", user_name_now + " " + user_email_now + " " + user_mobile_now);

                            // startActivity(new_intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("log_in_respo", log_in_string);
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                progressShow.stopProgress(UserProfileActivity.this);
                Log.d("log_in_error", t.toString());
            }
        });

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(UserProfileActivity.this, MainActivityHome.class);
        startActivity(intent);
    }

    @OnClick(R.id.upload_photo_tv)
    public void edit_profile_click(View view) {

        email_et.setKeyListener(listener);
        email_et.setFocusable(true);
        email_et.setEnabled(true);

        email = email_et.getText().toString();

        name_et.setKeyListener(listener);
        name_et.setFocusable(true);
        name_et.setEnabled(true);


        update_edut_btn.setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.edit_update_profile)
    public void update_profile_btnn(View view) {
        Edit_profile();
    }

    private void Edit_profile() {

        progressShow.showProgress(UserProfileActivity.this);

        Call<JsonElement> get_user_registered = edit_profile_service.getUser_profile_update(name_et.getText().toString(),
                email_et.getText().toString(), mpref.get_User_Mobile_verif(), dateString);

        Log.d("cv", name_et.getText().toString() + " " + email_et.getText().toString() + " " + mpref.get_User_Mobile_verif() + " " + dateString);

        get_user_registered.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {

                progressShow.stopProgress(UserProfileActivity.this);

                if (response.isSuccessful()) {
                    JsonElement json__Element = response.body();
                    String reg_in_string = String.valueOf(json__Element);
                    try {
                        JSONObject json = new JSONObject(reg_in_string);
                        String carr = json.getString("Cargo");
                        if (carr.equals("0")) {
                            Log.d("edit_cargo_respo", carr);
                            Intent intent = new Intent(UserProfileActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(UserProfileActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("edit_profile_respo", reg_in_string);

                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                progressShow.stopProgress(UserProfileActivity.this);
                Log.d("edit_profile_error", t.toString());
            }
        });

    }

    @OnClick(R.id.update_profile_user)
    public void click(View view) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        SharedPreferences preferences = getSharedPreferences("com.waterflood.gsdl_app", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        finish();
        startActivity(intent);
    }

}
