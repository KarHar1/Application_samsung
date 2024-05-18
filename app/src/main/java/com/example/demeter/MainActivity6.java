package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

public class MainActivity6 extends AppCompatActivity {

    private TextView eaten;
    private TextView calories;
    private TextView burned;
    private TextView errorMessagePlace;

    FirebaseFirestore db;
    DocumentReference docRef;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);


        SharedPreferences pref = getSharedPreferences(PREFS_NAME, 0);
        email = pref.getString("email", "");

        db = FirebaseFirestore.getInstance();
        docRef =  db.collection("users").document(email);


        eaten = findViewById(R.id.eatenTextView);
        calories = findViewById(R.id.caloriesTextView);
        burned = findViewById(R.id.burnedTextView);
        errorMessagePlace = findViewById(R.id.blablablaTextView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        fetchUserData();


        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                int itemId = item.getItemId();

                if (itemId == R.id.nav_food) {
                    selectedFragment = new MainActivity4();
                } else if (itemId == R.id.nav_exercise) {
                    selectedFragment = new MainActivity5();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }
        });

        // Set default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_food);
        }
    }



    private void fetchUserData() {
       docRef.get().addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = (DocumentSnapshot) task.getResult();
                    if (document != null && document.exists()) {
                        handleUserData(document);
                    }
                } else {
                    Log.d("MainActivity4", "get failed with ", task.getException());
                }
            }
        });
    }

    private void handleUserData(DocumentSnapshot document) {
        int age = document.getLong("age").intValue();
        int weight = document.getLong("weight").intValue();
        int height = document.getLong("height").intValue();
        String gender = document.getString("gender");
        int goal_weight = document.getLong("goalWeight").intValue();
        int gml = document.getLong("gml").intValue();
        int exer = document.getLong("exer").intValue();
        int days = document.getLong("days").intValue();
        User user = new User(age, height, weight, gml, goal_weight, exer, days, gender);
        double tr = user.calculateCalories();

        saveUserData("dailyCalories", Float.parseFloat(String.valueOf(tr)));
        Object eatenValue = document.get("caloriesEaten");
        Object burnedValue = document.get("caloriesBurned");




        burned.setText(String.valueOf(burnedValue) );
        eaten.setText(String.valueOf(eatenValue));
        calories.setText(String.valueOf(tr));

    }

    private void saveUserData(String nameOBJ, Object value) {
        docRef.update(nameOBJ, value);
    }






}

