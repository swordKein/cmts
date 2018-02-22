package com.kthcorp.cmts.util;

import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<String> {
    Map<String, Double> base;

    public ValueComparator(Map<String, Double> base) {
        this.base = base;
    }

    @Override
    public int compare(String a, String b) {
            // 오름차순 if((Double) base.get(a) <= (Double) base.get(b)) {
            if(base.get(a) >= base.get(b)) { // 내림차순
            return -1;
        } else {
            return 1;
        }
    }

}
