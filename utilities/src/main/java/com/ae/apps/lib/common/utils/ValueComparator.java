package com.ae.apps.common.utils;

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
