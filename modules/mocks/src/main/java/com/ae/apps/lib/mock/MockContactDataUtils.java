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

package com.ae.apps.lib.mock;

import android.content.res.Resources;

import com.ae.apps.aeappslibrary.R;
import com.ae.apps.common.vo.ContactMessageVo;
import com.ae.apps.common.vo.ContactVo;
import com.ae.apps.lib.common.models.ContactInfo;
import com.ae.apps.lib.common.models.PhoneNumberInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class MockContactDataUtils {

    private static final String LOCALE_FR = "fr";
    private static final String LOCALE_ES = "es";

    /**
     * Mock names, TODO read from resources
     */
    private static String mockNamesEN[] = {"James Elliot", "Aiden Perry", "Daisy Forster", "Matt Gibson",
            "Martin J. Fox", "Catherine", "Scott Burns", "Jesse Whitehurst", "Diana Brown"};
    private static String mockNamesES[] = {"Bicor Adomo Abrego", "Fortuna Granado Fonseca",
            "Germana Ruvalcaba", "Sotero Jimnez Razo", "Olimpia Campos Curiel", "Folco Vega Girn",
            "Aidee Padrn Cazares"};
    private static String mockNamesFR[] = {"Lyle Coulombe", "Saber Rivire", "Algernon Monjeau",
            "Emmeline Lamy", "Sylvie Mouet", "Carolos Bourgeau", "Ccile Fresne", "Loring Deslauriers"};

    /**
     * mock profile images
     */
    private static int mockProfileImages[] = {R.drawable.profile_icon_1, R.drawable.profile_icon_2,
            R.drawable.profile_icon_3, R.drawable.profile_icon_4, R.drawable.profile_icon_5, R.drawable.profile_icon_6,
            R.drawable.profile_icon_1, R.drawable.profile_icon_5, R.drawable.profile_icon_3};

    /**
     * Returns a mock contact
     *
     * @return ContactVo contactVo
     */
    public static ContactInfo getMockContact() {
        ContactInfo contactInfo = new ContactInfo();

        // Create the phone number list and add one
        List<PhoneNumberInfo> phoneNumbersList = new ArrayList<>();
        PhoneNumberInfo numberVo = new PhoneNumberInfo();
        numberVo.setPhoneNumber("87 7781 6267");
        phoneNumbersList.add(numberVo);

        numberVo = new PhoneNumberInfo();
        numberVo.setPhoneNumber("86 2343 6789");
        phoneNumbersList.add(numberVo);

        Random random = new Random();
        int randomVal = random.nextInt(mockNamesEN.length);
        String randomName = mockNamesEN[randomVal];

        // set mock details for this contact
        // contactInfo.setMockUser(true);
        // contactInfo.setMockProfileImageResource(mockProfileImages[randomVal]);

        contactInfo.setName(randomName);
        contactInfo.setId(String.valueOf(randomVal));
        contactInfo.setTimesContacted(String.valueOf(randomVal));
        contactInfo.setHasPhoneNumber(true);
        contactInfo.setPhoneNumbersList(phoneNumbersList);

        return contactInfo;
    }

    /**
     * A mock implementation for giving mock results. Used mainly for screenshots
     *
     * @param resource resource
     * @return list of contact messages
     */
    public static List<ContactMessageVo> getMockContactMessageList(Resources resource) {
        Random random = new Random();
        int startSeed = 180;
        int prevRandom = 0;
        int currRandom;
        ContactVo contactVo;
        ContactMessageVo messageVo;
        List<ContactMessageVo> mockedList = new ArrayList<>();

        // Supply different set of mock names based on current locale, default is EN
        String[] mockNames;
        String locale = Locale.getDefault().getLanguage();
        if (LOCALE_ES.equalsIgnoreCase(locale)) {
            mockNames = mockNamesES;
        } else if (LOCALE_FR.equalsIgnoreCase(locale)) {
            mockNames = mockNamesFR;
        } else {
            mockNames = mockNamesEN;
        }

        // Some important calculations are about to happen
        for (String name : mockNames) {
            contactVo = new ContactVo();
            contactVo.setName(name);
            messageVo = new ContactMessageVo();
            messageVo.setContactVo(contactVo);
            if (prevRandom == 0) {
                currRandom = startSeed;
            } else {
                currRandom = random.nextInt(startSeed);
            }
            if (currRandom > prevRandom) {
                currRandom = (int) (currRandom * 0.75);
            }
            messageVo.setMessageCount(currRandom);
            prevRandom = currRandom;
            startSeed -= 2;
            mockedList.add(messageVo);
        }
        return mockedList;
    }

}
