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

package com.ae.apps.lib.common.models;

import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import java.util.List;

/**
 * Represents a Contact
 *
 * @since 4.0
 */
public class ContactInfo implements Comparable<ContactInfo>{

    private String id;
    private String name;
    private boolean hasPhoneNumber;
    private transient Bitmap picture;
    private List<PhoneNumberInfo> phoneNumbersList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasPhoneNumber() {
        return hasPhoneNumber;
    }

    public void setHasPhoneNumber(boolean hasPhoneNumber) {
        this.hasPhoneNumber = hasPhoneNumber;
    }

    public List<PhoneNumberInfo> getPhoneNumbersList() {
        return phoneNumbersList;
    }

    public void setPhoneNumbersList(List<PhoneNumberInfo> phoneNumbersList) {
        this.phoneNumbersList = phoneNumbersList;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    @Override
    public int compareTo(@NonNull ContactInfo another) {
        if(this.id == null || another.id == null) {
            return 0;
        }
        return this.id.compareTo(another.id);
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
