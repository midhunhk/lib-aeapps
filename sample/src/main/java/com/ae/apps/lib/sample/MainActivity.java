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
import com.ae.apps.lib.sample.features.contacts.ContactsSampleActivity;
import com.ae.apps.lib.sample.features.sms.SmsSampleActivity;
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

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        features.add(FeatureInfo.of(1, "Contacts API"));
        features.add(FeatureInfo.of(2, "SMS API"));
        features.add(FeatureInfo.of(3, "Multi contact Picker"));
        features.add(FeatureInfo.of(4, "About Library"));

        RecyclerView recyclerView = findViewById(R.id.featuresList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        FeaturesRecyclerViewAdapter adapter = new FeaturesRecyclerViewAdapter(this, features);

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        FeatureInfo featureInfo = features.get(position);
        if (featureInfo.getId() == 1) {
            startActivity(new Intent(this, ContactsSampleActivity.class));
        } else if (featureInfo.getId() == 2) {
            startActivity(new Intent(this, SmsSampleActivity.class));
        } else {
            Toast.makeText(this, featureInfo.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_about) {
            DialogUtils.showCustomViewDialog(this, getLayoutInflater(),
                    R.layout.about_view,
                    R.string.menu_about);
        } else if (id == R.id.action_build) {
            startActivity(IntentUtils.getUriIntent(this, getString(R.string.url_travis_build)));
        } else if (id == R.id.action_source) {
            startActivity(IntentUtils.getUriIntent(this, getString(R.string.url_github_source)));
        }
        return super.onOptionsItemSelected(item);
    }
}
