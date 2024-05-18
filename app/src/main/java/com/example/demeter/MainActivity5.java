package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity5 extends Fragment {

    private TextView eatten, burned;
    private Button okExersizeButton, noEexersizeButton;
    private StringBuilder itemsInfoExer;
    private EditText exersizeSearch;
    private ArrayAdapter<String> adapterExer, adptr;
    private double totalCalories1 = 0;
    private int numberOfItmes;
    private HashMap<String, String> mapOfExer;

    private FirebaseFirestore db;
    private String email;
    private DocumentReference docRef;

    private String dateOFToday;
    private ListView burnedView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main5, container, false);

        mapOfExer = new HashMap<>();
        eatten = getActivity().findViewById(R.id.eatenTextView);
        burned = getActivity().findViewById(R.id.burnedTextView);
        burnedView = view.findViewById(R.id.exerciseView);
        numberOfItmes = 0;

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        dateOFToday = currentDate.format(formatter);

        SharedPreferences pref = getActivity().getSharedPreferences(PREFS_NAME, 0);
        email = pref.getString("email", "");
        SharedPreferences.Editor ed = pref.edit();

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(email);

        ListView exersizeList = view.findViewById(R.id.foodItemList);

        itemsInfoExer = new StringBuilder();
        exersizeSearch = view.findViewById(R.id.searchBarExercises);

        okExersizeButton = view.findViewById(R.id.okExButton);
        noEexersizeButton = view.findViewById(R.id.noExButton);

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
        adapterExer = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        adptr = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1);
        exersizeList.setAdapter(adapterExer);
        burnedView.setAdapter(adptr);

        fechtlistdata(); // Fetch the list data from Firestore

        burnedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                {
                    try {
                        String selectedItem = adptr.getItem(position);
                        if (selectedItem != null) {
                            numberOfItmes++;
                            itemsInfoExer.append(selectedItem);
                            adapterExer.add(selectedItem);

                            // Extract calories from the selected item
                            double selectedCalories = extractCaloriesFromString(selectedItem);
                            totalCalories1 += selectedCalories;
                            burned.setText(String.valueOf(totalCalories1));

                            // Save to Firestore
                            saveUserData("caloriesBurned", totalCalories1);

                            mapOfExer.put(String.valueOf(numberOfItmes), selectedItem);
                            exerInfoSave(mapOfExer);
                        }
                    } catch (NumberFormatException e) {
                        burned.setText("0");
                    }
                }
            }
        });

        return view;
    }

    private double extractCaloriesFromString(String itemInfo) {
        String[] parts = itemInfo.split("\n");
        for (String part : parts) {
            if (part.startsWith("Calories:")) {
                String caloriesStr = part.split(":")[1].trim();
                return Double.parseDouble(caloriesStr);
            }
        }
        return 0;
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
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }

                String responseBody = response.body().string();
                try {
                    JSONArray jsonArray = new JSONArray(responseBody);

                    if (jsonArray.length() > 0) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adptr.clear(); // Clear the adapter before adding new items
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject obj = jsonArray.getJSONObject(i);
                                        String name = obj.getString("name");
                                        double totalCalories = obj.getDouble("total_calories");
                                        String itemInfo = "Item: " + name + "\nCalories: " + totalCalories + "\n\n";
                                        adptr.add(itemInfo); // Add each item to the adapter
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adptr.clear();
                                adptr.add("No exercise found with the given query.");
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void fechtlistdata() {
        db.collection("users").document(email).collection("Exer")
                .document(dateOFToday).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
    }

    private void handleUserDataList(DocumentSnapshot document) {
        Map<String, Object> data = document.getData();
        if (data != null) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                String exerciseInfo = (String) entry.getValue();
                adapterExer.add(exerciseInfo);
            }
        }
    }

    private void saveUserData(String nameOBJ, Object value) {
        // Save user data to Firestore
        docRef.update(nameOBJ, value);
    }

    private void exerInfoSave(Object value) {
        db.collection("users").document(email).collection("Exer").document(dateOFToday).set(value);
    }
}
