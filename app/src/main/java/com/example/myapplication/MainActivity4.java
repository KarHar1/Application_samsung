package com.example.myapplication;

import static com.example.myapplication.MainActivity.User_Key;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class MainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        TextView eatten = findViewById(R.id.xxxx2);
        TextView calories = findViewById(R.id.xxxx);
        TextView burned = findViewById(R.id.xxxx1);
        FrameLayout breakfast = findViewById(R.id.rectangle_2);
        Button settingsbutton = findViewById(R.id.settingsbttn);

        int age = sp.getInt("age" , 0);
        int weight = sp.getInt("weight", 0);
        int height = sp.getInt("height" , 0);
        boolean gender = sp.getBoolean("gender" , true);
        int goal_weight = sp.getInt("goalWeight" , 0);
        int gml = sp.getInt("gml" , 0);
        int exer = sp.getInt("exer" , 1);
        int days = sp.getInt("days" , 0);
        User user = new User(age ,height ,weight , gml , goal_weight ,exer , days , gender);

        user.calculateCalories();
        calories.setText(String.valueOf(user.daily_calories));

        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity4.this , Settings.class));
            }
        });
        breakfast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int click =0;
                click++;
                        if(click == 1){
                            replaceFragment(new BreakfastFragment());

                        }else if(click == 2){
                            closeFragment(new BreakfastFragment());
                        }
            }
        });





    }
    @Override
    public void onBackPressed() {

        if (false) {
            super.onBackPressed();
        } else {

        }
    }
    public void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.rectangle_2 , fragment);
        fragmentTransaction.commit();
    }
    public void closeFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

}
