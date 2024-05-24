package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;
import static com.example.demeter.MainActivity5.hasRun;
import static com.example.demeter.MainActivity5.hasRun1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.PorterDuff;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import android.util.Log;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity4 extends Fragment {

    private FirebaseFirestore db;
    private String email;
    private long currentday;

    SharedPreferences pref;
    SharedPreferences.Editor ed;
    private DocumentReference docRef;
    private Map<String, String> value;
    HashMap<String, Object> somtthg;

    private EditText searchbar;
    private TextView booo;
    private Button okFoodButton;
    private Button noFoodButton;

    private ListView foodItems;
    private ArrayAdapter<String> adapter;
    private double totalCalories;
    private double totalBurnedCalories = 0;
    private StringBuilder itemsInfo;

    private TextView eatten;
    private TextView calories;
    private TextView burned;
    private TextView errorMessagePlace;
    String dateOFToday;

    int itemNumber;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main4, container, false);

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        dateOFToday = currentDate.format(formatter);

        eatten = getActivity().findViewById(R.id.eatenTextView);
        calories = getActivity().findViewById(R.id.caloriesTextView);
        burned = getActivity().findViewById(R.id.burnedTextView);

        currentday = System.currentTimeMillis();


        db = FirebaseFirestore.getInstance();
        pref = getActivity().getSharedPreferences(PREFS_NAME, 0);
        email = pref.getString("email", "");
        ed = pref.edit();

        docRef = db.collection("users").document(email);

        value = new HashMap<>();
        somtthg = new HashMap<>();
        searchbar = view.findViewById(R.id.searchBar);
        booo = view.findViewById(R.id.booo);
        okFoodButton = view.findViewById(R.id.okButton);
        noFoodButton = view.findViewById(R.id.noButton);

        foodItems = view.findViewById(R.id.foodItemList);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        foodItems.setAdapter(adapter);

        fetchUserData();
        fetchUserData();
        if (!eatten.getTouchables().isEmpty()) {
            totalCalories = Double.parseDouble(eatten.getText().toString());
        } else {
            totalCalories = 0;
        }
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = searchbar.getText().toString();
                performSearch(query);
            }
        });
        fechtlistdata();

        okFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    double currentEatenCalories = Double.parseDouble(eatten.getText().toString());
                    double updatedEatenCalories = currentEatenCalories + totalCalories;
                    eatten.setText(String.valueOf(updatedEatenCalories));



                        adapter.add(itemsInfo.toString());
                        saveUserData("caloriesEatten", updatedEatenCalories);
                        value.put(String.valueOf(itemNumber), itemsInfo.toString());
                        itemNumber++;
                        saveUserData("numberfood", itemNumber);

                        exerInfoSave(value);
                        MainActivity6.checkCaloriesComparison(getContext(), (MainActivity6) getActivity(), eatten, burned, calories);




                } catch (NumberFormatException e) {
                    eatten.setText("0");

                    double currentEatenCalories = Double.parseDouble(eatten.getText().toString());
                    double updatedEatenCalories = currentEatenCalories + totalCalories;
                    eatten.setText(String.valueOf(updatedEatenCalories));



                        adapter.add(itemsInfo.toString());
                        saveUserData("caloriesEaten", updatedEatenCalories);

                        value.put(String.valueOf(itemNumber), itemsInfo.toString());
                        itemNumber++;
                        saveUserData("numberfood", itemNumber);

                        exerInfoSave(value);



                }
            }
        });

        noFoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchbar.setText("");
            }
        });

        return view;
    }


    private void fechtlistdata() {


        db.collection("users").document(email).collection(dateOFToday).
                document("Food").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                handleUserDataList(document);
                            }
                        } else {
                            Log.d("Fragment4", "get failed with ", task.getException());
                        }
                    }
                });

        db.collection("users").document(email).collection(dateOFToday).
                document("CaloriesEatten").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document != null && document.exists()) {
                                handleUserData123(document);
                            }
                        } else {
                            Log.d("Fragment4", "get failed with ", task.getException());
                        }
                    }
                });

    }

    private void handleUserDataList(DocumentSnapshot document) {
        Map<String, Object> arr = new HashMap<>(document.getData());
        for (int i = 0; i < arr.size(); i++) {
            adapter.add((String) arr.get(String.valueOf(i)));
        }
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
                    Log.d("Fragment4", "get failed with ", task.getException());
                }
            }
        });
    }

    private void handleUserData123(DocumentSnapshot document) {
        if (document.get("numberfood") != null) {
            itemNumber = (int) document.getLong("numberfood").intValue();
        } else {
            itemNumber = 0;
        }
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


        calories.setText(String.valueOf(tr));
    }

    private void performSearch(String query) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.api-ninjas.com/v1/nutrition?query=" + query)
                .header("X-Api-Key", "Z5SIEjAXzW3uB7AaD5rfUYNGG2kyMOB8fREdkDvX")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        Log.d("Fragment4", "Request failed with code: " + response.code());
                        throw new IOException("Unexpected code " + response);
                    }

                    JSONArray jsonArray = new JSONArray(response.body().string());
                    itemsInfo = new StringBuilder();
                    double totalmotal =0;

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject data = jsonArray.getJSONObject(i);
                        String itemName = data.getString("name");
                        double caloriesValue = data.getDouble("calories");
                        itemsInfo.append("Item: ").append(itemName).append("\nCalories: ").append(caloriesValue).append("\n\n");
                        totalmotal += caloriesValue;
                    }
                    totalCalories += totalmotal;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (itemsInfo != null) {
                                booo.setText(itemsInfo.toString());
                            } else {
                                booo.setText("No items found.");
                            }
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void saveUserData(String nameOBJ, Object value1) {


        somtthg.put(nameOBJ, value1);

        db.collection("users").document(email).collection(dateOFToday).document("CaloriesEatten").update(somtthg);

    }

    private void exerInfoSave(Object value) {

        db.collection("users").document(email).collection(dateOFToday).document("Food").update((Map<String, Object>) value);

    }
}
