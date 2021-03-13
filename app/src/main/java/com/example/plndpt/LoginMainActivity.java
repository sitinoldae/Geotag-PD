package com.example.plndpt;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoginMainActivity extends AppCompatActivity {
    EditText etemail, etpass;
    int c = 0;
    private Sharedpreferences mpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityloginuser);
        TextView createnewac = findViewById(R.id.createnewac);
        etemail = findViewById(R.id.etemail);
        etpass = findViewById(R.id.mypass);
        mpref = Sharedpreferences.getUserDataObj(this);
        // final String email=etemail.getText().toString().trim();
        //final String pass=etpass.getText().toString().trim();
        Button btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logIn();
            }
        });


        createnewac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginMainActivity.this, SignUpActivity.class));


            }
        });
    }

    private void logIn() {
        String email = etemail.getText().toString().trim();
        String pass = etpass.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etemail.setError("Please Enter Email");
            etemail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etpass.setError("Please Enter Password");
            etpass.requestFocus();
            return;
        }


        String DATA_URL = "http://map.gsdl.org.in:8080/planningdpt/viewLogin/1";
        // String DATA_URL = "http://10.0.2.2:8080/viewLogin/1";
        //Creating a string request
        JsonArrayRequest stringRequest = new JsonArrayRequest(DATA_URL,
                new com.android.volley.Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSONObject j = null;
                        List<String> arrayList = new ArrayList<String>();
                        JSONObject object;
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                object = response.getJSONObject(i);
                                String userid = object.getString("userid");
                                String password = object.getString("password");

                                if (email.equalsIgnoreCase(userid) && pass.equals(password)) {

                                    c++;

                                    mpref.set_User_Mobile_verif(object.getString("mobile"));
                                    mpref.set_user_name_verif(object.getString("name"));
                                    mpref.set_user_email_verif(object.getString("userid"));
                                    mpref.setCircle_concerned_officer_mob(object.getString("dptname"));


                                }


                                //arrayList.add(dptname);
                            }

                            if (c > 0) {

                                Intent intent = new Intent(LoginMainActivity.this, VerifyOTPActivity.class);
                                intent.putExtra("phone_number", mpref.get_User_Mobile_verif());
                                intent.putExtra("name", mpref.get_user_name_verif());
                                intent.putExtra("userid", mpref.get_user_email_verif());
                                intent.putExtra("dptname", mpref.getCircle_concerned_officer_mob());
                                //mpref.set_User_Mobile_verif(otp_edit_txt.getText().toString());
                                startActivity(intent);


                            } else {
                                Toast.makeText(getApplicationContext(), "Please enter correct user Id and password", Toast.LENGTH_LONG).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("dpterror" + error.getMessage());
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);


    }
}
