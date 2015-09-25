package com.hari.development.gitissuetracker;

import android.content.Context;
import android.graphics.Bitmap;
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
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private IssueTracker helper = IssueTracker.getInstance();
    private String API_URL = "https://api.github.com/repos/rails/rails/issues";
   //For testing purpose private String API_URL = "https://api.github.com/repositories/8514/issues?page=45";
    private JsonArrayRequest jsonArrayRequest = null;
    private Button button ;
    private EditText editText = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.repo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText == null || editText.toString() == "Git Repository URL") {
                    //Do something

                    Toast.makeText(MainActivity.this, "Enter The repository address", Toast.LENGTH_LONG).show();
                } else {
                    if (isOnline()) {
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

    private void getIssueData(){

        jsonArrayRequest = new JsonArrayRequest(API_URL,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(0);
                            String state = jsonObject.getString("state");

                            //Toast.makeText(MainActivity.this, state, Toast.LENGTH_LONG).show();
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                }


        })
        {
            @Override
            protected Response<JSONArray> parseNetworkResponse(NetworkResponse response) {

                Map<String,String> responseHeader = response.headers;
                String test = responseHeader.get("Link");
                nextPage = nextPageUrlString(test);

                return super.parseNetworkResponse(response);
            }

            @Override
            protected void deliverResponse(JSONArray response) {
                int count = 0;
                super.deliverResponse(response);
                //Toast.makeText(MainActivity.this,nextPage,Toast.LENGTH_LONG).show();

                if (!(API_URL.equals(nextPage)) && nextPage != null){
                    API_URL = nextPage;
                    getIssueData();
                }
                Log.v("asd",API_URL);

            }
        };
        helper.add(jsonArrayRequest);
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
