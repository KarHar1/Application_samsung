package com.example.demeter;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity6 extends AppCompatActivity {

    private TextView eaten;
    private TextView calories;
    private TextView burned;
    private TextView errorMessagePlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main6);

        eaten = findViewById(R.id.eatenTextView);
        calories = findViewById(R.id.caloriesTextView);
        burned = findViewById(R.id.burnedTextView);
        errorMessagePlace = findViewById(R.id.blablablaTextView);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

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
}
