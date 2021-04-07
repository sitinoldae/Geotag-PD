package inorg.gsdl.plndpt;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.plndpt.R;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;

import static android.text.TextUtils.isEmpty;

public class VerifyOTPActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.verify_otrp_et)
    EditText otp_tv;
    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.text_resend)
    TextView resend_tv;
    RequestQueue requestQueue;
    private ApiInterface mobile_number_service;
    private Sharedpreferences mpref;
    private String phone_number_for_otp, id, dptname;
    private ProgressShow progressShow;
    private CountDownTimer countTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);
        System.out.print("verify OTP");
        ApiInterface api_service = ApiClient.getClient().create(ApiInterface.class);
        ButterKnife.bind(this);
        mpref = Sharedpreferences.getUserDataObj(this);

        requestQueue = Volley.newRequestQueue(this);
        mobile_number_service = ApiClient.getClient().create(ApiInterface.class);

        Random random1 = new Random();
        id = String.format("%04d", random1.nextInt(10000));

        mpref.set_otp_verification(id);
        System.out.println("otp=" + id);
        Intent intent = this.getIntent();
        phone_number_for_otp = intent.getStringExtra("phone_number");
        dptname = intent.getStringExtra("dptname");


        countTimer =new CountDownTimer(60000, 1000) { // adjust the milli seconds here

            public void onTick(long millisUntilFinished) {
                String text = String.format(Locale.getDefault(), "Time Remaining %02d min: %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);

                resend_tv.setText(text);
            }

            public void onFinish() {
                resend_tv.setText("Resend");
            }
        };

        // message is the fetching OTP
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equalsIgnoreCase("otp")) {
                    final String message = intent.getStringExtra("message");
                    // message is the fetching OTP
                    Log.d("otpp_value_check", message);
                    otp_tv.setText(message);
                }
            }
        };

        hit_retrofit();
    }

    private void hit_retrofit() {

        if (phone_number_for_otp.equals("")) {
            Toast.makeText(getApplicationContext(), "Please Register your mobile number", Toast.LENGTH_LONG).show();
        }
        ProgressShow.showProgress(VerifyOTPActivity.this);

        Call<String> call_number = mobile_number_service.getVerifyMobileNumber(phone_number_for_otp, id);
        call_number.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                ProgressShow.stopProgress(VerifyOTPActivity.this);

                if(response.code()==200){
                    Toast.makeText(getApplicationContext(),"Otp Sent",Toast.LENGTH_SHORT).show();
                    countTimer.start();
                }else{
                    Toast.makeText(getApplicationContext(),"Response Code : "+response.code(),Toast.LENGTH_SHORT).show();
                }

                // if (response.isSuccessful()) {
                String responseString = response.body();
                // todo: do something with the response string
                //  Log.d("my_repo",responseString);


                //}

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Error", t.toString());
                ProgressShow.stopProgress(VerifyOTPActivity.this);
            }
        });

    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.text_resend)
    public void resend_otp(View view) {

        mpref.set_otp_verification(id);
        hit_resend_otp();

        Log.d("new_value_rand", id);
        try {
            countTimer.cancel();
        } catch (Exception e) {
            e.printStackTrace();
        }
        countTimer.start();
    }

    private void hit_resend_otp() {

        final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

        Random random1 = new Random();
        id = String.format("%04d", random1.nextInt(10000));

        mpref.set_otp_verification(id);

        String URL_resend_otp = Uri.encode("http://gsdl.org.in/SMS/Smsservice.aspx?mobile=" + phone_number_for_otp +
                        "&sms=" + id,
                ALLOWED_URI_CHARS);

        Log.d("url_value", URL_resend_otp);

        StringRequest strrequest_resend_otp = new StringRequest(Request.Method.GET, URL_resend_otp, response -> Log.d("new_repo", response), error -> Log.i("new_respo_error", "" + error)) {
        };
//        strrequest_resend_otp.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().addToRequestQueue(strrequest_resend_otp);
        requestQueue.add(strrequest_resend_otp);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.otp_conti_btn)
    public void btn(View view) {

        if (isEmpty(otp_tv.getText().toString())) {
            otp_tv.setError("please enter your OTP");
            otp_tv.requestFocus();
            return;
        } else {
            if (otp_tv.getText().toString().equals(mpref.get_otp_verification())) {
                hit_log_in();

            } else {
                Toast.makeText(VerifyOTPActivity.this, "please enter correct otp", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hit_log_in() {
        ProgressShow.showProgress(VerifyOTPActivity.this);
        mpref.setLogged_in(true);
//        Call<JsonElement> get_log_in_data_check = api_service.get_Log_IN_Info(mpref.get_User_Mobile_verif());
//
//        get_log_in_data_check.enqueue(new Callback<JsonElement>() {
//            @Override
//            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
//
//                progressShow.stopProgress(VerifyOTPActivity.this);
//
//                if (response.isSuccessful()) {
//                    JsonElement jsonElement_login_check = response.body();
//                    String log_in_string_check = String.valueOf(jsonElement_login_check);
//                    try {
//                        JSONObject jjaray_go = new JSONObject(log_in_string_check);
//                        String cargo_check = jjaray_go.getString("Cargo");
//
//                        Log.d("check_cargo",cargo_check);
//
//                        if (cargo_check.equals("0")){
//                            Intent intent = new Intent(VerifyOTPActivity.this, UserNameActivity.class);
//                            startActivity(intent);
//                        } else {
//                            String user_name = jjaray_go.getString("Name");
//                            String user_email = jjaray_go.getString("Email");
//                            String user_mobile = jjaray_go.getString("Mobile");

        Intent intent = new Intent(VerifyOTPActivity.this, MainActivityHome.class);
        intent.putExtra("phone_number", mpref.get_User_Mobile_verif());
        intent.putExtra("name", mpref.get_user_name_verif());
        intent.putExtra("userid", mpref.get_user_email_verif());
        intent.putExtra("dptname", dptname);
        System.out.print("dptname==" + dptname);

//                            mpref.set_user_name_verif(user_name);
//                            mpref.set_user_email_verif(user_email);
//                            mpref.set_User_Mobile_verif(user_mobile);
//                            mpref.set_user_id(String.valueOf(1));
//
//                            Log.d("whole_data",user_name+" "+user_email+" "+user_mobile);
//
        startActivity(intent);

        Toast.makeText(getApplicationContext(), "Successfully Login", Toast.LENGTH_LONG).show();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    Log.d("log_in_respo",log_in_string_check);
//                }
    }
//
//            @Override
//            public void onFailure(Call<JsonElement> call, Throwable t) {
//                progressShow.stopProgress(VerifyOTPActivity.this);
//                Log.d("log_in_error_check",t.toString());
//            }
//        });
//
//
//    }

//
//    @Override
//    public void onResume() {
//        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
//        super.onPause();
//    }
//
//    public boolean onOptionsItemSelected(MenuItem item){
//      onBackPressed();
//      return true;
//
//    }
//
//    @Override
//    public void onBackPressed() {
//       // super.onBackPressed();
//        Intent intent = new Intent(VerifyOTPActivity.this,PhoneNumberVerifyActivity.class);
//        startActivity(intent);
//    }
}
