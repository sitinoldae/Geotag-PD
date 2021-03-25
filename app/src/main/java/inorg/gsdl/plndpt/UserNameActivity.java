package inorg.gsdl.plndpt;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plndpt.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.text.TextUtils.isEmpty;

public class UserNameActivity extends AppCompatActivity {

    @SuppressLint("NonConstantResourceId")
    @BindView(R.id.username_app)
    EditText edit_name_et;

    private Sharedpreferences mpref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_name);

        ButterKnife.bind(this);
        mpref = Sharedpreferences.getUserDataObj(this);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(R.id.username_btn_verify)
    public void click_on(View view) {
        if (isEmpty(edit_name_et.getText().toString())) {
            edit_name_et.setError("please enter name");
            edit_name_et.requestFocus();
            return;
        } else {
            Intent intent = new Intent(UserNameActivity.this, EmailActivity.class);
            mpref.set_user_name_verif(edit_name_et.getText().toString());
            startActivity(intent);
        }
    }
}
