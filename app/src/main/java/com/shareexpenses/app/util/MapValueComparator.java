package com.shareexpenses.app.util;

import java.util.Comparator;
import java.util.Map;

/**
 * Created by jscreve on 16/07/2017.
 */
public final class MapValueComparator<K,V extends Comparable<V>> implements Comparator<K> {

    private Map<K,V> map;

    private MapValueComparator() {
        super();
    }

    public MapValueComparator(Map<K,V> map) {
        this();
        this.map = map;
    }

    public int compare(K o1, K o2) {
        return map.get(o1).compareTo(map.get(o2));
    }
}
