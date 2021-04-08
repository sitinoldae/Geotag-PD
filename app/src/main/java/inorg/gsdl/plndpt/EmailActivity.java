package inorg.gsdl.plndpt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.text.TextUtils.isEmpty;

public class EmailActivity extends AppCompatActivity {

    private static String dateString;
    @SuppressLint("NonConstantResourceId")
    //@BindView(R.id.email_app)
            EditText email_edit_et;
    long date;
    private Sharedpreferences mpref;
    private ApiInterface reg_in_service;
    private ProgressShow progressShow;
    private AwesomeValidation awesomeValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_email);

        ButterKnife.bind(this);

        mpref = Sharedpreferences.getUserDataObj(this);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        addValidationToViews();

        reg_in_service = ApiClient.getClient().create(ApiInterface.class);

        date = System.currentTimeMillis();

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
        dateString = sdf.format(date);

        Log.d("check_date", dateString);

    }

    @SuppressLint("NonConstantResourceId")
    // @OnClick(R.id.email_btn_verify)
    public void clicl_email(View view) {

        if (isEmpty(email_edit_et.getText().toString())) {
            email_edit_et.setError("please enter email");
            email_edit_et.requestFocus();
        } else {

            if (awesomeValidation.validate()) {
                //  hit_login();
                hit_register_user();
            }
        }
    }

    private void hit_register_user() {

        ProgressShow.showProgress(EmailActivity.this);

        Call<JsonElement> get_user_registered = reg_in_service.getUser_Registration(mpref.get_user_name_verif(),
                email_edit_et.getText().toString(), mpref.get_User_Mobile_verif(), dateString);

        Log.d("cv", mpref.get_user_name_verif() + " " + email_edit_et.getText().toString() + " " + mpref.get_User_Mobile_verif() +
                " " + dateString);

        get_user_registered.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                ProgressShow.stopProgress(EmailActivity.this);

                if (response.isSuccessful()) {
                    JsonElement json__Element = response.body();
                    String reg_in_string = String.valueOf(json__Element);
                    try {
                        JSONObject json = new JSONObject(reg_in_string);
                        String carr = json.getString("Cargo");
                        if (carr.equals("1")) {
                            // user_log_in_now();
                            hit_login_final();
                            Log.d("cargo_respo", carr);
                        } else {
                            Toast.makeText(EmailActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("reg_in_respo", reg_in_string);

                    //  user_log_in_now();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                ProgressShow.stopProgress(EmailActivity.this);
                Log.d("reg_in_error", t.toString());
            }
        });
    }

 /*   private void user_log_in_now() {

        String URL_LOG_now = "http://gsdl.org.in/edu_test/WebService.asmx/selectUser";

        progressShow.showProgress(EmailActivity.this);

        StringRequest log_in_now_str_req = new StringRequest(Request.Method.POST, URL_LOG_now,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("log_in_volley_resp",response);

                        progressShow.stopProgress(EmailActivity.this);

                        String splitt = response;
                        String[] result = splitt.split("<");
                        String part11 = result[0];
                        String part22 = result[1];
                        Log.d("check_part",part11+"  "+part22);

                        try {
                            JSONObject jjaray_go_now = new JSONObject(part11);
                            String cargo_check_now = jjaray_go_now.getString("Cargo");

                            Log.d("_cargo_val",cargo_check_now);

                            if (cargo_check_now.equals("0")){

                            } else {
                                String user_name_now = jjaray_go_now.getString("Name");
                                String user_email_now = jjaray_go_now.getString("Email");
                                String user_mobile_now = jjaray_go_now.getString("Mobile");

                                Intent new_intent = new Intent(EmailActivity.this, MainActivity.class);
                                mpref.set_user_name_verif(user_name_now);
                                mpref.set_user_email_verif(user_email_now);
                                mpref.set_User_Mobile_verif(user_mobile_now);
                                mpref.set_user_id(String.valueOf(1));

                                Log.d("whole_data_check",user_name_now+" "+user_email_now+" "+user_mobile_now);

                                startActivity(new_intent);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                progressShow.stopProgress(EmailActivity.this);
                Log.i("volley_respo_error", "" + error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();

                param.put("mobile",mpref.get_User_Mobile_verif());
                Log.d("mm",mpref.get_User_Mobile_verif());
                return param;
            }

            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        log_in_now_str_req.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(log_in_now_str_req);

    }*/

    public void addValidationToViews() {

        // awesomeValidation.addValidation(EmailActivity.this, R.id.email_app, Patterns.EMAIL_ADDRESS, R.string.invalid_email);

    }


    private void hit_login_final() {

        ProgressShow.showProgress(EmailActivity.this);

        Call<JsonElement> get_log_in_data = reg_in_service.get_Log_IN_Info(mpref.get_User_Mobile_verif());

        get_log_in_data.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {

                ProgressShow.stopProgress(EmailActivity.this);

                if (response.isSuccessful()) {
                    JsonElement jsonElement_login = response.body();
                    String log_in_string = String.valueOf(jsonElement_login);
                    try {
                        JSONObject jjaray_go_now = new JSONObject(log_in_string);
                        String cargo_check_now = jjaray_go_now.getString("Cargo");

                        Log.d("_cargo_val", cargo_check_now);

                        //noinspection StatementWithEmptyBody
                        if (cargo_check_now.equals("0")) {

                        } else {
                            String user_name_now = jjaray_go_now.getString("Name");
                            String user_email_now = jjaray_go_now.getString("Email");
                            String user_mobile_now = jjaray_go_now.getString("Mobile");

                            Intent new_intent = new Intent(EmailActivity.this, ReportFormActivity.class);
                            mpref.set_user_name_verif(user_name_now);
                            mpref.set_user_email_verif(user_email_now);
                            mpref.set_User_Mobile_verif(user_mobile_now);
                            mpref.set_user_id(String.valueOf(1));

                            Log.d("whole_data_check", user_name_now + " " + user_email_now + " " + user_mobile_now);

                            startActivity(new_intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("log_in_respo", log_in_string);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                ProgressShow.stopProgress(EmailActivity.this);
                Log.d("log_in_error", t.toString());
            }
        });
    }
}
