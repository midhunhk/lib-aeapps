/*
 * Copyright (c) 2015 Midhun Harikumar
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
 *
 */

package com.ae.apps.lib.multicontact;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ae.apps.aeappslibrary.R;
import com.ae.apps.common.vo.ContactVo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that displays contacts list with a checkbox
 */
class MultiContactRecyclerViewAdapter extends RecyclerView.Adapter<MultiContactRecyclerViewAdapter.ViewHolder> {

    private final List<ContactVo> mValues;
    private final Map<String, Boolean> mCheckedStatus;
    private final MultiContactInteractionListener mListener;

    MultiContactRecyclerViewAdapter(final List<ContactVo> values, MultiContactInteractionListener listener) {
        mValues = values;
        mListener = listener;
        mCheckedStatus = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multi_contact_picker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mProfileName.setText(mValues.get(position).getName());
        holder.mProfileImage.setImageResource(R.drawable.profile_icon_4);

        final String contactId = holder.mItem.getId();

        // Clear out the previous listener attached to this Recycled ViewHolder
        holder.mCheckBox.setOnCheckedChangeListener(null);

        // Set the checked status of this checkbox from our data list
        if (mCheckedStatus.containsKey(contactId) && mCheckedStatus.get(contactId)) {
            holder.mCheckBox.setChecked(true);
        } else {
            holder.mCheckBox.setChecked(false);
        }

        holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Store the checked status of this data item
                mCheckedStatus.put(contactId, isChecked);

                if (isChecked) {
                    mListener.onContactSelected(contactId);
                } else {
                    mListener.onContactUnselected(contactId);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView mProfileImage;
        final TextView mProfileName;
        final CheckBox mCheckBox;
        ContactVo mItem;
        boolean mItemSelected;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mItemSelected = false;
            mProfileImage = (ImageView) view.findViewById(R.id.multiContactPickerProfileImage);
            mProfileName = (TextView) view.findViewById(R.id.multiContactPickerProfileName);
            mCheckBox = (CheckBox) view.findViewById(R.id.multiContactPickerCheckBox);
        }
    }
}
