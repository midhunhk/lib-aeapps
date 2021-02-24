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

/**
 * Represents a PhoneNumber
 *
 * @since 4.0
 */
public class PhoneNumberInfo {

    private String phoneType;
    private String phoneNumber;
    private String lastTimeContacted;
    private String unformattedPhoneNumber;

    public String getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(String phoneType) {
        this.phoneType = phoneType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLastTimeContacted() {
        return lastTimeContacted;
    }

    public void setLastTimeContacted(String lastTimeContacted) {
        this.lastTimeContacted = lastTimeContacted;
    }

    public String getUnformattedPhoneNumber() {
        return unformattedPhoneNumber;
    }

    public void setUnformattedPhoneNumber(String unformattedPhoneNumber) {
        this.unformattedPhoneNumber = unformattedPhoneNumber;
    }
}
