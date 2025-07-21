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
package com.ae.apps.lib.mock

import android.content.Context
import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.common.models.PhoneNumberInfo
import com.ae.apps.lib.mocks.R
import java.util.Random

object MockContactDataUtils {

    fun getMockContact(context: Context): ContactInfo {
            val contactInfo = ContactInfo()

            // Create the phone number list and add one
            val phoneNumbersList: MutableList<PhoneNumberInfo> = ArrayList()

            var numberInfo = PhoneNumberInfo()
            numberInfo.phoneNumber = "87 7781 6267"
            numberInfo.unformattedPhoneNumber = "8777816267"
            phoneNumbersList.add(numberInfo)

            numberInfo = PhoneNumberInfo()
            numberInfo.phoneNumber = "86 2343 6789"
            numberInfo.unformattedPhoneNumber = "8623436789"
            phoneNumbersList.add(numberInfo)

            // Get mock names from resources
            val mockNames = context.resources.getStringArray(R.array.mock_names)

            val random = Random()
            val randomVal = random.nextInt(mockNames.size)
            val randomName = mockNames[randomVal]

            contactInfo.name = randomName
            contactInfo.id = randomVal.toString()
            contactInfo.setHasPhoneNumber(true)
            contactInfo.phoneNumbersList = phoneNumbersList
            return contactInfo
        }
}