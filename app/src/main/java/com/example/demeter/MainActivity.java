package com.example.demeter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String FIRST_TIME_KEY = "first_time";

    int gml = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = this.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String email = prefs.getString("email" , "");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(email);

        if (prefs.getBoolean(FIRST_TIME_KEY, true)) {
            setContentView(R.layout.activity_main);

            EditText nameEditText = findViewById(R.id.edittext_1);
            EditText ageEditText = findViewById(R.id.edittext_2);
            EditText weightEditText = findViewById(R.id.edittext_3);
            EditText heightEditText = findViewById(R.id.edittext_4);
            Spinner genderSpinner = findViewById(R.id.edittext_5);
            Button nextButton = findViewById(R.id.button);

            nextButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameEditText.getText().toString();
                    String ageString = ageEditText.getText().toString();
                    String weightString = weightEditText.getText().toString();
                    String heightString = heightEditText.getText().toString();
                    String gender = genderSpinner.getSelectedItem().toString();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ageString) ||
                            TextUtils.isEmpty(weightString) || TextUtils.isEmpty(heightString)) {
                        Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int age = Integer.parseInt(ageString);
                    int weight = Integer.parseInt(weightString);
                    int height = Integer.parseInt(heightString);

                    if (age < 0 || age > 100 || weight < 0 || weight > 350 || height < 0 || height > 300) {
                        Toast.makeText(MainActivity.this, "Enter proper values for age, weight, and height", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Map<String, Object> user = new HashMap<>();
                    user.put("name", name);
                    user.put("age", age);
                    user.put("weight", weight);
                    user.put("height", height);
                    user.put("gender", gender);

                    db.collection("users")
                            .document(email)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                SharedPreferences.Editor prefsEditor = prefs.edit();
                                prefsEditor.putBoolean(FIRST_TIME_KEY, false);
                                prefsEditor.apply();

                                startActivity(new Intent(MainActivity.this, MainActivity2.class));
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(MainActivity.this, "Error saving user data", Toast.LENGTH_LONG).show();
                            });
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
        if(false){
            super.onBackPressed();
        }

    }
}
