package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {
    int gml = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        EditText goal_weight = findViewById(R.id.edittext_5);

        RadioGroup radios = findViewById(R.id.radioGoals);


        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        int userWeight = sp.getInt("weight", 0);


        SharedPreferences.Editor  editor= sp.edit();

        radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radio = findViewById(i);

                switch (radio.getText().toString()) {
                    case "Weight Loss":
                        gml = (1);
                        goal_weight.setEnabled(true);
                        break;
                    case "Weight Maintenance":
                        gml = (2);
                        goal_weight.setText(String.valueOf(userWeight)); // Clear the text
                        goal_weight.setEnabled(false);
                        break;
                    case "Weight Gain":
                        gml = (3);
                        goal_weight.setEnabled(true);
                        break;
                    default:
                        break;
                }
            }
        });



        Button next1 = findViewById(R.id.next2);
        next1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!(gml== 2 && TextUtils.isEmpty(goal_weight.getText()))){
                    if(gml==1 && Integer.parseInt(goal_weight.getText().toString())>userWeight) {
                        Toast.makeText(MainActivity2.this , "Weight should be smaller than goalweight" , Toast.LENGTH_LONG).show();
                    }else if(gml==3 && Integer.parseInt(goal_weight.getText().toString())<userWeight){
                        Toast.makeText(MainActivity2.this , "Weight should be bigger than goalweight" , Toast.LENGTH_LONG).show();
                    }else {
                        editor.putInt("goalWeight", Integer.parseInt(goal_weight.getText().toString()));
                        editor.putInt("gml", gml);
                        editor.commit();
                        startActivity(new Intent(MainActivity2.this, MainActivity3.class));
                        finish();
                    }
                }else if(TextUtils.isEmpty(goal_weight.getText())){
                    Toast.makeText(MainActivity2.this , "Fill all Fiealds" , Toast.LENGTH_LONG).show();
                }else{
                    editor.putInt("goalWeight" , Integer.parseInt(goal_weight.getText().toString()));
                    editor.putInt("gml" , gml);
                    editor.commit();
                    startActivity(new Intent(MainActivity2.this, MainActivity3.class));
                    finish();
                }
            }
        });
    }


}

