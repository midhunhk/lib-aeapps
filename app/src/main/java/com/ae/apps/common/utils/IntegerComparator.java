package com.ae.apps.common.utils;

import java.util.Comparator;
import java.util.Map;

/**
 * An integer comparator to be used for sorting a map whose value is an integer
 * 
 * @author Midhun
 * 
 */
public class IntegerComparator implements Comparator<String> {

	private Map<String, Integer>	base;

	public IntegerComparator(Map<String, Integer> base) {
		this.base = base;
	}

	@Override
	public int compare(String a, String b) {
		if (base.get(a) >= base.get(b)) {
			return -1;
		} else {
			return 1;
		}
	}

}
