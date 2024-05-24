package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity3 extends AppCompatActivity {

    int gml = 0;
  String email;
    Intent intent;
    FirebaseFirestore db;
    int days, exer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
         db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
         email = prefs.getString("email" , "");
        DocumentReference docRef = db.collection("users").document(email);

        CalendarView timePeriod = findViewById(R.id.editTextDate);
        Button next = findViewById(R.id.next2);
        RadioGroup exersizeLVL = findViewById(R.id.radioExer);

        exersizeLVL.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton bttn = findViewById(checkedId);
                switch (bttn.getText().toString()) {
                    case "Sedentary active":
                        exer = 1;
                        break;
                    case "Lightly Active":
                        exer = 2;
                        break;
                    case "Moderatly Active":
                        exer = 3;
                        break;
                    case "Very active":
                        exer = 4;
                        break;
                }
            }
        });

        long millisecondsInTwoMonths = 60L * 24 * 60 * 60 * 1000;

        timePeriod.setMinDate(System.currentTimeMillis() + millisecondsInTwoMonths);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePeriod.getDate() != 0) {
                    days = (int) calDayDiff(timePeriod);
                    saveDataToFirestore(days, exer);
                } else {
                    Toast.makeText(MainActivity3.this, "Fill all Fields", Toast.LENGTH_LONG).show();
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

    private void saveDataToFirestore(int days, int exer) {
        Map<String, Object> user = new HashMap<>();
        user.put("days", days);
        user.put("exer", exer);

        db.collection("users")
                .document(email)
                .update(user)
                .addOnSuccessListener(aVoid -> {
                    Intent intent2 = new Intent(MainActivity3.this, MainActivity6.class);
                    startActivity(intent2);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(MainActivity3.this, "Error saving data", Toast.LENGTH_SHORT).show();
                });
    }
}
