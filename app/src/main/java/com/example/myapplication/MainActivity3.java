package com.example.myapplication;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

public class MainActivity3 extends AppCompatActivity {

    Intent intent;
    SharedPreferences sp;
    int days , exer ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        CalendarView timePeriod = findViewById(R.id.editTextDate);
        Button next = findViewById(R.id.next2);
        RadioGroup exersizeLVL = findViewById(R.id.radioExer);

        sp = getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();




        exersizeLVL.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton bttn = findViewById(checkedId);
                switch(bttn.getText().toString()){
                    case "Sedentary active":
                        exer = 1 ;
                        break;
                    case "Lightly Active":
                        exer = 2 ;
                        break;
                    case "Moderatly Active":
                        exer = 3 ;
                        break;

                    case "Very active":
                        exer = 4 ;
                        break;


                }

            }
        });



        long millisecondsInTwoMonths = 60L * 24 * 60 * 60 * 1000;
        long millisecondsInOneWeek = 7L * 24 * 60 * 60 * 1000;
        timePeriod.setMaxDate(System.currentTimeMillis() + millisecondsInTwoMonths);
        timePeriod.setMinDate(System.currentTimeMillis() + millisecondsInOneWeek );

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(timePeriod.getDate()!= 0 ) {
                    days = (int) calDayDiff(timePeriod);
                    ed.putInt("days",days);
                    ed.putInt("exer" , exer);
                    ed.commit();
                    Intent intent2 = new Intent(MainActivity3.this, MainActivity4.class);
                    startActivity(intent2);

                }else{
                    Toast.makeText(MainActivity3.this , "Fill all Fiealds" , Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    long calDayDiff(CalendarView timePeriod) {
        long selectedDateMillis = timePeriod.getDate();
        long currentDateMillis = System.currentTimeMillis();

        long diff = selectedDateMillis - currentDateMillis;

       return diff / (1000 * 60 * 60 * 24);
    }




}
