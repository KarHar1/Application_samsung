package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity2 extends AppCompatActivity {

    int gml = 0;
    DocumentReference docRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = prefs.getString("email" , "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
         docRef = db.collection("users").document(email);

        EditText goal_weight = findViewById(R.id.edittext_5);
        RadioGroup radios = findViewById(R.id.radioGoals);

        radios.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radio = findViewById(i);

                switch (radio.getText().toString()) {
                    case "Weight Loss":
                        gml = 1;
                        goal_weight.setEnabled(true);
                        break;
                    case "Weight Maintenance":
                        gml = 2;
                        goal_weight.setEnabled(false);
                        break;
                    case "Weight Gain":
                        gml = 3;
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
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                int userWeight = document.getLong("weight").intValue();

                                if ((gml == 2) || !TextUtils.isEmpty(goal_weight.getText())) {
                                    if (gml == 1 && Integer.parseInt(goal_weight.getText().toString()) > userWeight) {
                                        Toast.makeText(MainActivity2.this, "Weight should be smaller than goal weight", Toast.LENGTH_LONG).show();
                                    } else if (gml == 3 && Integer.parseInt(goal_weight.getText().toString()) < userWeight) {
                                        Toast.makeText(MainActivity2.this, "Weight should be bigger than goal weight", Toast.LENGTH_LONG).show();
                                    } else {
                                        int goalWeight = userWeight;
                                        saveUserData(goalWeight, gml);
                                    }
                                } else if (TextUtils.isEmpty(goal_weight.getText())) {
                                    Toast.makeText(MainActivity2.this, "Fill all fields", Toast.LENGTH_LONG).show();
                                } else {
                                    int goalWeight = Integer.parseInt(goal_weight.getText().toString());
                                    saveUserData(goalWeight, gml);
                                }
                            }
                        } else {
                            Toast.makeText(MainActivity2.this, "Error getting user weight", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void saveUserData(int goalWeight, int gml) {
        // Save user data to Firestore
        docRef.update("goalWeight", goalWeight, "gml", gml)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(MainActivity2.this, MainActivity3.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity2.this, "Error saving user data", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
