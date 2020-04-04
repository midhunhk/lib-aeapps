/*
 * Copyright (c) 2020 Midhun Harikumar
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ae.apps.lib.common.models.ContactInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Adapter that displays contacts list with a checkbox
 */
class MultiContactRecyclerViewAdapter extends RecyclerView.Adapter<MultiContactRecyclerViewAdapter.ViewHolder>
        implements Filterable {

    private final Set<String> checkedStatus;
    private final MultiContactInteractionListener interactionListener;
    private final List<ContactInfo> contactInfoList;

    private List<ContactInfo> filteredContacts;

    MultiContactRecyclerViewAdapter(final List<ContactInfo> values, MultiContactInteractionListener listener) {
        filteredContacts = new ArrayList<>(values);
        contactInfoList = values;
        interactionListener = listener;
        checkedStatus = new HashSet<>();
    }

    void setSelectedContacts(final List<String> selectedContacts){
        checkedStatus.addAll(selectedContacts);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.multi_contact_picker_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.item = filteredContacts.get(position);
        holder.profileName.setText(filteredContacts.get(position).getName());
        holder.profileImage.setImageResource(com.ae.apps.lib.R.drawable.profile_icon_4);

        final String contactId = holder.item.getId();

        // Clear out the previous listener attached to this Recycled ViewHolder
        holder.checkBox.setOnCheckedChangeListener(null);

        // Set the checked status of this checkbox from our data list
        if(checkedStatus.contains(contactId)){
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checkedStatus.add(contactId);
                    interactionListener.onContactSelected(contactId);
                } else {
                    checkedStatus.remove(contactId);
                    interactionListener.onContactUnselected(contactId);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return filteredContacts.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                boolean dataUpdated = false;
                if (charSequence.length() == 0) {
                    if (filteredContacts.size() < contactInfoList.size()) {
                        filteredContacts = new ArrayList<>(contactInfoList);
                        dataUpdated = true;
                    }
                } else {
                    filteredContacts = filterContactsByName(charSequence.toString().toLowerCase());
                    dataUpdated = true;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = dataUpdated;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                boolean dataUpdated = Boolean.parseBoolean(filterResults.values.toString());
                if (dataUpdated) {
                    notifyDataSetChanged();
                }
            }
        };
    }

    private List<ContactInfo> filterContactsByName(String queryString) {
        List<ContactInfo> results = new ArrayList<>();
        for (ContactInfo contactInfo : contactInfoList) {
            if (null != contactInfo.getName() && contactInfo.getName().toLowerCase().contains(queryString)) {
                results.add(contactInfo);
            }
        }
        return results;
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
