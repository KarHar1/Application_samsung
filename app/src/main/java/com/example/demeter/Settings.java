package com.example.demeter;

import static android.content.Context.MODE_PRIVATE;
import static com.example.demeter.MainActivity.PREFS_NAME;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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

        // Add listener to fetch user data and handle real-time updates
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("Settings", "Listen failed.", e);
                    return;
                }

                if (snapshot != null && snapshot.exists()) {
                    handleUserData(snapshot);
                } else {
                    Log.d("Settings", "Current data: null");
                }
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
            }
        });

        disableFieldsAfterSaving();

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num % 2 == 0) {
                    // Enable fields for editing
                    enableFieldsForEditing();
                } else {
                    // Save data and disable fields
                    if (saveUserDataAndCheckValues()) {
                        disableFieldsAfterSaving();
                    }
                }
                num++;
            }
        });

        return view;
    }

    private void handleUserData(DocumentSnapshot document) {
        textName.setText(document.getString("name"));
        textAge.setText(String.valueOf(document.getLong("age").intValue()));
        textWeight.setText(String.valueOf(document.getLong("weight").intValue()));
        textHeight.setText(String.valueOf(document.getLong("height").intValue()));
        gWeight.setText(String.valueOf(document.getLong("goalWeight").intValue()));
        gml = document.getLong("gml").intValue();

        if (gml != 0) {
            if (gml == 1) {
                radioGainWeight.setChecked(true);
            } else if (gml == 2) {
                radioLoseWeight.setChecked(true);
            } else if (gml == 3) {
                radioMaintainWeight.setChecked(true);
            }
        }
    }

    private void saveUserData(String nameOBJ, Object value) {
        docRef.update(nameOBJ, value)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Settings", "User data updated: " + nameOBJ + " = " + value);
                        } else {
                            Log.w("Settings", "Error updating user data.", task.getException());
                        }
                    }
                });
    }

    private void enableFieldsForEditing() {
        textName.setEnabled(true);
        textAge.setEnabled(true);
        textWeight.setEnabled(true);
        textHeight.setEnabled(true);
        gWeight.setEnabled(true);
        radioGroupGoal.setEnabled(true);
        radioGainWeight.setEnabled(true);
        radioLoseWeight.setEnabled(true);
        radioMaintainWeight.setEnabled(true);
        logoutButton.setEnabled(true);
    }

    private boolean saveUserDataAndCheckValues() {
        boolean valuesWithinBounds = true;

        // Save data
        saveUserData("name", textName.getText().toString());
        saveUserData("age", Integer.parseInt(textAge.getText().toString()));
        saveUserData("weight", Integer.parseInt(textWeight.getText().toString()));
        saveUserData("height", Integer.parseInt(textHeight.getText().toString()));
        saveUserData("goalWeight", Integer.parseInt(gWeight.getText().toString()));

        // Check values against norms
        int age = Integer.parseInt(textAge.getText().toString());
        int weight = Integer.parseInt(textWeight.getText().toString());
        int height = Integer.parseInt(textHeight.getText().toString());

        if (age > 100 || weight > 500 || height > 300) {
            valuesWithinBounds = false;
            showValueWarningDialog();
        }

        // Save user data for RadioGroup
        int selectedId = radioGroupGoal.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = getView().findViewById(selectedId);
            switch (selectedRadioButton.getText().toString()) {
                case "Weight Loss":
                    gml = 1;
                    break;
                case "Weight Maintenance":
                    gml = 2;
                    break;
                case "Weight Gain":
                    gml = 3;
                    break;
            }
            saveUserData("gml", gml);
        }

        return valuesWithinBounds;
    }

    private void disableFieldsAfterSaving() {
        textName.setEnabled(false);
        textAge.setEnabled(false);
        textWeight.setEnabled(false);
        textHeight.setEnabled(false);
        gWeight.setEnabled(false);
        radioGroupGoal.setEnabled(false);
        radioGainWeight.setEnabled(false);
        radioLoseWeight.setEnabled(false);
        radioMaintainWeight.setEnabled(false);
        logoutButton.setEnabled(false);
    }

    private void showValueWarningDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Warning");
        builder.setMessage("Some of the input values are unusually high. Please double-check.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}


