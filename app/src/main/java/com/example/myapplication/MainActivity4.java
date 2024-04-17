package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity4 extends AppCompatActivity {

    private TextView eatten;
    private TextView calories;
    private TextView burned;
    private EditText searchbar;
    private TextView booo; // Added TextView reference

    // Declare class variables to hold the total calories
    private double totalCalories = 0;
    private double totalBurnedCalories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        // Initialize TextViews, EditText, and TextView for "booo"
        eatten = findViewById(R.id.xxxx2);
        calories = findViewById(R.id.xxxx);
        burned = findViewById(R.id.xxxx1);
        searchbar = findViewById(R.id.searchBar);
        booo = findViewById(R.id.booo); // Initialize TextView

        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        Button settingsbutton = findViewById(R.id.settingsbttn);

        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity4.this, Settings.class));
            }
        });

        // Retrieve user information from SharedPreferences
        int age = sp.getInt("age", 0);
        int weight = sp.getInt("weight", 0);
        int height = sp.getInt("height", 0);
        String gender = sp.getString("gender", "male");
        int goal_weight = sp.getInt("goalWeight", 0);
        int gml = sp.getInt("gml", 0);
        int exer = sp.getInt("exer", 1);
        int days = sp.getInt("days", 0);
        User user = new User(age, height, weight, gml, goal_weight, exer, days, gender);

        double tr = user.calculateCalories();
        calories.setText(String.valueOf(tr));

        // TextChangedListener for searchbar
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = searchbar.getText().toString();
                performSearch(query);
            }
        });

        // OnClickListener for "booo" TextView
        booo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eatten.setText(String.valueOf(Double.parseDouble(eatten.getText().toString()) + totalCalories));
                calories.setText(String.valueOf(Double.parseDouble(calories.getText().toString()) - totalCalories));
                totalBurnedCalories += totalCalories; // Update total burned calories
                if(Double.parseDouble(calories.getText().toString())<0){
                    Toast.makeText(MainActivity4.this, "yu have reaches your limit for today" , Toast.LENGTH_LONG).show();
                    searchbar.setEnabled(false);
                    booo.setEnabled(false);
                    calories.setText("calories");
                    eatten.setText("eatten");

                    // Handler to enable the TextView after 20 seconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Enable the TextView after 20 seconds
                            searchbar.setEnabled(true);
                            booo.setEnabled(true);
                        }
                    }, 2000);
                }
            }
        });
    }

    private void performSearch(String query) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.calorieninjas.com/v1/nutrition?query=" + query)
                .header("X-Api-Key", "An8kO+LlVrcipoCzOWxszw==hEmVWrmAWS5JCBvw")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                // Handle failure gracefully (e.g., show an error message)
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    // Parse JSON response
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    JSONArray dataArr = jsonObject.getJSONArray("items");

                    // Initialize StringBuilder to hold item information
                    StringBuilder itemsInfo = new StringBuilder();

                    // Iterate over each item in the response
                    for (int i = 0; i < dataArr.length(); i++) {
                        JSONObject data = dataArr.getJSONObject(i);

                        // Extract item details
                        String itemName = data.getString("name");
                        double caloriesValue = data.getDouble("calories");

                        // Append item information to the StringBuilder
                        itemsInfo.append("Item: ").append(itemName).append("\nCalories: ").append(caloriesValue).append("\n\n");

                        // Update total calories
                        totalCalories += caloriesValue;
                    }

                    // Update UI with retrieved data
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // Update UI elements
                            eatten.setText(String.valueOf(totalBurnedCalories));
                            booo.setText(itemsInfo.toString()); // Set text to "booo" TextView
                        }
                    });
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                    // Handle JSON parsing or IO exception
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Disable back button functionality if needed
        // super.onBackPressed();
        super.onBackPressed();
    }
}
