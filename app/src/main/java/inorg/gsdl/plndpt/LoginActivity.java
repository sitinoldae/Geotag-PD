 package inorg.gsdl.plndpt;

 import android.content.Intent;
 import android.os.Bundle;
 import android.text.TextUtils;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.ProgressBar;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.appcompat.app.AlertDialog;
 import androidx.appcompat.app.AppCompatActivity;

 import com.android.volley.RequestQueue;
 import com.android.volley.toolbox.JsonArrayRequest;
 import com.android.volley.toolbox.Volley;
 import com.example.plndpt.R;

 import org.json.JSONException;
 import org.json.JSONObject;

 import rezwan.pstu.cse12.youtubeonlinestatus.recievers.NetworkChangeReceiver;

 public class LoginActivity extends AppCompatActivity {
    EditText etemail, etpass;
    int c = 0;
    private Sharedpreferences mpref;
    private final String VERIFIED="";
    private ProgressBar progressBar;
    private boolean username_verified=false;
    String ALL_USER = "http://map.gsdl.org.in:8080/planningdpt/dptlogin/viewAllLogin";
    private ImageView iv_verification;
     private Button check_username_btn;
     private Button btnlogin;
     private ImageView iv_verification2;
     private ApplicationUtility applicationUtility;

     @Override
    protected void onStart() {
        try {
            NetworkChangeReceiver changeReceiver = new NetworkChangeReceiver(this);
            changeReceiver.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityloginuser);
        applicationUtility=new ApplicationUtility();
        TextView createnewac = findViewById(R.id.createnewac);
        etemail = findViewById(R.id.etemail);
        TextView tvpass = findViewById(R.id.tv_password);
        etpass = findViewById(R.id.mypass);
        progressBar = findViewById(R.id.progressLogin);
        check_username_btn = findViewById(R.id.checkUsernameBtn);
        mpref = Sharedpreferences.getUserDataObj(this);
        iv_verification=(findViewById(R.id.iv_verification));
        iv_verification2=(findViewById(R.id.iv_verification2));
         btnlogin = findViewById(R.id.btnlogin);
        check_username_btn.setOnClickListener(v ->
                VerifyUsername(ALL_USER,etemail.getText().toString()));
        btnlogin.setOnClickListener(v -> logIn());
        createnewac.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterationActivity.class)));
    }

    private void logIn() {
        progressBar.setVisibility(View.VISIBLE);
        String email = etemail.getText().toString().trim();
        String pass = etpass.getText().toString();
        if (TextUtils.isEmpty(email)) {
            etemail.setError("Please Enter Email");
            if(etpass.requestFocus()) {
             }
            hideProgressBar();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            etpass.setError("Please Enter Password");
            hideProgressBar();
            etpass.requestFocus();
            return;
        }
        // String UNAPPROVED_USERS = "http://map.gsdl.org.in:8080/planningdpt/viewLogin/0";
        // String DATA_URL = "http://map.gsdl.org.in:8080/viewLogin/1";
        //Creating a string request.
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
                                    applicationUtility.showSnack(LoginActivity.this,"User not approved ! Contact administrator");
                                    // TODO: 05-04-2021
                                    String Description = object.getString("description");
                                    if(!Description.isEmpty()){
                                        showUserUnapproved(Description);
                                    }else {
                                        applicationUtility.showSnack(LoginActivity.this,"User Description is " +
                                                "NULL");
                                        return;
                                    }

                                }
                            }else if(i==response.length()-1){
                                applicationUtility.showSnack(LoginActivity.this,"Login Failed ! Check Credentials");
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

     private void showUserUnapproved(String Description) {
         AlertDialog alertDialog;
         		alertDialog = new AlertDialog.Builder(LoginActivity.this)
                .setTitle("User Not Approved ! contact Admin")
                .setMessage("Reason : "+Description)
                .create();
         		alertDialog.show();
     }

     @Override
     public void onBackPressed() {
         AlertDialog.Builder builder = new AlertDialog.Builder(this);
         builder.setMessage("Do you want to start from the beginning ?")
                 .setCancelable(false)
                 .setPositiveButton("Yes", (dialog, id) -> {
                     try {
                         etemail.setText("");
                         etpass.setText("");
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                     LoginActivity.this.recreate();
                 })
                 .setNegativeButton("No", (dialog, id) -> dialog.cancel());
         AlertDialog alert = builder.create();
         alert.show();

     }

     private void VerifyUsername(String url, String email){
        showProgressBar();
        JsonArrayRequest stringRequest = new JsonArrayRequest(url,
                response -> {
                    JSONObject object;
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            object = response.getJSONObject(i);
                            String userId = object.getString("userid");
                            if (email.equalsIgnoreCase(userId)) {
                                applicationUtility.showSnack(LoginActivity.this,"User Verified\nEnter Password");
                                etemail.setVisibility(View.GONE);
                                etpass.setVisibility(View.VISIBLE);
                                btnlogin.setVisibility(View.VISIBLE);
                                iv_verification2.setVisibility(View.VISIBLE);
                                iv_verification2.focusSearch(View.FOCUS_DOWN);
                                iv_verification.setVisibility(View.GONE);
                                check_username_btn.setVisibility(View.GONE);
                                username_verified=true;
                                hideProgressBar();
                              }else{
                                if(i==response.length()-1 && username_verified==false){
                                    applicationUtility.showSnack(LoginActivity.this,"Username Invalid! try again or contact administration");
                                    hideProgressBar();
                                }
                                iv_verification2.setVisibility(View.GONE);
                                iv_verification.setVisibility(View.VISIBLE);}
                        }
                    } catch (JSONException e) {
                        hideProgressBar();
                        e.printStackTrace();
                    }
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
    } private void showProgressBar() {
        try {
            if(progressBar.getVisibility()==View.GONE|progressBar==null){
                progressBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void proceedLogin(Intent intent,String user) {
    startActivity(intent);
        applicationUtility.showSnack(LoginActivity.this,"Login Success !\nWelcome "+user);
    }

    private void Qtoast(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT).show();
    }
}
