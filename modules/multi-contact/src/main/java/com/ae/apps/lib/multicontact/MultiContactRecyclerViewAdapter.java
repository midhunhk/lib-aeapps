/*
 * Copyright (c) 2018 Midhun Harikumar
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ae.apps.lib.common.models.ContactInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Adapter that displays contacts list with a checkbox
 */
class MultiContactRecyclerViewAdapter extends RecyclerView.Adapter<MultiContactRecyclerViewAdapter.ViewHolder> {

    private final List<ContactInfo> contactInfos;
    private final Map<String, Boolean> checkedStatus;
    private final MultiContactInteractionListener interactionListener;

    MultiContactRecyclerViewAdapter(final List<ContactInfo> values, MultiContactInteractionListener listener) {
        contactInfos = values;
        interactionListener = listener;
        checkedStatus = new HashMap<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multi_contact_picker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.item = contactInfos.get(position);
        holder.profileName.setText(contactInfos.get(position).getName());
        holder.profileImage.setImageResource(com.ae.apps.lib.R.drawable.profile_icon_4);

        final String contactId = holder.item.getId();

        // Clear out the previous listener attached to this Recycled ViewHolder
        holder.checkBox.setOnCheckedChangeListener(null);

        // Set the checked status of this checkbox from our data list
        if (checkedStatus.containsKey(contactId) && checkedStatus.get(contactId)) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Store the checked status of this data item
                checkedStatus.put(contactId, isChecked);

                if (isChecked) {
                    interactionListener.onContactSelected(contactId);
                } else {
                    interactionListener.onContactUnselected(contactId);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactInfos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View view;
        final ImageView profileImage;
        final TextView profileName;
        final CheckBox checkBox;
        ContactInfo item;
        boolean itemSelected;

        ViewHolder(View view) {
            super(view);
            this.view = view;
            itemSelected = false;
            profileImage = view.findViewById(R.id.multiContactPickerProfileImage);
            profileName = view.findViewById(R.id.multiContactPickerProfileName);
            checkBox = view.findViewById(R.id.multiContactPickerCheckBox);
        }
    }
}
