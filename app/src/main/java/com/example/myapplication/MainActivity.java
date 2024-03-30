package com.example.myapplication;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String FIRST_TIME_KEY = "first_time";
    public static final String User_Key = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences userPref = getSharedPreferences("UserInfo" , MODE_PRIVATE);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);



        if (prefs.getBoolean(FIRST_TIME_KEY, true)) {

            setContentView(R.layout.activity_main);

            EditText nameEditText = findViewById(R.id.edittext_1);
            EditText ageEditText = findViewById(R.id.edittext_2);
            EditText weightEditText = findViewById(R.id.edittext_3);
            EditText heightEditText = findViewById(R.id.edittext_4);
            EditText genderEditText = findViewById(R.id.edittext_5);
            Button nextButton = findViewById(R.id.button);




                 nextButton.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         if(!(TextUtils.isEmpty(nameEditText.getText()) || TextUtils.isEmpty(weightEditText.getText()) || TextUtils.isEmpty(ageEditText.getText()) || TextUtils.isEmpty(heightEditText.getText()) ||TextUtils.isEmpty(genderEditText.getText()))) {


                             SharedPreferences.Editor editor1 = userPref.edit();
                             editor1.putString("name", nameEditText.getText().toString());
                             editor1.putInt("age", Integer.parseInt(ageEditText.getText().toString()));
                             editor1.putInt("weight", Integer.parseInt(weightEditText.getText().toString()));
                             editor1.putInt("height", Integer.parseInt(heightEditText.getText().toString()));
                             editor1.putBoolean("gender", Boolean.parseBoolean(genderEditText.getText().toString()));
                             editor1.commit();


                             SharedPreferences.Editor editor = prefs.edit();
                             editor.putBoolean(FIRST_TIME_KEY, false);
                             editor.apply();


                             startActivity(new Intent(MainActivity.this, MainActivity2.class));
                             finish();
                         }else{
                             Toast.makeText(MainActivity.this , "Fill all Fiealds" , Toast.LENGTH_LONG).show();
                         }

                     }
                 });


        } else {
            Intent intent = new Intent(MainActivity.this, MainActivity4.class);
            startActivity(intent);
            finish();
        }


    }
    @Override
    public void onBackPressed() {

        if (false) {
            super.onBackPressed();
        } else {

        }
    }

}

