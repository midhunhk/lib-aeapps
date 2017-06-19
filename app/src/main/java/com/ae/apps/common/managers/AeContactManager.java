/*
 * Copyright 2017 Midhun Harikumar
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

package com.ae.apps.common.managers;

import android.graphics.Bitmap;

import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.common.vo.MessageVo;

import java.util.List;

/**
 * A specification for a ContactManager used in AE App lab projects
 */

interface AeContactManager {

    /**
     * Gets the total contacts read
     *
     * @return number of contacts
     */
    int getTotalContactCount();

    /**
     * Returns a random contactVo from the list if the list is non empty, null otherwise
     *
     * @return a ContactVo object
     */
    ContactVo getRandomContact();

    /**
     * Returns a contact with phone details
     *
     * @param contactId contact id
     * @return contact vo
     */
    ContactVo getContactWithPhoneDetails(String contactId);

    /**
     * Returns a Bitmap object for the contact's photo, returning a default image
     *
     * @param contactId    contact id
     * @param defaultImage default image
     * @return contact photo
     */
    Bitmap getContactPhoto(String contactId, Bitmap defaultImage);

    /**
     * @param contactId contact id
     * @return contact vo
     */
    ContactVo getContactInfo(String contactId);

    /**
     * Returns the list of messages for this contact with id
     *
     * @param contactId contact id
     * @return list of message vo s
     */
    List<MessageVo> getContactMessages(String contactId);

    /**
     * Returns the contactId based on the rawContactId
     *
     * @param rawContactId ra contact id
     * @return
     */
    long getContactIdFromRawContactId(String rawContactId);

    /**
     * Does a lookup on the contacts by phone number lookup
     *
     * @param address address
     * @return
     */
    String getContactIdFromAddress(String address);
}
