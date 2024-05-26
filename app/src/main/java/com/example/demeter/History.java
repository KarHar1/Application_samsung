package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class History extends Fragment {
    private Button selectDateButton;
    private TextView eatenTextView, burnedTextView, dateTextView;
    private BottomNavigationView navigationView;
    public static String selectedDate, email;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_history, container, false);

        selectDateButton = view.findViewById(R.id.selectDateButton);
        eatenTextView = view.findViewById(R.id.eattenF);
        burnedTextView = view.findViewById(R.id.burnedF);
        dateTextView = view.findViewById(R.id.datedate);
        navigationView = view.findViewById(R.id.topnav);

        SharedPreferences pref = getActivity().getSharedPreferences(PREFS_NAME, 0);
        email = pref.getString("email", "");

        selectDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });

        navigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment selectedFragment = null;

                int itemId = menuItem.getItemId();

                if (itemId == R.id.nav_food_top) {
                    selectedFragment = new FragmentFood(); // Replace with actual fragment class
                } else if (itemId == R.id.nav_exercise_top) {
                    selectedFragment = new FragmentExer(); // Replace with actual fragment class
                }

                if (selectedFragment != null) {
                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.frame, selectedFragment)
                            .commit();
                    return true;
                }
                return false;
            }

        });


        if (savedInstanceState == null) {
            navigationView.setSelectedItemId(R.id.nav_food_top);
        }
        if (getActivity() instanceof MainActivity6) {
            MainActivity6 mainActivity = (MainActivity6) getActivity();
            mainActivity.hideView();
        }

        return view;
    }


    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selectedDate = String.valueOf(year) + "0" + String.valueOf(month + 1) + String.valueOf(dayOfMonth);
                        String selectedDateSt = String.valueOf(dayOfMonth) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(year);
                        dateTextView.setText(selectedDateSt);

                        // Ensure selectedDate is not null before using it
                        if (!selectedDate.isEmpty()) {
                            fetchUserData(selectedDate, "CaloriesEatten", 1);
                            fetchUserData(selectedDate, "CaloriesBurned", 2);

                            getChildFragmentManager().beginTransaction()
                                    .replace(R.id.frame, new FragmentFood())
                                    .commit();
                        }
                    }
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void fetchUserData(String date, String what, int num) {
        db.collection("users")
                .document(email)
                .collection(date)
                .document(what)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                handleUserData(document, num);
                            } else {
                                eatenTextView.setText("0");
                                burnedTextView.setText("0");
                            }
                        } else {
                            Log.d("Fragment4", "get failed with ", task.getException());
                        }
                    }
                });
    }

    private void handleUserData(DocumentSnapshot document, int num) {
        if (num == 1) {
            eatenTextView.setText(String.valueOf(document.get("caloriesEatten")));
        } else {
            burnedTextView.setText(String.valueOf(document.get("caloriesBurned")));
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        RelativeLayout layout = getActivity().findViewById(R.id.vector);
        if (layout != null) {
            layout.setVisibility(View.VISIBLE);
        }


        selectedDate = null;
    }
}
