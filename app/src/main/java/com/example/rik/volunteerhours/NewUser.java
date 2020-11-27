package com.example.rik.volunteerhours;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class NewUser extends AppCompatActivity {

    Spinner gradeLevelSpinner;
    String baseUrl;
    String baseUrlUpdateProfile;
    RequestQueue requestQueue;
    EditText txtFirstName;
    EditText txtLastName;
    EditText txtStudentNumber;
    EditText txtUsernameReg;
    EditText txtPasswordReg;
    String username;
    HashMap<String, String> awardsCategories = new HashMap<>();
    EditText txtPasswordConfirm;
    Spinner awardProgram;
    String baseUrlGetAwards;
    Boolean bringValues;
    ArrayAdapter<String> adapter;
    ArrayAdapter<String> adapter2;
    Integer userID;
    String urlTemp;
    AESCrypt aesCrypt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_user);

        aesCrypt = new AESCrypt();

        urlTemp = getString(R.string.localhostURL);
        baseUrl = urlTemp + "register/";
        baseUrlUpdateProfile = urlTemp + "updateProfile/";
        baseUrlGetAwards = urlTemp + "getAwards";

        //receive intent and parse values
        Intent receiveRegister = getIntent();

        Bundle bringValuesBundle = receiveRegister.getExtras();

        bringValues = bringValuesBundle.getBoolean("Bring Values", true);
        username = bringValuesBundle.getString("Username");


        gradeLevelSpinner = findViewById(R.id.gradeLevel);
        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtStudentNumber = findViewById(R.id.txtStudentNumber);
        txtUsernameReg = findViewById(R.id.txtUsernameReg);
        txtPasswordReg = findViewById(R.id.txtPasswordReg);
        txtPasswordConfirm = findViewById(R.id.txtPasswordConfirm);

        requestQueue = Volley.newRequestQueue(this);

        //get the values for the dropdown
        new myAsyncTaskGetAwards().execute(baseUrlGetAwards);



        // Spinner Drop down elements
        List<String> categories = new ArrayList();
        categories.add("Please Select");
        categories.add("9");
        categories.add("10");
        categories.add("11");
        categories.add("12");

        adapter = new ArrayAdapter<>(NewUser.this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        gradeLevelSpinner.setAdapter(adapter);


        //if I came from editProfile, then call another API to get other values, else resume
        if(bringValues) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable(){
                public void run(){
                    String final_url = urlTemp + "getUser/" + username + "/" + "NA" + "/" + true;
                    new myAsyncTaskGetUserInfo().execute(final_url);
                }
            }, 1000);



        }



    }

    public void registerNewUser(View view) throws Exception {

        //get and check the values of every field
        //if everything is filled in, call API to enter values
        String strFirstName = String.valueOf(txtFirstName.getText());
        String strLastName = String.valueOf(txtLastName.getText());
        String strGradeLevel = gradeLevelSpinner.getSelectedItem().toString();
        String strStudentNum = txtStudentNumber.getText().toString();
        String strRegisterUsername = String.valueOf(txtUsernameReg.getText());
        String strRegisterPassword = String.valueOf(txtPasswordReg.getText());
        String strPasswordConfirm = String.valueOf(txtPasswordConfirm.getText());
        String strAwardCat = awardProgram.getSelectedItem().toString();

        if(!strFirstName.equals("")) {
            if(!strLastName.equals("")) {
                if(!strGradeLevel.equals("Please Select")) {
                    if(!strStudentNum.equals("")) {
                        if(!strRegisterUsername.equals("")) {
                            if(!strRegisterPassword.equals("")) {
                                if(!strPasswordConfirm.equals("")) {
                                    if(!strAwardCat.equals("Please Select")) {
                                        if(strRegisterPassword.equals(strPasswordConfirm)) {
                                            String strCatID = awardsCategories.get(strAwardCat);
                                            String strEncryptedPassword = aesCrypt.encrypt(strRegisterPassword);
                                            if(bringValues) {
                                                String final_url = baseUrlUpdateProfile + strFirstName + "/" + strLastName + "/" +
                                                        strRegisterUsername + "/" + strEncryptedPassword + "/" +
                                                        strGradeLevel + "/" + strStudentNum + "/" + strCatID + "/" + userID.toString();

                                                new myAsyncTaskUpdateProfile().execute(final_url);

                                            } else {
                                                String final_url = baseUrl + strFirstName + "/" + strLastName + "/" +
                                                        strRegisterUsername + "/" + strEncryptedPassword + "/" +
                                                        strGradeLevel + "/" + strStudentNum + "/" + strCatID;

                                                new myAsyncTaskRegister().execute(final_url);
                                            }

                                        } else {

                                            Toast.makeText(NewUser.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(NewUser.this, "Please select an award category", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(NewUser.this, "Please confirm your password", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(NewUser.this, "Please enter a password", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(NewUser.this, "Please enter a username", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(NewUser.this, "Please enter a student number", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(NewUser.this, "Please select a grade", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(NewUser.this, "Please enter a last name", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(NewUser.this, "Please enter a first name", Toast.LENGTH_LONG).show();
        }

}
    public void backToLogin(View view) {
        finish();
    }

    public class myAsyncTaskRegister extends AsyncTask<String, Void, String> {
        //this calls API to register user
        @Override
        protected String doInBackground(String... url) {
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url[0], (JSONObject) null, listener(), errorListener());
            requestQueue.add(arrReq);

            String done = "";
            return done;
        }
        protected void onPostExecute(String done) {

        }

        private Response.Listener<JSONObject> listener(){
            return new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("Message");

                        Toast.makeText(NewUser.this, result, Toast.LENGTH_LONG).show();

                        if(result.equals("SUCCESS")) {
                            finish();
                            Toast.makeText(NewUser.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(NewUser.this, "Username already exists", Toast.LENGTH_LONG).show();
                        }


                    } catch (JSONException e) {
                        Toast.makeText(NewUser.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(NewUser.this, "Unable to register.", Toast.LENGTH_LONG).show();
                }
            };
        }
    }

    public class myAsyncTaskUpdateProfile extends AsyncTask<String, Void, String> {
        //this api is ONLY for updating the user
        @Override
        protected String doInBackground(String... url) {
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url[0], (JSONObject) null, listener(), errorListener());
            requestQueue.add(arrReq);

            String done = "";
            return done;
        }
        protected void onPostExecute(String done) {

        }

        private Response.Listener<JSONObject> listener(){
            return new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        String result = response.getString("Message");

                        if(result.equals("SUCCESS")) {
                            finish();
                            Toast.makeText(NewUser.this, "Profile has been updated successfully", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(NewUser.this, "Username already exists", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(NewUser.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(NewUser.this, "Unable to register.", Toast.LENGTH_LONG).show();
                }
            };
        }
    }

    public class myAsyncTaskGetAwards extends AsyncTask<String, Void, String> {
        //this is for getting the values for the awards dropdown
        @Override
        protected String doInBackground(String... url) {
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url[0], (JSONObject) null, listener(), errorListener());
            requestQueue.add(arrReq);

            String done = "";
            return done;
        }
        protected void onPostExecute(String done){
        }

        private Response.Listener<JSONObject> listener(){
            return new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = response;
                        JSONObject awardsJSON = (JSONObject) jsonObject.get("categories");

                        setSpinners(awardsJSON);

                    } catch (JSONException e) {
                        Toast.makeText(NewUser.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEY", error.getMessage());
                }
            };
        }

        public void setSpinners(JSONObject awards) throws JSONException {

            //setting the spinners after getting the values
            ArrayList<String> itemArray = new ArrayList<>();
            itemArray.add("Please Select");

            Iterator keys = awards.keys();

            while(keys.hasNext()) {
                String key = keys.next().toString();

                String awardCat = awards.get(key).toString();

                awardsCategories.put(awardCat, key);
                itemArray.add(awardCat);
            }

            awardProgram = findViewById(R.id.awardProgram);

            adapter2 = new ArrayAdapter<>(NewUser.this,
                    android.R.layout.simple_spinner_item, itemArray);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            awardProgram.setAdapter(adapter2);
        }
    }

    public class myAsyncTaskGetUserInfo extends AsyncTask<String, Void, String> {

        //getting previous values if necessary
        @Override
        protected String doInBackground(String... url) {
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url[0], (JSONObject) null, listener(), errorListener());
            requestQueue.add(arrReq);

            String done = "";
            return done;
        }

        protected void onPostExecute(String done){
        }

        private Response.Listener<JSONObject> listener(){
            return new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = response;
                        String error = jsonObject.get("Error").toString();

                        if (error.equals("")) {
                            String jsonUsername = jsonObject.get("Username").toString();
                            String password = jsonObject.get("Password").toString();
                            String firstName = jsonObject.get("First name").toString();
                            String lastName = jsonObject.get("Last name").toString();
                            Integer grade = Integer.valueOf(jsonObject.get("Grade").toString());
                            Integer studentNum = Integer.valueOf(jsonObject.get("Student Num").toString());
                            userID = Integer.valueOf(jsonObject.get("User_ID").toString());
                            String awardCategory = jsonObject.get("Award Category").toString();

                            String decryptedPassword = aesCrypt.decrypt(password);


                            setEditTexts(jsonUsername, firstName, lastName, grade, studentNum, decryptedPassword, awardCategory);
                        } else {
                            Toast.makeText(NewUser.this, error, Toast.LENGTH_LONG).show();
                        }



                    } catch (Exception e) {
                        Toast.makeText(NewUser.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(NewUser.this, "Unable to get data.", Toast.LENGTH_LONG).show();
                }
            };
        }
        private void setEditTexts (String jsonUsername, String firstName, String lastName,
                                      Integer gradeLevel, Integer studentNum, String password, String awardCategoryFinal) {

            //setting the GUI views to the previous information

            int gradeSpinnerPosition = adapter.getPosition(gradeLevel.toString());
            gradeLevelSpinner.setSelection(gradeSpinnerPosition);

            int awardSpinnerPosition = adapter2.getPosition(awardCategoryFinal);
            awardProgram.setSelection(awardSpinnerPosition);

            txtFirstName.setText(firstName);
            txtLastName.setText(lastName);
            txtStudentNumber.setText(studentNum.toString());
            txtUsernameReg.setText(jsonUsername);
            txtPasswordReg.setText(password);
            txtPasswordConfirm.setText(password);
        }
    }

}
