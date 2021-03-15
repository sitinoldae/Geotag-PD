package com.example.plndpt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;

import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.JsonElement;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReportFormActivity extends AppCompatActivity implements Callback<User>,Runnable {
    Thread t1;

    FusedLocationProviderClient fusedLocationProviderClient;
    private View mLoading;
    //String[] country = { "Pre-construction", "During Execution", "Final Completion"};
    String[] status = {"NO","YES"};
    public static final String DATA_URL = "http://map.gsdl.org.in:8080/planningdpt/viewProjects";
   // public static final String DATA_URL = "http://10.0.2.2:8080/viewProjects";

    public static final String JSON_ARRAY = "result";
    HashMap<String, String> capitalCities;

    AlertDialog.Builder builder;
    @BindView(R.id.issue_image)
    ImageView imageView_issue;

    @BindView(R.id.location_addr)
    TextView location_tv;
    //Creating a request queue
    RequestQueue requestQueue2;
    //Declaring an Spinner
    private Spinner spinnerdpt;

    //An ArrayList for Spinner Items
    private ArrayList<String> dptname;

    //JSON Array
    private JSONArray result;


    private Spinner spinner,spinerstatus;
    private String item;
    String div;
    private AlertDialog alertDialoggg;

    private LocationTrack locationTrack;
    // initializing
    // FusedLocationProviderClient
    // object
    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView;
    int PERMISSION_ID = 44;
    private Geocoder geocoder;
    List<Address> addresses;
    public  static final int RequestPermissionCode  = 1;
    private byte[] data, imageBytes;

    private Sharedpreferences mpref;
    private ApiInterface repost_form_interface;
    private ApiInterface2 repost_form_interface2;
    private Bitmap mphoto_bitmap, gallery_bitmap;
    private String imageString;
    private ProgressShow progressShow;
    private String my_value;
    private RequestQueue requestQueue;
    String appID;
    static int num;
    EditText dsc;
    long date;
    public static User u1=new User();
    private static String dateString;
            Spinner spin;
    private static final int PICK_PHOTO_FOR_AVATAR = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);
        setUIRef();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // method to get the location

        //Display Loading
        showLoading();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ReportFormActivity.this);


       spin = (Spinner) findViewById(R.id.spinnerprogress);
       spinerstatus = findViewById(R.id.spinnerstatus);

        mpref = Sharedpreferences.getUserDataObj(this);
        //Creating the ArrayAdapter instance having the country list
       // ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
        //aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
       // spin.setAdapter(aa);


        ArrayAdapter aa1 = new ArrayAdapter(this,android.R.layout.simple_spinner_item,status);
        aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spinerstatus.setAdapter(aa1);

        //Initializing the ArrayList
        dptname = new ArrayList<String>();
        //Initializing Spinner
        spinner = (Spinner) findViewById(R.id.spinner);
dsc = findViewById(R.id.dsc);
        //This method will fetch the data from the URL to get deptname

       final String DATA_URL = "http://map.gsdl.org.in:8080/planningdpt/viewpro/"+mpref.getCircle_concerned_officer_mob();
    //    final String DATA_URL = "http://10.0.2.2:8080/viewpro/"+mpref.getCircle_concerned_officer_mob();

        Log.d("url main",DATA_URL);
        getprojectname(DATA_URL);
        EditText name,email,mob;
        builder = new AlertDialog.Builder(this);
        u1=new User();
        if (android.os.Build.VERSION.SDK_INT > 19) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
           }

        requestQueue= Volley.newRequestQueue(this);
        ButterKnife.bind(this);
        name=findViewById(R.id.name);
        mob=findViewById(R.id.mobile);
        email=findViewById(R.id.email);


        name.setText(mpref.get_user_name_verif());
        u1.setName(mpref.get_user_name_verif());


        mob.setText(mpref.get_User_Mobile_verif());
        u1.setMobile(mpref.get_User_Mobile_verif());


        email.setText(mpref.get_user_email_verif());
        u1.setUserid(mpref.get_user_email_verif());

        u1.setLat(mpref.get_search_latitude());


        u1.setLog(mpref.get_search_Longitude());







        repost_form_interface = ApiClient.getClient().create(ApiInterface.class);
        repost_form_interface2 = ApiClient2.getClient().create(ApiInterface2.class);

//        ActionBar actionBar = getActionBar();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setTitle("Report Issue");

        geocoder = new Geocoder(this, Locale.getDefault());

        //spinner2 = (Spinner) findViewById(R.id.spinner);

        date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateString = sdf.format(date);

        Log.d("date_ccc",dateString);


        u1.setTimestamp(dateString);














        imageView_issue.setScaleType(ImageView.ScaleType.FIT_XY);

        if(getIntent().getStringExtra("search_text")!= null){
            location_tv.setVisibility(View.VISIBLE);
            location_tv.setText(getIntent().getStringExtra("search_text"));
        }else {
            location_tv.setVisibility(View.GONE);
        }

