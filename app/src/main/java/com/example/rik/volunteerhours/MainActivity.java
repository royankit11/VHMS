package com.example.rik.volunteerhours;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    EditText txtUsername;
    EditText txtPassword;
    String baseUrl;
    String strUsername;
    String strPassword;
    RequestQueue requestQueue;
    AESCrypt aesCrypt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        aesCrypt = new AESCrypt();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String urlTemp = getString(R.string.localhostURL);
        baseUrl = urlTemp + "getUser/";

        txtUsername = findViewById(R.id.txtUsername);
        txtPassword = findViewById(R.id.txtPassword);
        requestQueue = Volley.newRequestQueue(this);
    }

    public void goToRegister(View view) {
        //brings user to register page and does bring any prior values
        Intent goRegister = new Intent(this, NewUser.class);
        Boolean bringValues = false;
        Bundle bringValuesBundle = new Bundle();
        bringValuesBundle.putBoolean("Bring Values", bringValues);
        goRegister.putExtras(bringValuesBundle);
        startActivity(goRegister);
    }

    public void toHomePage(View view) throws Exception {
        //gets username and password
        strUsername = String.valueOf(txtUsername.getText());
        strPassword = String.valueOf(txtPassword.getText());

        //as long as both username and password are filled, go to AsyncTask
        if(!strUsername.equals("")) {
            if(!strPassword.equals("")) {

                String strEncryptedPassword = aesCrypt.encrypt(strPassword);

                String full_api_url = baseUrl + strUsername + "/" + strEncryptedPassword + "/" + false;
                new myAsyncTaskLogIn().execute(full_api_url);
            } else {
                Toast.makeText(MainActivity.this, "Provide password", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(MainActivity.this, "Provide username", Toast.LENGTH_LONG).show();
        }
    }

    public class myAsyncTaskLogIn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url[0], (JSONObject) null, listener(), errorListener());
            requestQueue.add(arrReq);

            String done = "Logging In...";
            return done;
        }

        protected void onPostExecute(String done){
            Toast.makeText(MainActivity.this, done, Toast.LENGTH_LONG).show();
        }

        private Response.Listener<JSONObject> listener(){
            return new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = response;
                        String error = jsonObject.get("Error").toString();

                        //if both the username and password match, then parse the other user information values
                        //and go to bringToHomePage function
                        if (error.equals("")) {
                            String jsonUsername = jsonObject.get("Username").toString();
                            String firstName = jsonObject.get("First name").toString();
                            String lastName = jsonObject.get("Last name").toString();
                            Integer grade = Integer.valueOf(jsonObject.get("Grade").toString());
                            Integer studentNum = Integer.valueOf(jsonObject.get("Student Num").toString());
                            Integer userID = Integer.valueOf(jsonObject.get("User_ID").toString());

                            bringToHomePage(jsonUsername, firstName, lastName, grade, studentNum, userID);
                        } else {
                            Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                        }



                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Unable to login.", Toast.LENGTH_LONG).show();
                }
            };
        }
        private void bringToHomePage (String jsonUsername, String firstName, String lastName, Integer gradeLevel, Integer studentNum, Integer userID) {
            //this function packs necessary values into a bundle and sends it to HomePage
            Intent goHome = new Intent(MainActivity.this, HomePage.class);
            Bundle userInfo = new Bundle();
            userInfo.putString("Username", jsonUsername);
            userInfo.putString("FirstName", firstName);
            userInfo.putInt("UserID", userID);
            goHome.putExtras(userInfo);

            Toast.makeText(MainActivity.this, "Successfully logged in!", Toast.LENGTH_LONG).show();

            startActivity(goHome);

        }
    }



}
