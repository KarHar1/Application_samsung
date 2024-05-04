package com.example.demeter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.auth.FirebaseAuth;

public class Settings extends AppCompatActivity {
    private int clickCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText name = findViewById(R.id.text_name);
        EditText age = findViewById(R.id.text_age);
        EditText weight = findViewById(R.id.text_weight);
        EditText height = findViewById(R.id.text_height);
        EditText goal = findViewById(R.id.gWeight);

        RadioGroup grp = findViewById(R.id.radio_group_goal);
        Button logout = findViewById(R.id.button_logout);
        Button edit = findViewById(R.id.editbutton);

        SharedPreferences sp = getApplicationContext().getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();

        // Load user data from SharedPreferences
        name.setText(sp.getString("name", ""));
        age.setText(String.valueOf(sp.getInt("age", 0)));
        weight.setText(String.valueOf(sp.getInt("weight", 0)));
        height.setText(String.valueOf(sp.getInt("height", 0)));
        goal.setText(String.valueOf(sp.getInt("goalWeight", 0)));



        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final int[] gml = {0};
                clickCount++;

                if (clickCount == 1) {
                    // Enable editing
                    age.setEnabled(true);
                    name.setEnabled(true);
                    weight.setEnabled(true);
                    height.setEnabled(true);
                    goal.setEnabled(true);
                } else if (clickCount == 2) {
                    // Disable editing
                    age.setEnabled(false);
                    name.setEnabled(false);
                    weight.setEnabled(false);
                    height.setEnabled(false);
                    goal.setEnabled(false);
                    grp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            RadioButton radio = findViewById(i);

                            switch (radio.getText().toString()) {
                                case "Weight Loss":
                                    gml[0] = (1);
                                    goal.setEnabled(true);
                                    break;
                                case "Weight Maintenance":
                                    gml[0] = (2);
                                    goal.setText(String.valueOf(weight)); // Clear the text
                                    goal.setEnabled(false);
                                    break;
                                case "Weight Gain":
                                    gml[0] = (3);
                                    goal.setEnabled(true);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

                    // Save changes to SharedPreferences
                    ed.putString("name", name.getText().toString());
                    ed.putInt("age", Integer.parseInt(age.getText().toString()));
                    ed.putInt("weight", Integer.parseInt(weight.getText().toString()));
                    ed.putInt("height", Integer.parseInt(height.getText().toString()));
                    ed.putInt("goalWeight", Integer.parseInt(goal.getText().toString()));
                    ed.putInt("gml", gml[0]);
                    ed.commit(); // Apply changes

                    clickCount = 0;
                }
            }
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(Settings.this, SignUpActivity.class));
                finish(); // Finish current activity
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(true) {
            startActivity(new Intent(Settings.this, MainActivity4.class));
        }else{super.onBackPressed();}
    }
}
