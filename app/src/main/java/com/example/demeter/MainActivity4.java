package com.example.demeter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import io.grpc.internal.JsonParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity4 extends AppCompatActivity {

    private TextView eatten;
    private TextView calories , errorMessagePlace;
    private TextView burned , burnedView;
    private EditText searchbar , exersizeSearch;
    private TextView booo;
    private double totalCalories = 0;
    private double totalCalories1 = 0;

    private double totalBurnedCalories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        eatten = findViewById(R.id.xxxx2);
        calories = findViewById(R.id.xxxx);
        burned = findViewById(R.id.xxxx1);
        searchbar = findViewById(R.id.searchBar);
        exersizeSearch = findViewById(R.id.searchBarExersize);
        booo = findViewById(R.id.booo);
        burnedView = findViewById(R.id.exersizView);
        errorMessagePlace = findViewById(R.id.blablabla);

        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);

        Button settingsbutton = findViewById(R.id.settingsbttn);

        settingsbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity4.this, Settings.class));
            }
        });

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

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = searchbar.getText().toString();
                Log.d("Debug", "Search query: " + query); // Log the search query
                performSearch(query);
            }
        });

        // OnClickListener for "booo" TextView
        booo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eatten.setText(String.valueOf(Double.parseDouble(eatten.getText().toString()) + totalCalories));
                totalBurnedCalories += totalCalories; // Update total burned calories


                if(Double.parseDouble(calories.getText().toString())+Double.parseDouble(burned.getText().toString())<Double.parseDouble(eatten.getText().toString())){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            calories.getBackground().setColorFilter(
                                    getResources().getColor(R.color.red), PorterDuff.Mode.SRC_ATOP);
                            errorMessagePlace.setText("you have passed your  limit for today");
                        }
                    });

                }
            }
        });
        burnedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    burned.setText(String.valueOf(Double.parseDouble(burned.getText().toString()) + totalCalories1));
                }catch (NumberFormatException e){
                    burned.setText("0");
                    burned.setText(String.valueOf(Double.parseDouble(burned.getText().toString()) + totalCalories1));

                }

            }
        });


        exersizeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                performExersizeSearch(exersizeSearch.getText().toString());

            }
        });
    }

    private void performExersizeSearch(String query) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.api-ninjas.com/v1/caloriesburned?activity=" + query)
                .header("X-Api-Key", "Z5SIEjAXzW3uB7AaD5rfUYNGG2kyMOB8fREdkDvX")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected code " + response);
                    }

                    JSONArray jsonArray = new JSONArray(response.body().string());

                    if (jsonArray.length() > 0) {
                        JSONObject obj = jsonArray.getJSONObject(0); // Take the first object

                        String name = obj.getString("name");
                         totalCalories1 = obj.getDouble("total_calories");

                        // Update UI with retrieved data
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update UI elements
                                burnedView.setText("Exercise: " + name +
                                        "\nTotal Calories: " + totalCalories1);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Update UI elements to show no results
                                burnedView.setText("No exercise found with the given query.");
                            }
                        });
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
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
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                try {
                    if (!response.isSuccessful()) {
                        Log.d("Debug", "blyaaa"); // Log the search query
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
        if(false){
            super.onBackPressed();
        }

    }
}
