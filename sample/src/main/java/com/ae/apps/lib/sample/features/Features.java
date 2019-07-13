/*
 * Copyright 2019 Midhun Harikumar
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

package com.ae.apps.lib.sample.features;

import com.ae.apps.lib.sample.features.contacts.ContactsSampleActivity;
import com.ae.apps.lib.sample.features.sms.SmsSampleActivity;
import com.ae.apps.lib.sample.models.FeatureInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides a static list of features
 */
public class Features {

    public enum SpecialFeature {
        ABOUT, NOT_IMPLEMENTED
    }

    public static List<FeatureInfo> getFeatures() {
        List<FeatureInfo> features = new ArrayList<>();
        features.add(FeatureInfo.of(1, "Contacts API", "An Abstraction over Android Contacts", ContactsSampleActivity.class));
        /* TODO: Comment out when publishing to PlayStore due to SMS API Permission restrictions */
        // features.add(FeatureInfo.of(2, "SMS API", "An Abstraction over Android SMS Api", SmsSampleActivity.class));

        features.add(FeatureInfo.of(3, "Multi contact Picker", "Select multiple contacts", SpecialFeature.NOT_IMPLEMENTED));
        features.add(FeatureInfo.of(4, "About", "More Information", SpecialFeature.ABOUT));
        return features;
    }
}
