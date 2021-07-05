package inorg.gsdl.plndpt;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plndpt.R;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ForgetPassword extends AppCompatActivity {
    String FORGOT_PASSWORD_URL="http://map.gsdl.org.in/updatequery/GetData.asmx/updatepwd/";
    EditText fp_email,fp_password,fp_password2;
    Button fp_pass_reset_btn;
    private ApplicationUtility applicationUtility;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        context=getApplicationContext();
        fp_email=findViewById(R.id.fp_email);
        fp_password=findViewById(R.id.fp_pass);
        fp_password2=findViewById(R.id.fp_pass2);
        fp_pass_reset_btn=findViewById(R.id.fp_reset_btn);
        applicationUtility=new ApplicationUtility();
        fp_pass_reset_btn.setOnClickListener(v -> {
            if(fp_email.getText().equals(null)||fp_password.getText().equals(null)||fp_password2.getText().equals(null)){
                Toast.makeText(getApplicationContext(),"please enter your the correct details before proceeding to reset password",Toast.LENGTH_SHORT).show();
            }else {
                proceedToResetPassword(fp_email,fp_password,fp_password2);
            }
        });
    }

    private void proceedToResetPassword(EditText fp_email, EditText fp_password, EditText fp_password2) {
        String email = fp_email.getText().toString().trim();
        String pass = fp_password.getText().toString();
        if (TextUtils.isEmpty(email)) {
            fp_email.setError("Please Enter Email");
            if (fp_email.requestFocus()) {
            }
            return;
        }
        if (TextUtils.isEmpty(fp_password.getText().toString())) {
            fp_password.setError("Please Enter Password");
            fp_email.requestFocus();
            return;
        }
        if(!fp_password.getText().toString().matches(fp_password2.getText().toString())) {
            fp_password2.setError("password do not match");
            fp_password2.requestFocus();
            return;
        }
     //proceed
        try {
         //   doServiceCall();
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url("http://map.gsdl.org.in/updatequery/GetData.asmx/updatepwd?username="+fp_email.getText().toString()+"&password="+fp_password.getText().toString())
                    .method("GET", null)
                    .build();
            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    runOnUiThread(() -> Toast.makeText(context, e.getMessage(),Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                    if(response.code()==200){
                        runOnUiThread(() -> Toast.makeText(context,response.message()+"password reset successfull\nplease login throgh the updated password",Toast.LENGTH_SHORT).show());
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
         
}