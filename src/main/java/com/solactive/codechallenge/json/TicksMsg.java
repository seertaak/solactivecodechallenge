package com.solactive.codechallenge.json;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class TicksMsg implements Serializable {
    public String instrument;
    public float price;
    public long timestamp;

    public TicksMsg() {}

    public TicksMsg(String instrument, float price, long timestamp) {
        this.instrument = instrument;
        this.price = price;
        this.timestamp = timestamp;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
