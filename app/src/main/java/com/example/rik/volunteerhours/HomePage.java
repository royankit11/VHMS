package com.example.rik.volunteerhours;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

public class HomePage extends AppCompatActivity {

    TextView welcome;
    Integer userID;
    String username;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        welcome = findViewById(R.id.txtWelcome);

        //receive intent along with welcome user
        Intent receiveHome = getIntent();

        Bundle userInfo = receiveHome.getExtras();
        username = userInfo.getString("Username");
        String firstName = userInfo.getString("FirstName");
        userID = userInfo.getInt("UserID");
        welcome.setText("Hello " + firstName);
    }

    public void showHours(View view) {
        //go to show hours
        Intent goViewHours = new Intent(this, viewHours.class);
        Bundle bringUserID = new Bundle();
        bringUserID.putInt("UserID", userID);
        goViewHours.putExtras(bringUserID);
        startActivity(goViewHours);
    }

    public void goToEnter(View view) {
        //
        Intent goEnterHours = new Intent(this, EnterHours.class);

        Bundle userIDBundle = new Bundle();
        Boolean bringValues = false;
        userIDBundle.putBoolean("Bring Values", bringValues);
        userIDBundle.putInt("UserID", userID);
        goEnterHours.putExtras(userIDBundle);

        startActivity(goEnterHours);
    }

    public void backToLogin(View view) {
        finish();
    }

    public void toRegisterPage(View view) {
        Intent goRegister = new Intent(this, NewUser.class);

        Bundle bringValuesBundle = new Bundle();
        Boolean bringValues = true;
        bringValuesBundle.putBoolean("Bring Values", bringValues);
        bringValuesBundle.putString("Username", username);

        goRegister.putExtras(bringValuesBundle);

        startActivity(goRegister);
    }
}
