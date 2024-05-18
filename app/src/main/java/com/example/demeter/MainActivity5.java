package com.example.demeter;

import static com.example.demeter.MainActivity.PREFS_NAME;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class MainActivity5 extends Fragment {

    private TextView eatten;
    private Button okExersizeButton, noEexersizeButton;
    private StringBuilder itemsInfoExer;
    private EditText exersizeSearch;
    private ArrayList<String> allTimeExerInfo;
    private ArrayAdapter<String> adapterExer;
    private double totalCalories1 = 0;

    private FirebaseFirestore db;
    private String email;
    private DocumentReference docRef;

    private TextView burned , burnedView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main5, container, false);

        eatten = getActivity().findViewById(R.id.eatenTextView);
        burned = getActivity().findViewById(R.id.burnedTextView);
        burnedView = getActivity().findViewById(R.id.exerciseView);

        SharedPreferences pref = getActivity().getSharedPreferences(PREFS_NAME, 0);
        email = pref.getString("email", "");

        db = FirebaseFirestore.getInstance();
        docRef = db.collection("users").document(email);

        ListView exersizeList = view.findViewById(R.id.foodItemList);

        itemsInfoExer = new StringBuilder();
        allTimeExerInfo = new ArrayList<>();
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

        return view;
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
                        JSONObject obj = jsonArray.getJSONObject(0);

                        String name = obj.getString("name");
                        totalCalories1 = obj.getDouble("total_calories");
                        itemsInfoExer.append("Item: ").append(name).append("\nCalories: ").append(totalCalories1).append("\n\n");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                burnedView.setText(itemsInfoExer);
                            }
                        });
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
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

    private void exerInfoSave(String nameOBJ, Object value) {
        docRef.collection("days_exer").add(value);
    }
}
