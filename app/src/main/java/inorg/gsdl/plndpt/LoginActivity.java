 package inorg.gsdl.plndpt;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ProgressBar;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.appcompat.app.AppCompatActivity;

 import com.android.volley.RequestQueue;
 import com.android.volley.toolbox.JsonArrayRequest;
 import com.android.volley.toolbox.Volley;
 import com.example.plndpt.R;

 import org.json.JSONException;
 import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    EditText etemail, etpass;
    int c = 0;
    private Sharedpreferences mpref;
    private Boolean VERIFIED;
    private ProgressBar progressBar;
    private final boolean USER_FOUND=false;
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityloginuser);
        TextView createnewac = findViewById(R.id.createnewac);
        etemail = findViewById(R.id.etemail);
        etpass = findViewById(R.id.mypass);
        progressBar = findViewById(R.id.progressLogin);
        mpref = Sharedpreferences.getUserDataObj(this);
        Button btnlogin = findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(v -> logIn());
        createnewac.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterationActivity.class)));
    }

    private void logIn() {
        progressBar.setVisibility(View.VISIBLE);
        String email = etemail.getText().toString().trim();
        String pass = etpass.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etemail.setError("Please Enter Email");
            etemail.requestFocus();
            progressBar.setVisibility(View.GONE);
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etpass.setError("Please Enter Password");
            progressBar.setVisibility(View.GONE);
            etpass.requestFocus();
            return;
        }
        String APPROVED_USERS = "http://map.gsdl.org.in:8080/planningdpt/viewLogin/1";
        String UNAPPROVED_USERS = "http://map.gsdl.org.in:8080/planningdpt/viewLogin/0";
        // String DATA_URL = "http://map.gsdl.org.in:8080/viewLogin/1";
        //Creating a string request
        message="";
        ProcessJsonFromUrl(APPROVED_USERS,email,pass);
        ProcessJsonFromUrl(UNAPPROVED_USERS,email,pass);
    }
    private void ProcessJsonFromUrl(String url,String email,String pass){
        JsonArrayRequest stringRequest = new JsonArrayRequest(url,
                response -> {
                    JSONObject object;
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            object = response.getJSONObject(i);
                            String password = object.getString("password");
                            String userid = object.getString("userid");
                            VERIFIED = Boolean.valueOf(object.getString("status"));
                            if (email.equalsIgnoreCase(userid) && pass.equals(password)) {
                                c++;
                                mpref.set_User_Mobile_verif(object.getString("mobile"));
                                mpref.set_user_name_verif(object.getString("name"));
                                mpref.set_user_email_verif(object.getString("userid"));
                                mpref.setCircle_concerned_officer_mob(object.getString("dptname"));
                            }
                        }

                        if (c > 0) {
                            if(VERIFIED==true) {
                                Intent intent = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                                intent.putExtra("phone_number", mpref.get_User_Mobile_verif());
                                intent.putExtra("name", mpref.get_user_name_verif());
                                intent.putExtra("userid", mpref.get_user_email_verif());
                                intent.putExtra("dptname", mpref.getCircle_concerned_officer_mob());
                                progressBar.setVisibility(View.GONE);
                                message="Login Success !";
                                startActivity(intent);
                            }else if(VERIFIED==false){
                             message="Unverified User, Please contact Administrator ";
                            }
                        } else {
                            message="Invalid Credentials, Please Check Credentials";
                        }
                    } catch (JSONException e) {
                        progressBar.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
                    },
                error -> System.out.println("Error :" + error.getMessage()));

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
