package com.example.demeter;

import static android.content.Context.MODE_PRIVATE;
import static com.example.demeter.MainActivity.PREFS_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Settings extends Fragment {

    private EditText textName, textAge, textWeight, textHeight, gWeight;
    private RadioGroup radioGroupGoal;
    private RadioButton radioGainWeight, radioLoseWeight, radioMaintainWeight;
    private Button editButton, logoutButton;
    int gml;
    int num = 0;

    FirebaseFirestore db;

    String email;

    DocumentReference docRef;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_settings, container, false);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = requireActivity().getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        email = prefs.getString("email", "");
        docRef = db.collection("users").document(email);

        // Initializing EditTexts
        textName = view.findViewById(R.id.text_name);
        textAge = view.findViewById(R.id.text_age);
        textWeight = view.findViewById(R.id.text_weight);
        textHeight = view.findViewById(R.id.text_height);
        gWeight = view.findViewById(R.id.gWeight);

        // Initializing RadioGroup and RadioButtons
        radioGroupGoal = view.findViewById(R.id.radio_group_goal);
        radioGainWeight = view.findViewById(R.id.radio_gain_weight);
        radioLoseWeight = view.findViewById(R.id.radio_lose_weight);
        radioMaintainWeight = view.findViewById(R.id.radio_maintain_weight);

        // Initializing Buttons
        editButton = view.findViewById(R.id.editbutton);
        logoutButton = view.findViewById(R.id.button_logout);

        fetchUserData();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num % 2 == 0) {
                    textName.setEnabled(true);
                    textAge.setEnabled(true);
                    textWeight.setEnabled(true);
                    textHeight.setEnabled(true);
                    gWeight.setEnabled(true);

                    // Initializing RadioGroup and RadioButtons
                    radioGroupGoal.setEnabled(true);
                    radioGainWeight.setEnabled(true);
                    radioLoseWeight.setEnabled(true);
                    radioMaintainWeight.setEnabled(true);
                    logoutButton.setEnabled(true);

                    saveUserData("name", textName.getText().toString());
                    saveUserData("age", textAge.getText().toString());
                    saveUserData("weight", textWeight.getText().toString());
                    saveUserData("height", textHeight.getText().toString());
                    saveUserData("goal_weight", gWeight.getText().toString());

                    // Save user data for RadioGroup
                    int selectedRadioButtonId = radioGroupGoal.getCheckedRadioButtonId();
                    if (selectedRadioButtonId != -1) {
                        RadioButton selectedRadioButton = view.findViewById(selectedRadioButtonId);
                        String selectedGoal = selectedRadioButton.getText().toString();
                        saveUserData("goal", selectedGoal);
                    }

                    num++;
                } else {
                    textName.setEnabled(false);
                    textAge.setEnabled(false);
                    textWeight.setEnabled(false);
                    textHeight.setEnabled(false);
                    gWeight.setEnabled(false);
                    logoutButton.setEnabled(false);

                    // Initializing RadioGroup and RadioButtons
                    radioGroupGoal.setEnabled(false);
                    radioGainWeight.setEnabled(false);
                    radioLoseWeight.setEnabled(false);
                    radioMaintainWeight.setEnabled(false);
                    num++;
                }
            }
        });

        return view;
    }

    private void fetchUserData() {
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
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
        Object eatenValue = document.get("eatten");
        Object burnedValue = document.get("burned");
        textWeight.setText(document.getString("name"));
        textAge.setText(String.valueOf(document.getLong("age").intValue()));
        textWeight.setText(String.valueOf(document.getLong("weight").intValue()));
        textHeight.setText(String.valueOf(document.getLong("height").intValue()));
        gWeight.setText(String.valueOf(document.getLong("goalWeight").intValue()));
        gml = document.getLong("gml").intValue();

        int dailyCal = document.getLong("dailyCalories").intValue();
    }

    private void saveUserData(String nameOBJ, Object value) {
        docRef.update(nameOBJ, value);
    }
}
