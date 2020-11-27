package com.example.rik.volunteerhours;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EnterHours extends AppCompatActivity {

    EditText location;
    EditText hours;
    EditText dateChosen;
    DatePickerDialog dateOfVol;
    String baseUrl;
    String updateRecordUrl;
    String getPreviousValuesUrl;
    RequestQueue requestQueue;
    Integer userID;
    Integer yearFinal;
    Integer monthFinal;
    Integer dayFinal;
    Boolean bringValues;
    Integer volID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_hours);

        String urlTemp = getString(R.string.localhostURL);
        baseUrl = urlTemp + "inputHours/";
        updateRecordUrl = urlTemp + "updateRecord/";
        getPreviousValuesUrl = urlTemp + "getRecord/";

        Intent receiveEnter = getIntent();

        Bundle userIDBundle = receiveEnter.getExtras();

        bringValues = userIDBundle.getBoolean("Bring Values", true);

        hours = findViewById(R.id.hoursChosen);
        dateChosen = findViewById(R.id.dateChosen);
        location = findViewById(R.id.location);

        requestQueue = Volley.newRequestQueue(this);

        if(bringValues) {
            volID = userIDBundle.getInt("VolID");

            String final_url = getPreviousValuesUrl + volID.toString();

            new myAsyncTaskGetRecord().execute(final_url);

        } else {
            userID = userIDBundle.getInt("UserID");
        }




    }

    public void selectDate(View view) {
        final Calendar cldr = Calendar.getInstance();
        final int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        dateOfVol = new DatePickerDialog(EnterHours.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateChosen.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
                yearFinal = year;
                monthFinal = monthOfYear + 1;
                dayFinal = dayOfMonth;
            }
        }, year, month, day);
        dateOfVol.show();
    }


    public void enterTheHours(View view) throws ParseException {
        String strLocation = String.valueOf(location.getText());
        String strDate = String.valueOf(dateChosen.getText());

        String strHours = String.valueOf(hours.getText());

        String strDateChosen = dateChosen.getText().toString();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date selectedDate = sdf.parse(strDateChosen);

        Boolean dateIsNotInFuture = new Date().after(selectedDate);

        if(!dateIsNotInFuture) {
            Toast.makeText(EnterHours.this, "Date cannot be in the future", Toast.LENGTH_LONG).show();
            return;
        }

        if(!strHours.equals("")) {
            if(!strDate.equals("")) {
                if(!strLocation.equals("")) {
                    if(bringValues) {
                        String[] arrDateParts = strDateChosen.split("/");

                        String final_url = updateRecordUrl + arrDateParts[0] + "/" + arrDateParts[1] +
                                "/" + arrDateParts[2] + "/" + hours.getText().toString() + "/" + location.getText().toString() + "/" +
                                volID.toString();

                        new myAsyncTaskUpdateRecord().execute(final_url);
                    } else {

                        String final_url = baseUrl + userID.toString() + "/" + monthFinal.toString() + "/" +
                                dayFinal.toString() + "/" + yearFinal.toString() + "/" + strHours + "/" + strLocation;
                        new myAsyncTaskInputHours().execute(final_url);
                        }
                } else {
                    Toast.makeText(EnterHours.this, "Provide a location", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(EnterHours.this, "Provide a date", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(EnterHours.this, "Enter a valid amount of hours", Toast.LENGTH_LONG).show();
        }

    }

    public void backToHome(View view) {
        finish();
    }


    public class myAsyncTaskInputHours extends AsyncTask<String, Void, String> {

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
                        String result = jsonObject.getString("Message");

                        if(result.equals("SUCCESS")){
                            Toast.makeText(EnterHours.this, "Data was successfully entered", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(EnterHours.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EnterHours.this, "Unable to save data.", Toast.LENGTH_LONG).show();
                }
            };
        }
    }

    public class myAsyncTaskUpdateRecord extends AsyncTask<String, Void, String> {

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
                        String result = jsonObject.getString("Message");

                        if(result.equals("SUCCESS")){
                            Toast.makeText(EnterHours.this, "Record has been updated successfully", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(EnterHours.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(EnterHours.this, "Unable to save data.", Toast.LENGTH_LONG).show();
                }
            };
        }
    }

    public class myAsyncTaskGetRecord extends AsyncTask<String, Void, String> {

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
                        String date = jsonObject.get("Date").toString();
                        String strHours = String.valueOf(jsonObject.getInt("Hours"));
                        String strLocation = jsonObject.get("Location").toString();

                        dateChosen.setText(date);
                        hours.setText(strHours);
                        location.setText(strLocation);

                    } catch (JSONException e) {
                        Toast.makeText(EnterHours.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener(){

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Volley", error.getMessage());
                }
            };
        }
    }
}
