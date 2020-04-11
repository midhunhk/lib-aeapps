/*
 * Copyright 2018 Midhun Harikumar
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ae.apps.lib.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

/**
 * Base Activity if using a NoActionBar theme and to supply a custom toolbar
 */
public abstract class ToolBarBaseActivity extends AppCompatActivity {
    private static final String BRAND_LGE = "LGE";
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
        if (keyCode == KeyEvent.KEYCODE_MENU && BRAND_LGE.equals(Build.BRAND)) {
            openOptionsMenu();
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

}
