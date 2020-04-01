package com.ae.apps.lib.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ae.apps.lib.common.utils.DialogUtils;
import com.ae.apps.lib.common.utils.intents.IntentUtils;
import com.ae.apps.lib.sample.adapters.FeaturesRecyclerViewAdapter;
import com.ae.apps.lib.sample.features.Features;
import com.ae.apps.lib.sample.models.FeatureInfo;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity
        implements FeaturesRecyclerViewAdapter.ItemClickListener {

    private List<FeatureInfo> features = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        // Get a static list of Features to be displayed in the Features List
        features = Features.getFeatures();

        RecyclerView recyclerView = findViewById(R.id.featuresList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FeaturesRecyclerViewAdapter adapter = new FeaturesRecyclerViewAdapter(this, features);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        FeatureInfo featureInfo = features.get(position);
        if (null != featureInfo.getFeatureClass()) {
            startActivity(new Intent(this, featureInfo.getFeatureClass()));
        } else {
            if (featureInfo.getSpecialFeature() == Features.SpecialFeature.ABOUT) {
                DialogUtils.showCustomViewDialog(this, getLayoutInflater(),
                        R.layout.about_view,
                        R.string.menu_about);
            } else {
                Toast.makeText(this, featureInfo.getName(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // No menu at this time
        // getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            DialogUtils.showCustomViewDialog(this, getLayoutInflater(),
                    R.layout.about_view,
                    R.string.menu_about);
            return true;
        } else if (id == R.id.action_build) {
            startActivity(IntentUtils.getUriIntent(this, getString(R.string.url_travis_build)));
            return true;
        } else if (id == R.id.action_source) {
            startActivity(IntentUtils.getUriIntent(this, getString(R.string.url_github_source)));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
