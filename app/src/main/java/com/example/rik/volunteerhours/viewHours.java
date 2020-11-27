package com.example.rik.volunteerhours;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class viewHours extends AppCompatActivity {

    DatePickerDialog pickerStart;
    DatePickerDialog pickerEnd;
    EditText dateStart;
    EditText dateEnd;
    Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
    int padding = 25;
    RequestQueue requestQueue;
    TableLayout tv;
    String baseUrl;
    String baseUrlDeleteRecord;
    RadioGroup selectGroup;
    LinearLayout linLayout;
    LinearLayout linLayoutButtons;
    TextView totalHoursDateRange;
    TextView totalHoursSoFar;
    TextView csaCat;
    TextView hoursForAward;
    String category;
    Integer intTotalHoursDateRange = 0;
    Integer intHoursForAward;
    Integer intTotalHoursSoFar;
    Button editRecord;
    Integer selectedID = -1;
    Button deleteRecord;
    Integer userID;
    //if samsung
    Integer radioButtonTopMargin = 50;
    Integer textViewSize = 18;
    //if emulator
    //Integer radioButtonTopMargin = 150;
    //Integer textViewSize = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_hours);
        dateStart = findViewById(R.id.dateStart);
        dateEnd = findViewById(R.id.dateEnd);

        String urlTemp = getString(R.string.localhostURL);
        baseUrl = urlTemp + "viewHours/";
        baseUrlDeleteRecord = urlTemp + "deleteRecord/";

        Intent receiveView = getIntent();

        Bundle bundleUserID = receiveView.getExtras();

        userID = bundleUserID.getInt("UserID");


        requestQueue = Volley.newRequestQueue(this);
        selectGroup = new RadioGroup(viewHours.this);
        selectGroup.setOrientation(RadioGroup.VERTICAL);
        linLayout = findViewById(R.id.linLayout);
        totalHoursDateRange = findViewById(R.id.totalHoursDateRange);
        totalHoursSoFar = findViewById(R.id.totalHoursSoFar);
        hoursForAward = findViewById(R.id.hoursForAward);
        csaCat = findViewById(R.id.csaCat);

        tv = findViewById(R.id.tableDisplay);
        tv.removeAllViewsInLayout();


    }

    public void openCalendarStart(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        pickerStart = new DatePickerDialog(viewHours.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateStart.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, year, month, day);
        pickerStart.show();
    }

    public void openCalendarEnd(View view) {
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        pickerEnd = new DatePickerDialog(viewHours.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateEnd.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            }
        }, year, month, day);
        pickerEnd.show();
    }

    public void backToHome(View view) {
        finish();
    }

    public void displayTable(View view) {
        selectGroup.clearCheck();

        String strStartDate = dateStart.getText().toString();
        String strEndDate = dateEnd.getText().toString();

        String[] arrDatePartsStart = strStartDate.split("/");
        String[] arrDatePartsEnd = strEndDate.split("/");

        if(strStartDate.equals("") || strEndDate.equals("")) {
            Toast.makeText(viewHours.this, "Please enter valid date range", Toast.LENGTH_LONG).show();
            return;
        }

        selectGroup.removeAllViews();
        linLayout.removeView(selectGroup);
        linLayout.removeView(linLayoutButtons);
        tv.removeAllViews();

        TableRow tr = new TableRow(viewHours.this);

        tr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));


        TextView blank = new TextView(viewHours.this);
        blank.setPadding(padding, 0, 0, 0);
        blank.setText(" ");
        blank.setGravity(Gravity.CENTER);
        tr.addView(blank);

        TextView b8 = new TextView(viewHours.this);
        b8.setPadding(padding, 0, 0, 0);
        b8.setText("Date");
        b8.setTextColor(Color.BLACK);
        b8.setBackgroundColor(Color.rgb(135, 206, 250));
        b8.setTextSize(textViewSize);
        b8.setTypeface(boldTypeface);
        tr.addView(b8);

        TextView b5 = new TextView(viewHours.this);
        b5.setPadding(padding, 0, 0, 0);
        b5.setText("Hours");
        b5.setTextColor(Color.BLACK);
        b5.setBackgroundColor(Color.rgb(135, 206, 250));
        b5.setTextSize(textViewSize);
        b5.setTypeface(boldTypeface);
        tr.addView(b5);

        TextView b6 = new TextView(viewHours.this);
        b6.setPadding(padding, 0, 0, 0);
        b6.setText("Location");
        b6.setTextColor(Color.BLACK);
        b6.setBackgroundColor(Color.rgb(135, 206, 250));
        b6.setTextSize(textViewSize);
        b6.setTypeface(boldTypeface);
        tr.addView(b6);

        tv.addView(tr);

        final View vline = new View(viewHours.this);
        vline.setLayoutParams(new
                TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 4));
        vline.setBackgroundColor(Color.BLUE);
        tv.addView(vline); // add line below heading

        String finalViewHoursUrl = baseUrl + arrDatePartsStart[0] + "/" + arrDatePartsStart[1] + "/" + arrDatePartsStart[2] + "/" +
                arrDatePartsEnd[0] + "/" + arrDatePartsEnd[1] + "/" + arrDatePartsEnd[2] + "/" + userID.toString();

        new myAsyncTaskShowHours().execute(finalViewHoursUrl);

    }

    public class myAsyncTaskShowHours extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            JsonArrayRequest arrReq = new JsonArrayRequest(url[0], listener(), errorListener());
            requestQueue.add(arrReq);

            String done = "";
            return done;
        }

        protected void onPostExecute(String done) {
        }

        private Response.Listener<JSONArray> listener() {
            return new Response.Listener<JSONArray>() {

                @Override
                public void onResponse(JSONArray response) {
                    try {
                        intTotalHoursDateRange = 0;
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject record = response.getJSONObject(i);

                            String error = record.getString("Error");

                            if (error.equals("")) {
                                String date = "";
                                String hours = "";
                                String location = "";
                                Integer id = null;


                                date = record.getString("Date");
                                hours = record.get("Hours").toString();

                                intTotalHoursDateRange = intTotalHoursDateRange + Integer.valueOf(hours);

                                location = record.getString("Location");
                                id = Integer.valueOf(record.get("ID").toString());
                                category = record.getString("Category");
                                intHoursForAward = record.getInt("HoursAward");
                                intTotalHoursSoFar = record.getInt("TotalHours");

                                makeNewRow(date, hours, location, id);
                            } else {
                                Toast.makeText(viewHours.this, error, Toast.LENGTH_LONG).show();
                                return;
                            }

                        }

                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                        params.setMargins(0, radioButtonTopMargin, 0, 0);
                        selectGroup.setLayoutParams(params);
                        linLayout.addView(selectGroup);
                        csaCat.setText("Category: " + category);
                        totalHoursDateRange.setText("Total Hours In Date Range: " + intTotalHoursDateRange.toString());
                        hoursForAward.setText("Hours Required For Award: " + intHoursForAward.toString());
                        totalHoursSoFar.setText("Total Hours So Far: " + intTotalHoursSoFar.toString());

                        editRecord = new Button(viewHours.this);
                        editRecord.setText("Edit");
                        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        btnParams.setMargins(50, 0, 0, 0);
                        editRecord.setLayoutParams(btnParams);
                        editRecord.setTextSize(25);
                        editRecord.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selectedID = -1;
                                selectedID = selectGroup.getCheckedRadioButtonId();

                                if(selectedID > -1) {
                                    Intent goEdit = new Intent(viewHours.this, EnterHours.class);

                                    Bundle bringValuesBundle = new Bundle();
                                    Boolean bringValues = true;
                                    bringValuesBundle.putBoolean("Bring Values", bringValues);
                                    bringValuesBundle.putInt("VolID", selectedID);

                                    goEdit.putExtras(bringValuesBundle);

                                    startActivity(goEdit);
                                } else {
                                    Toast.makeText(viewHours.this, "Please select a record to edit", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                        deleteRecord = new Button(viewHours.this);
                        deleteRecord.setText("Delete");
                        LinearLayout.LayoutParams btnParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        btnParams.setMargins(200, 0, 0, 0);
                        deleteRecord.setLayoutParams(btnParams2);
                        deleteRecord.setTextSize(25);
                        deleteRecord.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                selectedID = -1;
                                selectedID = selectGroup.getCheckedRadioButtonId();

                                if(selectedID > -1) {
                                    String finalUrl = baseUrlDeleteRecord + selectedID.toString();

                                    new myAsyncTaskDeleteRecord().execute(finalUrl);
                                } else {
                                    Toast.makeText(viewHours.this, "Please select a record to delete", Toast.LENGTH_LONG).show();
                                }

                            }
                        });

                        linLayoutButtons = new LinearLayout(viewHours.this);
                        linLayoutButtons.setOrientation(LinearLayout.HORIZONTAL);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        linLayoutButtons.setLayoutParams(layoutParams);

                        linLayoutButtons.addView(editRecord);
                        linLayoutButtons.addView(deleteRecord);
                        linLayout.addView(linLayoutButtons);

                    } catch (JSONException e) {
                        Toast.makeText(viewHours.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }

                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(viewHours.this, "Unable to get data.", Toast.LENGTH_LONG).show();
                }
            };
        }

        public void makeNewRow(String date, String hours, String location, Integer id) {
            TableRow tr = new TableRow(viewHours.this);

            tr.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.WRAP_CONTENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            RadioButton radioButton1 = new RadioButton(viewHours.this);
            radioButton1.setId(id);
            selectGroup.addView(radioButton1);

            TextView blank = new TextView(viewHours.this);
            blank.setPadding(padding, 0, 0, 0);
            blank.setText("      ");
            blank.setGravity(Gravity.CENTER);
            tr.addView(blank);


            TextView txtDate = new TextView(viewHours.this);
            txtDate.setPadding(padding, 0, 0, 0);
            txtDate.setText(date);
            txtDate.setGravity(Gravity.CENTER);
            txtDate.setTextColor(Color.RED);
            txtDate.setBackgroundColor(Color.WHITE);
            txtDate.setTextSize(textViewSize);
            tr.addView(txtDate);

            TextView txtHours = new TextView(viewHours.this);
            txtHours.setPadding(padding, 0, 0, 0);
            txtHours.setText(hours);
            txtHours.setTextColor(Color.BLUE);
            txtHours.setBackgroundColor(Color.WHITE);
            txtHours.setTextSize(textViewSize);
            tr.addView(txtHours);

            TextView txtLocation = new TextView(viewHours.this);
            txtLocation.setPadding(padding, 0, 50, 0);
            txtLocation.setText(location);
            txtLocation.setTextColor(Color.RED);
            txtLocation.setBackgroundColor(Color.WHITE);
            txtLocation.setTextSize(textViewSize);
            tr.addView(txtLocation);


            //add table row to table layout
            if (tr != null) {
                tv.addView(tr);
            }


            final View blankSpace = new View(viewHours.this);
            blankSpace.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 5));
            blankSpace.setBackgroundColor(Color.WHITE);
            tv.addView(blankSpace);

            final View vline1 = new View(viewHours.this);
            vline1.setLayoutParams(new
                    TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, 3));
            vline1.setBackgroundColor(Color.BLACK);
            tv.addView(vline1);  // add line below each row

        }
    }

    public class myAsyncTaskDeleteRecord extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            JsonObjectRequest arrReq = new JsonObjectRequest(Request.Method.GET, url[0], (JSONObject) null, listener(), errorListener());
            requestQueue.add(arrReq);

            String done = "";
            return done;
        }

        protected void onPostExecute(String done) {
        }

        private Response.Listener<JSONObject> listener() {
            return new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject jsonObject = response;
                        String result = jsonObject.getString("Message");

                        if(result.equals("SUCCESS")){
                            Toast.makeText(viewHours.this, "Record has been deleted successfully", Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        Toast.makeText(viewHours.this, "Unable to connect to database.", Toast.LENGTH_LONG).show();

                    }


                }
            };
        }

        private Response.ErrorListener errorListener() {
            return new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(viewHours.this, "Unable to delete record.", Toast.LENGTH_LONG).show();
                }
            };
        }
    }

}

