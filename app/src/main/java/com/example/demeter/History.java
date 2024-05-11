package com.example.demeter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class History extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        TextView cal = findViewById(R.id.ed);
        TextView burn = findViewById(R.id.ed2);
        TextView eat = findViewById(R.id.ed3);
        ListView list = findViewById(R.id.foodItemList);
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        ArrayAdapter<String> ad = new ArrayAdapter<>(this , android.R.layout.simple_list_item_1);


        cal.setText(sp.getString("dailyCalories" , ""));
        burn.setText(sp.getString("burnedCalories" , "0909"));
        eat.setText(sp.getString("caloriesEatten", "151"));

    }
    @Override
    public void onBackPressed() {
if(false){
    super.onBackPressed();
}else {
    try {
        startActivity(new Intent(History.class.newInstance(), MainActivity4.class));
    } catch (IllegalAccessException e) {
        throw new RuntimeException(e);
    } catch (InstantiationException e) {
        throw new RuntimeException(e);
    }
}

    }
}