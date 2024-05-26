package com.example.demeter;

import static com.example.demeter.History.email;
import static com.example.demeter.History.selectedDate;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ListAdapter;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FragmentExer extends Fragment {
    ListView list;

    ArrayAdapter<String> ad;

    String dateOFToday;

    FirebaseFirestore db;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_fragment_exer, container, false);
        list = view.findViewById(R.id.listEXer);

        dateOFToday = selectedDate;

        db = FirebaseFirestore.getInstance();

        ad = new ArrayAdapter<>(Objects.requireNonNull(getContext()), android.R.layout.simple_list_item_1);

        list.setAdapter(ad);


fechtlistdata();



        return view;
    }
    private void fechtlistdata() {
if(selectedDate!=null) {

    db.collection("users").document(email).collection(dateOFToday).
            document("Exer").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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

    }


    public void clearAdapter() {
        if (ad != null) {
            ad.clear();
            ad.notifyDataSetChanged();
        }
    }


    private void handleUserDataList(DocumentSnapshot document) {
        Map<String, Object> arr = new HashMap<>(document.getData());
        for (int i = 0; i < arr.size(); i++) {
            ad.add((String) arr.get(String.valueOf(i)));
        }
    }
}
