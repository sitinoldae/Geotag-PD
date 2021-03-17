package inorg.gsdl.plndpt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.StrictMode;
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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.plndpt.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ReportFormActivity extends AppCompatActivity implements Callback<User>, Runnable {

    public static User user = new User();
    boolean PHOTO_LOADED = false;
    private static String dateString;

    FusedLocationProviderClient fusedLocationProviderClient;

    String[] status = {"NO", "YES"};
    HashMap<String, String> hashMap;
    AlertDialog.Builder builder;
    @BindView(R.id.issue_image)
    ImageView ivReportImage;
    @BindView(R.id.location_addr)
    TextView location_tv;
    @BindView(R.id.tv_progress)
    TextView tv_progress;
    RequestQueue requestQueue2;
    FusedLocationProviderClient mFusedLocationClient;

    int PERMISSION_ID = 44;
    String appID;
    EditText etDescription;
    long date;
    Spinner spinnerStage;
    private View progressbarLayout;
    private Spinner projectNameSpinner, spinnerProjectCompleted;
    private AlertDialog AlertDialogImageChooser;
    private Geocoder geocoder;
    private Sharedpreferences mpref;
    private ApiInterface repost_form_interface;
    private ApiInterface2 repost_form_interface2;
/*    private Bitmap mphoto_bitmap, gallery_bitmap;
    private String imageString;
    private String my_value;*/
    private RequestQueue requestQueue;
    EasyImage easyImage;
    private File usableImageFile;
    private boolean ImageLoaded=false;
    private boolean ImageUploaded=false;
    private ProgressBar masterProgressBar;
    private String USABLE_IMAGE_DOWNLOAD_LINK="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_form);
        setUIRef();
        generateAppId();
        masterProgressBar = findViewById(R.id.progressbarReport);
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Nammu.askForPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, new PermissionCallback() {
                @Override
                public void permissionGranted() {
                    //Nothing, this sample saves to Public gallery so it needs permission
                }

                @Override
                public void permissionRefused() {
                    finish();
                }
            });
        }
        EasyImage.configuration(ReportFormActivity.this).setImagesFolderName("GeoTags")
                .setCopyTakenPhotosToPublicGalleryAppFolder(true)
                .setCopyPickedImagesToPublicGalleryAppFolder(true)
                .setAllowMultiplePickInGallery(false);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        showLoading();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(ReportFormActivity.this);
        spinnerStage = findViewById(R.id.spinnerStage);
        spinnerProjectCompleted = findViewById(R.id.spinnerProjectCompleted);

        mpref = Sharedpreferences.getUserDataObj(this);

        ArrayAdapter projectStatusArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, status);
        projectStatusArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerProjectCompleted.setAdapter(projectStatusArrayAdapter);
        projectNameSpinner = findViewById(R.id.projectSpinner);
        etDescription = findViewById(R.id.etDescription);

        final String DATA_URL = "http://map.gsdl.org.in:8080/planningdpt/viewpro/" + mpref.getCircle_concerned_officer_mob();
        Log.d("url main", DATA_URL);

        getprojectname(DATA_URL);
        EditText etName, etEmail, etMobile;
        builder = new AlertDialog.Builder(this);
        user = new User();
        if (android.os.Build.VERSION.SDK_INT > 19) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        requestQueue = Volley.newRequestQueue(this);
        ButterKnife.bind(this);
        etName = findViewById(R.id.etName);
        etMobile = findViewById(R.id.etMobile);
        etEmail = findViewById(R.id.etEmail);


        etName.setText(mpref.get_user_name_verif());
        user.setName(mpref.get_user_name_verif());


        etMobile.setText(mpref.get_User_Mobile_verif());
        user.setMobile(mpref.get_User_Mobile_verif());


        etEmail.setText(mpref.get_user_email_verif());
        user.setUserid(mpref.get_user_email_verif());

        user.setLat(mpref.get_search_latitude());
        user.setLog(mpref.get_search_Longitude());


        repost_form_interface = ApiClient.getClient().create(ApiInterface.class);
        repost_form_interface2 = ApiClient2.getClient().create(ApiInterface2.class);
        geocoder = new Geocoder(this, Locale.getDefault());
        date = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        dateString = sdf.format(date);
        Log.d("date_ccc", dateString);
        user.setTimestamp(dateString);
        ivReportImage.setScaleType(ImageView.ScaleType.FIT_XY);

        if (getIntent().getStringExtra("search_text") != null) {
            location_tv.setVisibility(View.VISIBLE);
            location_tv.setText(getIntent().getStringExtra("search_text"));
        } else {
            location_tv.setVisibility(View.GONE);
        }

        spinnerStage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                user.setProgress(spinnerStage.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerProjectCompleted.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                user.setStatus(spinnerProjectCompleted.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void generateAppId() {
        Random random = new Random();
        Calendar calender = Calendar.getInstance();
        appID = "UAP" + random.nextInt(1000) + calender.get(Calendar.YEAR) + "" + (calender.get(Calendar.MONTH) + 1) + "" + calender.get(Calendar.DATE) + "" +
                (calender.get(Calendar.HOUR)) + "" + calender.get(Calendar.MINUTE) + "" + calender.get(Calendar.SECOND);
        Toast.makeText(this, "Application Id generated: "+appID, Toast.LENGTH_SHORT).show();
    }

    private void setUIRef() {
        //Create a Instance of the Loading Layout
        progressbarLayout = findViewById(R.id.my_loading_layout);
    }

    private void showLoading() {
        /*Call this function when you want progress dialog to appear*/
        if (progressbarLayout != null) {
            progressbarLayout.setVisibility(View.VISIBLE);
        }
    }

    private void hideLoading() {
        /*Call this function when you want progress dialog to disappear*/
        if (progressbarLayout != null) {
            progressbarLayout.setVisibility(View.GONE);
        }
    }

    String getprojectname(String url) {

        JsonArrayRequest projectNameRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject j = null;
                        List<String> arrayList = new ArrayList<>();
                        hashMap = new HashMap<String, String>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String projectName = object.getString("projectname");
                                String projectid = object.getString("projectid");

                                System.out.print("projectname" + projectName);
                                hashMap.put(projectName, projectid);
                                arrayList.add(projectName);
                            }
                            projectNameSpinner.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList));
                        //    hideLoading();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("volley error : " + error.getMessage());
                    }
                });

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(projectNameRequest);

        projectNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                user.setProject(projectNameSpinner.getSelectedItem().toString());
                String project = projectNameSpinner.getSelectedItem().toString();

                getreportprojectcheck(project);

                for (Map.Entry<String, String> e : hashMap.entrySet()) {

                    if (project.equals(e.getKey())) {
                        user.setProjectid(e.getValue());
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


        final String DATA_URLALlPROJECT = "http://map.gsdl.org.in:8080/planningdpt/viewReportlimi`";
        JsonArrayRequest stringRequest2 = new JsonArrayRequest(DATA_URLALlPROJECT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        JSONObject j = null;
                        List<String> arrayList = new ArrayList<>();

                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject object = response.getJSONObject(i);
                                String projectname = object.getString("project");

                                System.out.print("project" + projectname);

                                arrayList.add(projectname);
                            }


                            if (!arrayList.contains(project)) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Pre-construction");
                                hideLoading();
                                spinnerStage.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));

                            } else {
                                checkprogressandstatus(project);
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

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue2.add(stringRequest2);

    }

    private void checkprogressandstatus(String project) {
        hideLoading();
        final String DATA_URLALlPROJECT = "http://map.gsdl.org.in:8080/planningdpt/viewReportlimi";
        JsonArrayRequest stringRequest2 = new JsonArrayRequest(DATA_URLALlPROJECT,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {

                        JSONObject j = null;
                        List<String> arrayList = new ArrayList<>();
                        HashMap<String, String> dataall = new HashMap<String, String>();
                        masterProgressBar.setMax(response.length()-1);
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                masterProgressBar.setProgress(i);
                                JSONObject object = response.getJSONObject(i);
                                String projectname = object.getString("project");
                                String status = object.getString("status");
                                String progress = object.getString("progress");
                                dataall.put("project", projectname);
                                dataall.put("status", status);
                                dataall.put("progress", progress);
                            }

                            if (dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Pre-construction") &&
                                    dataall.get("status").equalsIgnoreCase("NO")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Pre-construction");


                                spinnerStage.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            } else if (dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Pre-construction") &&
                                    dataall.get("status").equalsIgnoreCase("YES")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("During Execution");


                                spinnerStage.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            } else if (dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("During Execution") &&
                                    dataall.get("status").equalsIgnoreCase("NO")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("During Execution");


                                spinnerStage.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            } else if (dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("During Execution") &&
                                    dataall.get("status").equalsIgnoreCase("YES")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Final Completion");


                                spinnerStage.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            } else if (dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Final Completion") &&
                                    dataall.get("status").equalsIgnoreCase("NO")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Final Completion");


                                spinnerStage.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            } else if (dataall.get("project").equalsIgnoreCase(project) && dataall.get("progress").equalsIgnoreCase("Final Completion") &&
                                    dataall.get("status").equalsIgnoreCase("YES")) {

                                List<String> arrayList1 = new ArrayList<>();
                                arrayList1.add("Final Completion");


                                spinnerStage.setAdapter(new ArrayAdapter<String>(ReportFormActivity.this, android.R.layout.simple_spinner_dropdown_item, arrayList1));
                            }

                            } catch (JSONException e) {
                            System.out.println("Json Error:" + e.getMessage());
                            Toast.makeText(getApplicationContext(), "Json Error :"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Volley Error :" + error.getMessage());
                        Toast.makeText(getApplicationContext(), "Json Error :"+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue2.add(stringRequest2);


    }

    @OnClick(R.id.issue_photo_layout)
    public void photo_upload_option_linear(View view) {

        LayoutInflater li = LayoutInflater.from(ReportFormActivity.this);
        View promptsView = li.inflate(R.layout.camera_option_dialogue, null);

        Button button_camera = promptsView.findViewById(R.id.camera_btn);
        Button button_gallery = promptsView.findViewById(R.id.gallery_btn);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportFormActivity.this);
        alertDialogBuilder.setView(promptsView);
        AlertDialogImageChooser = alertDialogBuilder.create();
        AlertDialogImageChooser.show();

        button_camera.setOnClickListener(v -> {
            int permissionCheck = ContextCompat.checkSelfPermission(ReportFormActivity.this, Manifest.permission.CAMERA);
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                Nammu.askForPermission(ReportFormActivity.this, Manifest.permission.CAMERA, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        //Nothing, this sample saves to Public gallery so it needs permission
                        EasyImage.openCameraForImage(ReportFormActivity.this,0);
                    }
                    @Override
                    public void permissionRefused() {
                        Toast.makeText(ReportFormActivity.this, "Cannot take photos because camera permission was refused !", Toast.LENGTH_SHORT).show();
                    }
                });
            }else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        EasyImage.openCameraForImage(ReportFormActivity.this,0);
                    }
            if(AlertDialogImageChooser.isShowing()){
                AlertDialogImageChooser.dismiss();
            }
        });

        button_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EasyImage.openChooserWithGallery(ReportFormActivity.this,"Choose GeoTag Image",0);
                if(AlertDialogImageChooser.isShowing()){
                    AlertDialogImageChooser.dismiss();
                }
            }
        });
    }

   /* protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {

            case 0:
                if (resultCode == RESULT_OK) {
                    ivReportImage.setVisibility(View.VISIBLE);
                    mphoto_bitmap = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    Log.d("new_value__camera", String.valueOf(mphoto_bitmap));

                    ivReportImage.setImageBitmap(mphoto_bitmap);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    mphoto_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageBytes = baos.toByteArray();
                    imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
                    System.out.print("encode64_coder" + imageString);
                    if (imageString.isEmpty()) {

                    } else {
                        my_value = imageString;
                    }
                }
                break;
            case 1:
                System.out.println("yooo");
                if (resultCode == RESULT_OK && imageReturnedIntent != null && imageReturnedIntent.getData() != null) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    Log.d("nayi_value", String.valueOf(selectedImage));

                    ivReportImage.setVisibility(View.VISIBLE);
                    try {
                        gallery_bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        Log.d("new_value__", String.valueOf(gallery_bitmap));

                        ivReportImage.setImageBitmap(gallery_bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    my_value = getStringImage(gallery_bitmap);
                    System.out.print("honey" + my_value);
                }
                break;
        }
    }*/
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       super.onActivityResult(requestCode, resultCode, data);

       EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
           @Override
           public void onImagesPicked(@NonNull List<File> imageFiles, EasyImage.ImageSource source, int type) {
               Toast.makeText(getApplicationContext(), "The number of files returned : " + imageFiles.size(), Toast.LENGTH_SHORT).show();
               File ImageFile= null;
               File imagepath= null;
               try {
                   ImageFile = imageFiles.get(0);
                   imagepath = ImageFile.getParentFile();
                   usableImageFile = new File(imagepath,appID+".jpg");
                   ImageFile.renameTo(usableImageFile);
                   Toast.makeText(ReportFormActivity.this, "Image Renamed : "+usableImageFile.getPath(), Toast.LENGTH_SHORT).show();
               } catch (Exception e) {
                   e.printStackTrace();
               }
               Toast.makeText(ReportFormActivity.this, "File Info :\n"+usableImageFile.getPath()+"\n"+usableImageFile.getPath(), Toast.LENGTH_SHORT).show();
               try {
                   ivReportImage.setVisibility(View.VISIBLE);
                   ivReportImage.setImageBitmap(BitmapFactory.decodeFile(usableImageFile.getPath()));
                   Toast.makeText(ReportFormActivity.this, "Image loaded " + usableImageFile.getName(), Toast.LENGTH_SHORT).show();
                   ImageLoaded=true;
               } catch (Exception e) {
                   Toast.makeText(ReportFormActivity.this, "Error loading Image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                   e.printStackTrace();
               }
               if(ImageLoaded){
                 sendFileToUploadOnFirebase(usableImageFile);
               }
           }
       });
   }

    private void sendFileToUploadOnFirebase(File usableImageFile) {
        masterProgressBar.setIndeterminate(true);
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        Uri UsableFileUri = Uri.fromFile(usableImageFile);
        StorageReference uploadReference = storageRef.child("images/"+UsableFileUri.getLastPathSegment());
        UploadTask uploadTask = uploadReference.putFile(UsableFileUri);
        Toast.makeText(getApplicationContext(), "Uploading "+usableImageFile.getName()+" to firebase", Toast.LENGTH_SHORT).show();
// Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(getApplicationContext(), "Uploading "+usableImageFile.getName()+" to firebase Failed\n"+exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getApplicationContext(), "Upload "+usableImageFile.getName()+" to firebase Success !", Toast.LENGTH_SHORT).show();
                uploadReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Toast.makeText(getApplicationContext(),"Download url : "+uri.toString(),Toast.LENGTH_LONG).show();
                    USABLE_IMAGE_DOWNLOAD_LINK=uri.toString();
                    tv_progress.setText("Geotag Image Uploaded !");
                    ImageUploaded=true;
                });
            }
        }).addOnProgressListener(taskSnapshot -> {
            int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
            tv_progress.setText("UPLOADING "+progress+"%");
            masterProgressBar.setIndeterminate(false);
            masterProgressBar.setProgress(progress);
        });


    }


    @OnClick(R.id.btn_submit)
    public void submit_form(View view) {

        if(!USABLE_IMAGE_DOWNLOAD_LINK.matches("")){
            //image has been uploaded so we can continue
            try {
                user.setReportid(appID);
                user.setImages(USABLE_IMAGE_DOWNLOAD_LINK);
                String USER_DESCRIPTION = etDescription.getText().toString();
                user.setDescription(USER_DESCRIPTION);


                if (user.getLat().equals("0.0") || user.getLog().equals("0.0") || user.getLat().equals("") || user.getLog().equals("")) {
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Please check your internet connection or GPS setting");
                    alert.show();
                    return;
                }
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

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }else {
            Toast.makeText(getApplicationContext(),"Please Select ISSUE/GEOTAG image first",Toast.LENGTH_SHORT).show();
        }

    }


    @OnClick(R.id.location_layout)
    public void location_linear_layout(View view) {


        LayoutInflater li = LayoutInflater.from(ReportFormActivity.this);
        View promptsView = li.inflate(R.layout.location_option_dialogue, null);

        Button current_btn_location = promptsView.findViewById(R.id.current_location_btn);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportFormActivity.this);
        alertDialogBuilder.setView(promptsView);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        current_btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//nit
                if (!checkPermissions()) {
                    Toast.makeText(getApplicationContext(), "Check GPS or Related Permission", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        getLastLocation();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                alertDialog.dismiss();
            }
        });


    }

    private void IssueFormHit() {

        try {
            if ((user.getProgress().equals("Pre-construction") || user.getProgress().equals("During Execution") || user.getProgress().equals("Final Completion")) && user.getStatus().equals("YES")) {
                formSubmit();
                updateStatus();
            } else {
                formSubmit();
            }
            builder.setMessage("Successfully Submitted \n Application ID: " + appID)
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
            AlertDialog alert = builder.create();
            alert.setTitle("Confirmation");
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error :" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        //byte[] data = Base64.decode(encodedImage, Base64.DEFAULT);
        return encodedImage;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    @Override
    public void onResponse(Call<User> call, retrofit2.Response<User> response) {
        Log.d("INFO_NEEL", "yo" + response.body());

    }

    @Override
    public void onFailure(Call<User> call, Throwable t) {
        Log.d("ERROR_INFO_NEEL", "yo" + t);
    }

    void formSubmit() {

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
            paramObject.put("project", user.getProject());
            paramObject.put("name", user.getName());
            paramObject.put("userid", user.getUserid());
            paramObject.put("mobile", user.getMobile());
            paramObject.put("lat", user.getLat());
            paramObject.put("log", user.getLog());
            paramObject.put("images", user.getImages());
            paramObject.put("timestamp", user.getTimestamp());
            paramObject.put("reportid", user.getReportid());
            paramObject.put("description", user.getDescription());
            paramObject.put("progress", user.getProgress());
            paramObject.put("status", user.getStatus());
            paramObject.put("projectid", user.getProjectid());


            Call<User> userCall = apiInterface.getUser(paramObject.toString());
            userCall.enqueue(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    void updateStatus() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApiInterface2.URL_UPDATESTATUS)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface2 apiInterface = retrofit.create(ApiInterface2.class);

        try {
            JSONObject paramObject = new JSONObject();


            paramObject.put("project", user.getProject());

            Call<User> userCall = apiInterface.updateStatus(paramObject.toString());
            userCall.enqueue(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {

        if (checkPermissions()) {

            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            requestNewLocationData();
                            try {
                                user.setLat(String.valueOf(location.getLatitude()));
                                user.setLog(String.valueOf(location.getLongitude()));
                                List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                if (addresses != null && addresses.size() > 0) {
                                    String address = addresses.get(0).getAddressLine(0) + " ";
                                    if(addresses.get(0).getAddressLine(1)!=null) {
                                        String addLineTwo = addresses.get(0).getAddressLine(1);
                                        address=address+addLineTwo;
                                    }
                                    if (address != null) {
                                        location_tv.setVisibility(View.VISIBLE);
                                        location_tv.setText(address);
                                        hideLoading();
                                    } else {
                                        location_tv.setVisibility(View.GONE);
                                    }
                                    System.out.println("Address >> " + address);
                                    Toast.makeText(getApplicationContext(), "" + location.getLatitude() + "" + location.getLongitude() + "", Toast.LENGTH_SHORT).show();


                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        LocationCallback mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Toast.makeText(ReportFormActivity.this, locationResult.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
                Toast.makeText(ReportFormActivity.this, locationAvailability.toString(), Toast.LENGTH_SHORT).show();

            }
        };

        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA
        }, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

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
    public void onBackPressed() {
        requestQueue.stop();
        requestQueue2.stop();
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    public void run() {
        getLastLocation();

    }
}
