package com.example.demeter;
import static com.example.demeter.MainActivity.PREFS_NAME;
import static com.example.demeter.MainActivity5.hasRun;
import static com.example.demeter.MainActivity5.hasRun1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class MainActivity6 extends AppCompatActivity {

    private TextView eaten;
    private TextView calories;
    private TextView burned  , totalView;
    private TextView errorMessagePlace;

    public String dateOFToday;

    FirebaseFirestore db;
    DocumentReference docRef;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        dateOFToday = currentDate.format(formatter);

        SharedPreferences pref = getSharedPreferences(PREFS_NAME, 0);
        email = pref.getString("email", "");

        SharedPreferences.Editor ed = pref.edit();
        ed.putString("dayoftoday", dateOFToday).apply();

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(email);

        eaten = findViewById(R.id.eatenTextView);
        calories = findViewById(R.id.caloriesTextView);
        burned = findViewById(R.id.burnedTextView);
        totalView = findViewById(R.id.totalTextView);

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
                } else if (itemId == R.id.nav_settings) {
                    selectedFragment = new Settings();
                } else if (itemId == R.id.nav_history) {
                    selectedFragment = new History();
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
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.d("MainActivity6", "Listen failed with ", e);
                    return;
                }
                if (document != null && document.exists()) {
                    handleUserData(document, 0);
                }
            }
        });

        db.collection("users").document(email).collection(dateOFToday).document("CaloriesEatten")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("MainActivity6", "Listen failed with ", e);
                            return;
                        }
                        if (document != null && document.exists()) {
                            handleUserData(document, 1);
                        }
                    }
                });

        db.collection("users").document(email).collection(dateOFToday).document("CaloriesBurned")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot document, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.d("MainActivity6", "Listen failed with ", e);
                            return;
                        }
                        if (document != null && document.exists()) {
                            handleUserData(document, 2);
                        }
                    }
                });
    }

    private void handleUserData(DocumentSnapshot document, Integer bool) {
        if (bool == 0) {
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
            calories.setText(String.valueOf(tr));
        } else if (bool == 1) {
            Object eatenValue = 0;
            if (document.getLong("caloriesEatten") != null) {
                eatenValue = document.getLong("caloriesEatten").intValue();
            }
            eaten.setText(String.valueOf(eatenValue));
        } else if (bool == 2) {
            Object burnedValue = 0;
            if (document.getLong("caloriesBurned") != null) {
                burnedValue = document.getLong("caloriesBurned").intValue();
            }
            burned.setText(String.valueOf(burnedValue));
        }

updateTotalView();
    }

    private void saveUserData(String nameOBJ, Object value) {
        docRef.update(nameOBJ, value);
    }

    public static void checkCaloriesComparison(Context context, MainActivity6 activity, TextView eatenView, TextView burnedView, TextView totalView) {
        // Get the text from the TextViews and parse them to double
        double eaten = Double.parseDouble(eatenView.getText().toString());
        double burned = Double.parseDouble(burnedView.getText().toString());
        double total = Double.parseDouble(totalView.getText().toString());

        if (eaten - burned > total) {
            // Show a warning using an AlertDialog
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Warning");
            builder.setMessage("You have exceeded your daily calorie limit.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Optional: Handle the OK button click event
                    dialog.dismiss(); // Dismiss the dialog
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            // Clear the warning
            // You can optionally add a Toast message to indicate that the limit is not exceeded
            // Toast.makeText(context, "You are within your daily calorie limit.", Toast.LENGTH_SHORT).show();
        }
    }

    public void hideView() {
        RelativeLayout layout = findViewById(R.id.vector);
        if (layout != null) {
            layout.setVisibility(View.GONE);
        }
    }


    private void updateTotalView() {
        try {
            int eatenCalories = Integer.parseInt(eaten.getText().toString());
            int burnedCalories = Integer.parseInt(burned.getText().toString());
            totalView.setText(String.valueOf(eatenCalories - burnedCalories));
        } catch (NumberFormatException e) {
            Log.d("MainActivity6", "NumberFormatException: " + e.getMessage());
            totalView.setText("0");
        }
    }
}
