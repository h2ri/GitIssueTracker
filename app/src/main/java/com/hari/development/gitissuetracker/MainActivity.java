package com.hari.development.gitissuetracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private IssueTracker helper = IssueTracker.getInstance();
    private String API_URL = "https://api.github.com/repos/rails/rails/issues";
   //For testing purpose private String API_URL = "https://api.github.com/repositories/8514/issues?page=45";
    private JsonArrayRequest jsonArrayRequest = null;
    private Button button ;
    private EditText editText = null;

    List<Issue> IssueList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.repo);
        IssueList = new ArrayList<>();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText == null || editText.toString() == "Git Repository URL") {
                    //Do something

                    Toast.makeText(MainActivity.this, "Enter The repository address", Toast.LENGTH_LONG).show();
                } else {
                    if (isOnline()) {
                        Log.v("asd","InOnClick");
                        getIssueData();

                    } else
                        Toast.makeText(MainActivity.this, "Network isn't available", Toast.LENGTH_LONG).show();
                }
            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    String nextPage =null;
    DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'z'");

    private void getIssueData(){
        Log.v("asd","getIssueData");

        jsonArrayRequest = new JsonArrayRequest(API_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            //String state = jsonObject.getString("state");
                            Log.v("asd", "response.length()");
                            //Toast.makeText(MainActivity.this,response.length(),Toast.LENGTH_LONG).show();
                            /*for (int i = 0 ; i < response.length() ; i++ ){
                                JSONObject jsonObject = response.getJSONObject(i);
                                Issue issue = new Issue();
                                issue.setCreatedAt(formatter.parseDateTime(jsonObject.getString("created_at")));
                                IssueList.add(issue);

                            }*/

                            //Toast.makeText(MainActivity.this, state, Toast.LENGTH_LONG).show();
                        }catch(Exception e){
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
                }


        })
        {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {
                Log.v("asd","InParseNetworkResponse");
                Map<String,String> responseHeader = response.headers;
                String test = responseHeader.get("Link");
                nextPage = nextPageUrlString(test);

                return super.parseNetworkResponse(response);
            }

            @Override
            protected void deliverResponse(JSONArray response) {

                Log.v("asd","deliverResponse");
                super.deliverResponse(response);
                //Toast.makeText(MainActivity.this,nextPage,Toast.LENGTH_LONG).show();
                Log.v("asd",nextPage);
                if (!(API_URL.equals(nextPage)) && nextPage != null){
                    API_URL = nextPage;
                    getIssueData();
                }else{
                    displayList();
                }


            }
        };
        helper.add(jsonArrayRequest);
    }


    private void displayList(){

        for (Issue i:IssueList){
            Log.v("asd,",i.getCreatedAt().toString());

        }

    }
    private String nextPageUrlString(String test){
        //To get url string for next page
        String[] temp = test.split(",");
        String result = null;
        for(int i = temp.length-1 ; i>=0 ; i--)
        {
            if(temp[i].contains("next")) {
                String[] temp2 = temp[i].split(" ");
                result = temp2[0].replaceAll("[<>;]"," ");
                result.trim();
                return result;
            }
        }
        return null;
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }



}
