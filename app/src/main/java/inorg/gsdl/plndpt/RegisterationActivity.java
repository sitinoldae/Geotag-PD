package inorg.gsdl.plndpt;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.plndpt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RegisterationActivity extends AppCompatActivity implements Callback<MyUserData> {
    //  public static final String DATA_URL = "http://map.gsdl.org.in:8080/planningdpt/viewProjects";
    //public static final String DATA_URL = "http://10.0.2.2:8080/viewprojectnameonly";
    public static final String DATA_URL = "http://map.gsdl.org.in:8080/planningdpt/viewprojectnameonly";
    EditText etname, etemail, etpass, mobile;
    MyUserData d1 = new MyUserData();
    private View mLoading;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activitysignup);
        etname = findViewById(R.id.editName);
        etemail = findViewById(R.id.editEmail);
        etpass = findViewById(R.id.editPass);
        mobile = findViewById(R.id.editMob);

        Button btn_new = findViewById(R.id.buttonAcount);
        setUIRef();

        //Display Loading
        showLoading();

        spinner = findViewById(R.id.spinnerdpt);
        getData();
        btn_new.setOnClickListener(v -> {
            final String name = etname.getText().toString().trim();
            final String email = etemail.getText().toString().trim();
            final String pass = etpass.getText().toString().trim();
            final String mobiles = mobile.getText().toString().trim();

            if (TextUtils.isEmpty(name)) {
                etname.setError("Please Enter Name");
                etname.requestFocus();
                return;
            }
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

            if (TextUtils.isEmpty(mobiles)) {
                mobile.setError("Please Enter Mobile");
                mobile.requestFocus();
                return;
            }


            d1.setMobile(mobiles);
            d1.setUserId(email);
            d1.setPassword(pass);
            d1.setName(name);
            d1.setStatus("0");
            d1.setDeptcode("5000");

            servicecall();


        });

    }


    @Override
    public void onResponse(Call<MyUserData> call, Response<MyUserData> response) {

        if (response.code() == 200) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog.Builder alertDialogBuilder2 = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("Do you want to submit");
            alertDialogBuilder.setPositiveButton("yes",
                    (arg0, arg1) -> {
                        alertDialogBuilder2.setMessage("Successfully submitted but wait for approval");
                        alertDialogBuilder2.setPositiveButton("yes",
                                (arg01, arg11) -> {
                                    Intent intent = new Intent(RegisterationActivity.this, LoginSelector.class);
                                    startActivity(intent);
                                    finish();
                                });
                        AlertDialog alertDialog2 = alertDialogBuilder2.create();
                        alertDialog2.show();
                    });

            alertDialogBuilder.setNegativeButton("No", (dialog, which) -> finish());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

    }

    @Override
    public void onFailure(Call<MyUserData> call, Throwable t) {
    }

    private void servicecall() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface2.URL_LOGIN)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface2 apiInterface = retrofit.create(ApiInterface2.class);

        // prepare call in Retrofit 2.0
        try {
            JSONObject paramObject = new JSONObject();

            paramObject.put("userid", d1.getUserId());
            paramObject.put("password", d1.getPassword());
            paramObject.put("status", d1.getStatus());
            paramObject.put("mobile", d1.getMobile());
            paramObject.put("deptcode", d1.getDeptcode());
            paramObject.put("timestamp", "0");
            paramObject.put("name", d1.getName());
            paramObject.put("dptname", d1.getDptname());


            Call<MyUserData> userCall = apiInterface.addLogin(paramObject.toString());
            userCall.enqueue(this);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void setUIRef() {
        //Create a Instance of the Loading Layout
        mLoading = findViewById(R.id.my_loading_layout);
    }


    private void showLoading() {
        /*Call this function when you want progress dialog to appear*/
        if (mLoading != null) {
            mLoading.setVisibility(View.VISIBLE);
        }
    }


    private void hideLoading() {
        /*Call this function when you want progress dialog to disappear*/
        if (mLoading != null) {
            mLoading.setVisibility(View.GONE);
        }
    }

    private void getData() {

        //Creating a string request
        JsonArrayRequest stringRequest = new JsonArrayRequest(DATA_URL,
                response -> {
                    JSONObject j = null;
                    List<String> arrayList = new ArrayList<String>();

                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject object = response.getJSONObject(i);
                            String dptname = object.getString("dptname");
                            System.out.print("dptname" + dptname);
                            arrayList.add(dptname);
                        }

                        Set<Integer> hashSet = new LinkedHashSet(arrayList);
                        ArrayList<String> removedDuplicates = new ArrayList(hashSet);
                        spinner.setAdapter(new ArrayAdapter<>(RegisterationActivity.this, android.R.layout.simple_spinner_dropdown_item, removedDuplicates));
                        //Parsing the fetched Json String to JSON Object
                        //j = new JSONObject(response);
                        hideLoading();
                        //Storing the Array of JSON String to our JSON Array
                        //  result = object.getJSONArray(JSON_ARRAY);
                      //Calling method getStudents to get the students from the JSON Array
                        // getStudents(result);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("dpterror" + error.getMessage());
                    }
                });
        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        //Adding request to the queue
        requestQueue.add(stringRequest);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                d1.setDptname(spinner.getSelectedItem().toString());
                //Creating a string request
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
}