//        final String[] arraySpinner = new String[]{"Select Issue Type:", "Drainage", "Pipe Leakage", "Water Logging", "other"};
//
//        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                android.R.layout.simple_spinner_dropdown_item, arraySpinner);

//        spinner.setAdapter(adapter);

//        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                item = parent.getItemAtPosition(position).toString();
//                u1.setType_req(item);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
    spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            u1.setProgress(spin.getSelectedItem().toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    });

        spinerstatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                u1.setStatus(spinerstatus.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
}


    private void setUIRef()
    {
        //Create a Instance of the Loading Layout
        mLoading = findViewById(R.id.my_loading_layout);
    }


    private void showLoading()
    {
        /*Call this function when you want progress dialog to appear*/
        if (mLoading != null)
        {
            mLoading.setVisibility(View.VISIBLE);
        }
    }




    private void hideLoading()
    {
        /*Call this function when you want progress dialog to disappear*/
        if (mLoading != null)
        {
            mLoading.setVisibility(View.GONE);
        }
    }

    String getprojectname(String url)
    {

        JsonArrayRequest stringRequest2 = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject j = null;
                        List<String> arrayList = new ArrayList<>();
                         capitalCities = new HashMap<String, String>();

                        try {
                            for(int i=0;i<response.length();i++) {
                                JSONObject object = response.getJSONObject(i);
                                String dptname = object.getString("projectname");
                                String projectid = object.getString("projectid");

                                System.out.print("projectname"+dptname);
                                capitalCities.put(dptname, projectid);
                                arrayList.add(dptname);
                            }
                            spinner.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList));
                           // hideLoading();
                            //Parsing the fetched Json String to JSON Object
                            //j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            //  result = object.getJSONArray(JSON_ARRAY);
                            hideLoading();
                            //Calling method getStudents to get the students from the JSON Array
                            // getStudents(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("dpterror"+error.getMessage());
                    }
                });

        RequestQueue requestQueue2= Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue2.add(stringRequest2);


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               u1.setProject(spinner.getSelectedItem().toString());
                String project = spinner.getSelectedItem().toString();


                getreportprojectcheck(project);

                for (Map.Entry<String, String> e : capitalCities.entrySet())
                {

                    if(project.equals(e.getKey()))

                    {
                        u1.setProjectid(e.getValue());
                    }
                    System.out.println("Key: " + e.getKey()
                            + " Value: " + e.getValue());
            }


        }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return "";
    }

    private void getreportprojectcheck(String project) {
        showLoading();



        final String DATA_URLALlPROJECT = "http://map.gsdl.org.in:8080/planningdpt/viewReports";
        JsonArrayRequest stringRequest2 = new JsonArrayRequest(DATA_URLALlPROJECT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject j = null;
                        List<String> arrayList = new ArrayList<>();

                        try {
                            for(int i=0;i<response.length();i++) {
                                JSONObject object = response.getJSONObject(i);
                                String projectname = object.getString("project");

                                System.out.print("project"+projectname);

                                arrayList.add(projectname);
                            }


                            if(!arrayList.contains(project))
                            {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Pre-construction");
                                hideLoading();
                                spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));

                            }

                            else

                            {
                                checkprogressandstatus(project);

                            }

                          //  spinner.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList));
                            //Parsing the fetched Json String to JSON Object
                            //j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            //  result = object.getJSONArray(JSON_ARRAY);

                            //Calling method getStudents to get the students from the JSON Array
                            // getStudents(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("dpterror"+error.getMessage());
                    }
                });

        RequestQueue requestQueue2= Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue2.add(stringRequest2);

    }

    private void checkprogressandstatus(String project) {
        hideLoading();
        final String DATA_URLALlPROJECT = "http://map.gsdl.org.in:8080/planningdpt/viewReports";
        JsonArrayRequest stringRequest2 = new JsonArrayRequest(DATA_URLALlPROJECT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {



                        JSONObject j = null;
                        List<String> arrayList = new ArrayList<>();
                        HashMap<String, String> dataall = new HashMap<String, String>();

                        try {


                            {}
                            for(int i=0;i<response.length();i++) {
                                JSONObject object = response.getJSONObject(i);
                                String projectname = object.getString("project");
                                String status = object.getString("status");
                                String progress = object.getString("progress");

                                System.out.print("project"+projectname);

                                dataall.put("project",projectname);
                                dataall.put("status",status);
                                dataall.put("progress",progress);

                                System.out.println(dataall);

                               // arrayList.add(projectname);
                            }

                            if(dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Pre-construction") &&
                                    dataall.get("status").equalsIgnoreCase("NO")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Pre-construction");


                                spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            }

                            else
                            if(dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Pre-construction") &&
                                    dataall.get("status").equalsIgnoreCase("YES")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("During Execution");


                                spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            }
else
                            if(dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("During Execution") &&
                                    dataall.get("status").equalsIgnoreCase("NO")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("During Execution");


                                spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            }

                            else
                            if(dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("During Execution") &&
                                    dataall.get("status").equalsIgnoreCase("YES")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Final Completion");


                                spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            }
                            else

                            if(dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Final Completion") &&
                                    dataall.get("status").equalsIgnoreCase("NO")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Final Completion");


                                spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            }

                            else
                            if(dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Final Completion") &&
                                    dataall.get("status").equalsIgnoreCase("YES")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Final Completion");


                                spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            }




//                                }else
//
//                                if(m.getValue().equals("Pre-construction")&& m.getValue().equals("YES"))
//                                {
//
//
//
//                                    spin.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
//
//
//                                }
//                                System.out.println(m.getKey()+" "+m.getValue());
                         //   }



                            //  spinner.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList));
                            //Parsing the fetched Json String to JSON Object
                            //j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            //  result = object.getJSONArray(JSON_ARRAY);

                            //Calling method getStudents to get the students from the JSON Array
                            // getStudents(result);
                        } catch (JSONException e) {
                            System.out.println("dpterror"+e.getMessage());
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("dpterror"+error.getMessage());
                    }
                });

        RequestQueue requestQueue2= Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue2.add(stringRequest2);





    }


    @OnClick(R.id.issue_photo_layout)
    public void photo_upload_option_linear(View view){

        LayoutInflater li = LayoutInflater.from(ReportFormActivity.this);
        View promptsView = li.inflate(R.layout.camera_option_dialogue, null);

        Button button_camera = (Button) promptsView.findViewById(R.id.camera_btn);
        Button button_gallery = (Button) promptsView.findViewById(R.id.gallery_btn);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportFormActivity.this);
        alertDialogBuilder.setView(promptsView);
        alertDialoggg = alertDialogBuilder.create();
        alertDialoggg.show();

        button_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                System.out.print("preeti");
                alertDialoggg.dismiss();
                startActivityForResult(takePicture, 0);
            }
        });

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                alertDialoggg.dismiss();
                //  startActivityForResult(pickPhoto, 1);
          /*      Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
*/
                Intent intent = new Intent();
// Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);







            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {

            case 0:
                if(resultCode == RESULT_OK){
                    imageView_issue.setVisibility(View.VISIBLE);
                    mphoto_bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Log.d("new_value__camera", String.valueOf(mphoto_bitmap));

                    imageView_issue.setImageBitmap(mphoto_bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mphoto_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    System.out.print("encode64_coder"+imageString);
                    if (imageString.isEmpty()){

                    }else {
                        my_value = imageString;
                    }

                }
                break;
            case 1:
                System.out.println("yooo");
                if(resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null){
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d("nayi_value", String.valueOf(selectedImage));

                    imageView_issue.setVisibility(View.VISIBLE);
                    try {
                        gallery_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        Log.d("new_value__", String.valueOf(gallery_bitmap));

                        imageView_issue.setImageBitmap(gallery_bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                   // System.out.println("preeti=="+gallery_bitmap);
                    my_value  =getStringImage(gallery_bitmap);
                    System.out.print("honey"+my_value);

                }
                break;
        }
    }









    @OnClick(R.id.btn_submit)
    public void submit_form(View view){

        try {
            Random random= new Random();
            Calendar calender= Calendar.getInstance();
            appID="UAP"+random.nextInt(1000)+calender.get(Calendar.YEAR)+""+(calender.get(Calendar.MONTH)+1)+""+calender.get(Calendar.DATE)+""+
                    (calender.get(Calendar.HOUR))+""+calender.get(Calendar.MINUTE)+""+calender.get(Calendar.SECOND);


            u1.setReportid(appID);

            u1.setImages(my_value);

            String dscn = dsc.getText().toString();
            u1.setDescription(dscn);


            if(u1.getLat().equals("0.0") || u1.getLog().equals("0.0") ||u1.getLat().equals("") || u1.getLog().equals(""))
            {
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Please check your internet connection or GPS setting");
                alert.show();

                return;


            }


            //Setting message manually and performing action on button click
            builder.setMessage("Do you want to submit your request?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            IssueFormHit();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();

                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Confirmation");
            alert.show();


//            }else {
//                Toast.makeText(getApplicationContext(),"Upload Issue Image",Toast.LENGTH_LONG).show();
//            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error : "+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }

    private void Report_form() {

       // progressShow.showProgress(ReportFormActivity.this);

//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        mphoto_bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
//        byte[] imageBytes = baos.toByteArray();
//        imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

    //    Log.d("hgfdsa",imageString);

        //----------------------------------------------------------

//-------------------------------------------------------------------

    }


    @OnClick(R.id.location_layout)
    public void location_linear_layout(View view) {


        LayoutInflater li = LayoutInflater.from(ReportFormActivity.this);
        View promptsView = li.inflate(R.layout.location_option_dialogue, null);

        Button current_btn_location = (Button) promptsView.findViewById(R.id.current_location_btn);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportFormActivity.this);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        current_btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//nit
                if(!checkPermissions()){
                    Toast.makeText(getApplicationContext(), "Check GPS or Related Permission", Toast.LENGTH_SHORT).show();
                }else{
                    try {
                        getLastLocation();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error : "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                alertDialog.dismiss();
            }
        });


    }





    private  void IssueFormHit() {

        try {
            if((u1.getProgress().equals("Pre-construction") || u1.getProgress().equals("During Execution") ||u1.getProgress().equals("Final Completion"))   && u1.getStatus().equals("YES"))
            {
                formSubmit();
                updateStatus();
            }
            else
            {
                formSubmit();
            }

            Report_form();

            builder.setMessage("Successfully Submitted \n Application ID: "+appID)
                    .setCancelable(false)
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            finish();


                        }
                    })
                    .setNegativeButton("", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button


                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Confirmation");
            alert.show();


            //----------------------------------nain-------------neel


//
//       int socketTimeout = 30000;//30 seconds - change to what you want
//        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
//
//
//        AppController.getInstance().getRequestQueue().getCache().clear();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Error :"+e.getMessage(),Toast.LENGTH_SHORT).show();
        }


    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        //byte[] data = Base64.decode(encodedImage, Base64.DEFAULT);
        return encodedImage;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(ReportFormActivity.this, MainActivityHome.class);
        startActivity(intent);
    }


    @Override
    public void onResponse(Call<User> call, retrofit2.Response<User> response) {
        Log.d("INFO_NEEL","yo"+response.body());

    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Log.d("ERROR_INFO_NEEL","yo"+t);
    }


    void formSubmit()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface2.URL_REPORT)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface2 apiInterface = retrofit.create(ApiInterface2.class);

        // prepare call in Retrofit 2.0
        try {
            JSONObject paramObject = new JSONObject();


            paramObject.put("dptname", mpref.getCircle_concerned_officer_mob());
            paramObject.put("project",u1.getProject());
            paramObject.put("name", u1.getName());
            paramObject.put("userid", u1.getUserid());
            paramObject.put("mobile", u1.getMobile());
            paramObject.put("lat", u1.getLat());
            paramObject.put("log", u1.getLog());
            paramObject.put("images",u1.getImages());
            paramObject.put("timestamp", u1.getTimestamp());
            paramObject.put("reportid", u1.getReportid());
            paramObject.put("description", u1.getDescription());
            paramObject.put("progress",u1.getProgress());
            paramObject.put("status",u1.getStatus());
            paramObject.put("projectid",u1.getProjectid());


            Call<User> userCall = apiInterface.getUser(paramObject.toString());
            userCall.enqueue(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }


    void updateStatus()
    {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface2.URL_UPDATESTATUS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface2 apiInterface = retrofit.create(ApiInterface2.class);

        // prepare call in Retrofit 2.0
        try {
            JSONObject paramObject = new JSONObject();


            paramObject.put("project",u1.getProject());



            Call<User> userCall = apiInterface.updateStatus(paramObject.toString());
            userCall.enqueue(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }




    }




    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {


                            try
                            {

                                u1.setLat(String.valueOf(location.getLatitude()));
                                u1.setLog(String.valueOf(location.getLongitude()));
                       List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                      if (addresses != null && addresses.size() > 0) {
                            String address = addresses.get(0).getAddressLine(0)+" "+addresses.get(0).getAddressLine(1);
                            if (address != null) {
                                location_tv.setVisibility(View.VISIBLE);
                                location_tv.setText(address);
                                hideLoading();
                            } else {
                                location_tv.setVisibility(View.GONE);
                            }
                            System.out.println("Address >> " + address);
                            Toast.makeText(getApplicationContext(),""+location.getLatitude()+""+location.getLongitude()+"",Toast.LENGTH_SHORT).show();


                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }



                  //          latitudeTextView.setText(location.getLatitude() + "");
                     //       longitTextView.setText(location.getLongitude() + "");
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

public void run()
{
    getLastLocation();

}

}
