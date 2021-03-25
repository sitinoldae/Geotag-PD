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
    private final String VERIFIED="";
    private ProgressBar progressBar;

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
            hideProgressBar();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etpass.setError("Please Enter Password");
            hideProgressBar();
            etpass.requestFocus();
            return;
        }
        String ALL_USER = "http://map.gsdl.org.in:8080/planningdpt/dptlogin/viewAllLogin";
        // String UNAPPROVED_USERS = "http://map.gsdl.org.in:8080/planningdpt/viewLogin/0";
        // String DATA_URL = "http://map.gsdl.org.in:8080/viewLogin/1";
        //Creating a string request.
        String message = "";
        ProcessJsonFromUrl(ALL_USER,email,pass);
    }
    private void ProcessJsonFromUrl(String url,String email,String pass){
        JsonArrayRequest stringRequest = new JsonArrayRequest(url,
                response -> {
                    JSONObject object;
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            object = response.getJSONObject(i);
                            String userPassword = object.getString("password");
                            String userId = object.getString("userid");
                            String userStatus = object.getString("status");
                            if (email.equalsIgnoreCase(userId) && pass.equals(userPassword)) {
                                c++;
                                mpref.set_User_Mobile_verif(object.getString("mobile"));
                                mpref.set_user_name_verif(object.getString("name"));
                                mpref.set_user_email_verif(object.getString("userid"));
                                mpref.setCircle_concerned_officer_mob(object.getString("dptname"));
                                if(userStatus.matches("1")){
                                    Intent intent = new Intent(LoginActivity.this, VerifyOTPActivity.class);
                                    intent.putExtra("phone_number", mpref.get_User_Mobile_verif());
                                    intent.putExtra("name", mpref.get_user_name_verif());
                                    intent.putExtra("userid", mpref.get_user_email_verif());
                                    intent.putExtra("dptname", mpref.getCircle_concerned_officer_mob());
                                    hideProgressBar();
                                    proceedLogin(intent,mpref.get_user_name_verif());
                                    return;
                                }else if(userStatus.matches("0")){
                                    Qtoast("User not approved ! Contact administrator");
                                    return;
                                }
                            }else if(i==response.length()-1){
                               Qtoast("Login Failed ! Check Credentials");
                               hideProgressBar();
                                return;
                                }
                        }
                    } catch (JSONException e) {
                        hideProgressBar();
                        e.printStackTrace();
                    }
                    hideProgressBar();
                },
                error -> System.out.println("Error :" + error.getMessage()));

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void hideProgressBar() {
        try {
            if(progressBar.getVisibility()==View.VISIBLE){
                progressBar.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedLogin(Intent intent,String user) {
    startActivity(intent);
    Qtoast("Login Success !\nWelcome "+user);
    }

    private void Qtoast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}
