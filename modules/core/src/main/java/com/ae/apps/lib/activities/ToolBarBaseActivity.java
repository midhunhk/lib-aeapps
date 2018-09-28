package com.ae.apps.lib.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class ToolBarBaseActivity extends AppCompatActivity {
    Toolbar mToolbar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayoutResourceId());

        // Find the toolbar and set it as action bar
        mToolbar = findViewById(getToolbarResourceId());
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
        }
    }

    /**
     * Sets the toolbartitle
     *
     * @param title Title for the toolbar
     */
    protected void setToolbarTitle(String title) {
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(title);
        }
    }

    /**
     * Returns the toolbar instance
     *
     * @return the toolbar instance
     */
    protected Toolbar getToolBar() {
        return mToolbar;
    }

    /**
     * Returns the toolbar resource id. Should be R.id.toolbar in most cases
     *
     * @return return the toolbar resource id
     */
    protected abstract int getToolbarResourceId();

    /**
     * Returns the layout resource id for the activity
     *
     * @return return the layout resource id
     */
    protected abstract int getLayoutResourceId();

    protected void displayHomeAsUp() {
        // Show the back arrow in toolbar to go back
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Fix for NPE on LG Devices when pressing hardware menu button
        return keyCode == KeyEvent.KEYCODE_MENU && "LGE".equals(Build.BRAND)
                || super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Fix for NPE on LG Devices when pressing hardware menu button
        if (keyCode == KeyEvent.KEYCODE_MENU && "LGE".equals(Build.BRAND)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
