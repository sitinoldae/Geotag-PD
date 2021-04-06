package inorg.gsdl.plndpt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.Random;

import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;

import static android.text.TextUtils.isEmpty;

public class PhoneNumberVerifyActivity extends AppCompatActivity {

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    @SuppressLint("NonConstantResourceId")
    //@BindView(R.id.enter_otp_et)
    EditText otp_edit_txt;

    private Sharedpreferences mpref;
    private String iddd;

    private ApiInterface mobile_number_service;

    public PhoneNumberVerifyActivity(ProgressShow progressShow) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_phone_number_verify);

        ButterKnife.bind(this);
        mpref = Sharedpreferences.getUserDataObj(this);

        mobile_number_service = ApiClient.getClient().create(ApiInterface.class);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Verify Number");

        Random random = new Random();
        @SuppressLint("DefaultLocale") String id = String.format("%04d", random.nextInt(10000));
        mpref.set_otp_verification(id);
        iddd = "Your OTP for water logging is " + id + ". Please use this code for the verification process.";
        Log.d("value_rand", id);
    }

    //@SuppressLint("NonConstantResourceId")
    //@OnClick(R.id.verify_num_btn)
    public void btn(View view) {
        // hit_number_for_otp();

        hit_retrofit();
    }

    private void hit_retrofit() {

        if (isEmpty(otp_edit_txt.getText().toString())) {

            otp_edit_txt.setError("Enter your mobile number");
            otp_edit_txt.requestFocus();
            return;
        }

        ProgressShow.showProgress(PhoneNumberVerifyActivity.this);

        Call<String> call_number = mobile_number_service.getVerifyMobileNumber(otp_edit_txt.getText().toString(), iddd);
        call_number.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                ProgressShow.stopProgress(PhoneNumberVerifyActivity.this);

                // if (response.isSuccessful()) {
                // todo: do something with the response string
                //  Log.d("my_repo",responseString);

                Intent intent = new Intent(PhoneNumberVerifyActivity.this, VerifyOTPActivity.class);
                intent.putExtra("phone_number", otp_edit_txt.getText().toString());
                mpref.set_User_Mobile_verif(otp_edit_txt.getText().toString());
                startActivity(intent);
                //}

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Log.e("Error", t.toString());
                ProgressShow.stopProgress(PhoneNumberVerifyActivity.this);
            }
        });

    }

//    private void hit_number_for_otp() {
//
//        final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";
//
//        String URL = Uri.encode("http://gsdl.org.in/SMS/Smsservice.aspx?mobile="+otp_edit_txt.getText().toString()+"&sms="+iddd,
//                ALLOWED_URI_CHARS);
//
//        Log.d("url_value",URL);
//
//        if (isEmpty(otp_edit_txt.getText().toString())) {
//
//            otp_edit_txt.setError("Enter your mobile number");
//            otp_edit_txt.requestFocus();
//            return;
//        }
//
//        progressShow.showProgress(PhoneNumberVerifyActivity.this);
//
//        StringRequest strrequest_otp = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//
//                progressShow.stopProgress(PhoneNumberVerifyActivity.this);
//                Log.d("my_repo",response);
//
//                Intent intent = new Intent(PhoneNumberVerifyActivity.this, VerifyOTPActivity.class);
//                intent.putExtra("phone_number",otp_edit_txt.getText().toString());
//                startActivity(intent);
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//                progressShow.stopProgress(PhoneNumberVerifyActivity.this);
//                Log.i("respo_error",""+error);
//            }
//        }){
//        };
//        strrequest_otp.setRetryPolicy(new DefaultRetryPolicy(0,-1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        AppController.getInstance().addToRequestQueue(strrequest_otp);
//    }


}
