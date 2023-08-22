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

import com.ae.apps.lib.common.models.ContactInfo
import com.ae.apps.lib.common.models.PhoneNumberInfo
import java.util.Random

object MockContactDataUtils {
    private const val LOCALE_FR = "fr"
    private const val LOCALE_ES = "es"

    /**
     * Mock names, TODO read from resources
     */
    private val mockNamesEN = arrayOf(
        "James Elliot", "Aiden Perry", "Daisy Forster", "Matt Gibson",
        "Martin J. Fox", "Catherine", "Scott Burns", "Jesse Whitehurst", "Diana Brown"
    )
    private val mockNamesES = arrayOf(
        "Bicor Adomo Abrego", "Fortuna Granado Fonseca",
        "Germana Ruvalcaba", "Sotero Jimnez Razo", "Olimpia Campos Curiel", "Folco Vega Girn",
        "Aidee Padrn Cazares"
    )
    private val mockNamesFR = arrayOf(
        "Lyle Coulombe", "Saber Rivire", "Algernon Monjeau",
        "Emmeline Lamy", "Sylvie Mouet", "Carolos Bourgeau", "Ccile Fresne", "Loring Deslauriers"
    )

    /**
     * mock profile images
     */
    /*
    private static final int[] mockProfileImages = {R.drawable.profile_icon_1, R.drawable.profile_icon_2,
            R.drawable.profile_icon_3, R.drawable.profile_icon_4, R.drawable.profile_icon_5, R.drawable.profile_icon_6,
            R.drawable.profile_icon_1, R.drawable.profile_icon_5, R.drawable.profile_icon_3};
*/
    val mockContact: ContactInfo
        /**
         * Returns a mock contact
         *
         * @return ContactVo contactVo
         */
        get() {
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
            val random = Random()
            val randomVal = random.nextInt(mockNamesEN.size)
            val randomName = mockNamesEN[randomVal]

            // set mock details for this contact
            // contactInfo.setMockUser(true);
            // contactInfo.setMockProfileImageResource(mockProfileImages[randomVal]);
            contactInfo.name = randomName
            contactInfo.id = randomVal.toString()
            contactInfo.setHasPhoneNumber(true)
            contactInfo.phoneNumbersList = phoneNumbersList
            return contactInfo
        }
}