package com.solactive.codechallenge.disruptor;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class StockTickEvent {
    public int internalStockId;
    public float price;
    public long timestamp;

    public StockTickEvent() {}

    // for debug purposes only!
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
