package com.ae.apps.lib.sample;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.ae.apps.lib.sample.adapters.FeaturesRecyclerViewAdapter;
import com.ae.apps.lib.sample.models.FeatureInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements FeaturesRecyclerViewAdapter.ItemClickListener {

    private List<FeatureInfo> features = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        features.add(FeatureInfo.of(1, "Runtime Permissions"));
        features.add(FeatureInfo.of(2, "Contacts API"));
        features.add(FeatureInfo.of(3, "SMS API"));
        features.add(FeatureInfo.of(4, "Multi contact Picker"));

        RecyclerView recyclerView = findViewById(R.id.featuresList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FeaturesRecyclerViewAdapter adapter = new FeaturesRecyclerViewAdapter(this, features);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, features.get(position).getName(), Toast.LENGTH_SHORT).show();
    }
}
