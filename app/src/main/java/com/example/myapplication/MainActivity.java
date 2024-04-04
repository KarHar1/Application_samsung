package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "MyPrefsFile";
    public static final String FIRST_TIME_KEY = "first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences userPref = getSharedPreferences("UserInfo", MODE_PRIVATE);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

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
                    String gender =genderSpinner.getSelectedItem().toString();

                    if (TextUtils.isEmpty(name) || TextUtils.isEmpty(ageString) ||
                            TextUtils.isEmpty(weightString) || TextUtils.isEmpty(heightString)) {
                        Toast.makeText(MainActivity.this, "Fill all fields", Toast.LENGTH_LONG).show();
                        return;
                    }

                    int age = Integer.parseInt(ageString);
                    int weight = Integer.parseInt(weightString);
                    int height = Integer.parseInt(heightString);
                    if (age < 0 || age > 100) {
                        setError("Enter a proper age", ageEditText);
                        return;
                    }
                    clearError(ageEditText);

                    if (weight < 0 || weight > 350) {
                        setError("Enter a proper weight", weightEditText);
                        return;
                    }
                    clearError(weightEditText);

                    if (height < 0 || height > 300) {
                        setError("Enter a proper height", heightEditText);
                        return;
                    }
                    clearError(heightEditText);

                    SharedPreferences.Editor editor = userPref.edit();
                    editor.putString("name", name);
                    editor.putInt("age", age);
                    editor.putInt("weight", weight);
                    editor.putInt("height", height);
                    editor.putString("gender", gender);
                    editor.apply();

                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putBoolean(FIRST_TIME_KEY, false);
                    prefsEditor.apply();

                    startActivity(new Intent(MainActivity.this, MainActivity2.class));
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(MainActivity.this, MainActivity4.class);
            startActivity(intent);
            finish();
        }
    }

    public void setError(String error, EditText editText) {
        editText.getBackground().setColorFilter(
                getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
        editText.setText("");
        editText.setHint(error);
    }

    private void clearError(EditText editText) {
        editText.getBackground().setColorFilter(
                getResources().getColor(R.color.buttonColor), PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void onBackPressed() {

        if (false) {
            super.onBackPressed();
        } else {

        }
    }
}
