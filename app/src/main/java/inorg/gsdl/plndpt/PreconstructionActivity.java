package inorg.gsdl.plndpt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.plndpt.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PreconstructionActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    RecyclerView recyclerView;
    MyListAdapter adapter;
    RequestQueue requestQueue;
    List<User> myListData = new ArrayList<>();
    String[] abc = {"india", "UK", "US"};
    ProgressBar progressBar;
    private Sharedpreferences mpref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preconstruction);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressbar);

        mpref = Sharedpreferences.getUserDataObj(this);
        requestQueue = Volley.newRequestQueue(this);
        String URL_Officer = "http://map.gsdl.org.in:8080/planningdpt/viewReports/Pre-construction";


        JsonArrayRequest requestOfficer = new JsonArrayRequest(Request.Method.GET, URL_Officer,
                null, response -> {
                    try {
                        for (int ii = 0; ii < response.length(); ii++) {

                            JSONObject jsonObject = response.getJSONObject(ii);
                            String d = jsonObject.getString("reportid");
                            String e = jsonObject.getString("timestamp");
                            String i = jsonObject.getString("project");


                            System.out.print("yoyo" + d);

                            mpref.set_user_id(d);
                            mpref.setImages(i);

                            User u = new User(i, e, d);


                            myListData.add(u);


                            Log.d("info", "" + myListData);

                            for (User ss : myListData) {
                                Log.d("datadata", "" + ss.getProject() + " " + ss.getTimestamp());
                            }

                        }

                        getDataFromList(myListData);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }, error -> Log.d("error", error.getMessage()));
        requestQueue.add(requestOfficer);


        //    myListData = new User[]{
        //          new User("abc","xyz")};


    }

    private void getDataFromList(List<User> users) {

        Log.d("method getdata", "" + users);
        adapter = new MyListAdapter(users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }
}