package org.decaywood.entity;

/**
 * @author: decaywood
 * @date: 2015/11/30 21:35.
 */
public class Entry<K, V> {

    /**
     * MACD 金叉天数 key
     */
    public static final String MACD_CROSS_KEY = "macdCross";
    /**
     * 高粉丝数关注人数key
     */
    public static final String VIP_COUNT_KEY = "vipCount";

    /**
     * 板块说明
     */
    public static final String INDUSTRY_DESC = "industryDesc";

    private final K key;
    private final V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
