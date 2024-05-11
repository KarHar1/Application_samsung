package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity5 extends AppCompatActivity {
    private TextView eatten;
    private TextView calories, errorMessagePlace;
    private Button okExersizeButton, noEexersizeButton;
    StringBuilder itemsInfoExer;
    private TextView burned, burnedView;
    private EditText exersizeSearch;
    ArrayList<String> allTimeExerInfo;
    private ArrayAdapter<String> adapterExer;
    private double totalCalories1 = 0;

    FirebaseFirestore db;
    String email;
    DocumentReference docRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main5);
        SharedPreferences pref =  getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        email = pref.getString("email",  "");

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(email); // Assuming you have a Firestore document reference here

        ListView exersizeList = findViewById(R.id.foodItemList);

        itemsInfoExer = new StringBuilder();
        allTimeExerInfo = new ArrayList<>();
        exersizeSearch = findViewById(R.id.searchBarExercises);

        okExersizeButton = findViewById(R.id.okExButton);
        noEexersizeButton = findViewById(R.id.noExButton);

        exersizeSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                performExersizeSearch(exersizeSearch.getText().toString());
            }
        });

        // Adapter for exercises
        adapterExer = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        exersizeList.setAdapter(adapterExer);

        okExersizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    burned.setText(String.valueOf(Double.parseDouble(burned.getText().toString()) + totalCalories1));
                    adapterExer.add(itemsInfoExer.toString());
                    allTimeExerInfo.add(itemsInfoExer.toString());

                    // Save to Firestore
                    saveUserData("caloriesBurned", totalCalories1);
                    exerInfoSave("exercises", itemsInfoExer.toString());
                } catch (NumberFormatException e) {
                    burned.setText("0");
                    burned.setText(String.valueOf(Double.parseDouble(burned.getText().toString()) + totalCalories1));
                }
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
                        itemsInfoExer.append("Item: ").append(name).append("\nCalories: ").append(totalCalories1).append("\n\n");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                burnedView.setText(itemsInfoExer);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
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

    private void saveUserData(String nameOBJ, Object value) {
        // Save user data to Firestore
        docRef.update(nameOBJ, value);
    }
    private void exerInfoSave(String nameOBJ, Object value){
        docRef.collection("days_exer").add(value);
    }
}
