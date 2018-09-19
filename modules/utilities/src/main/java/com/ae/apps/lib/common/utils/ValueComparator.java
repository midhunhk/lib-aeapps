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

package com.ae.apps.lib.common.utils;

import java.util.Comparator;
import java.util.Map;

/**
 * Comparator that can be used for sorting a map based on the values
 *
 * @param <K> k
 * @param <V> v
 */
public class ValueComparator<K, V extends Comparable<V>> implements Comparator<K> {

    private Map<K, V> base;

    public ValueComparator(Map<K, V> base) {
        this.base = base;
    }

    @Override
    public int compare(K a, K b) {
        if (null == a || null == b) {
            return 0;
        }
        if (null == base.get(a) || null == base.get(b)) {
            return 0;
        }
        return base.get(b).compareTo(base.get(a));
    }

}
