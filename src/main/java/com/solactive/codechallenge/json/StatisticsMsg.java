package com.solactive.codechallenge.json;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.io.Serializable;

public class StatisticsMsg implements Serializable {
    public float avg;
    public float max;
    public float min;
    public int count;

    public StatisticsMsg() {}

    public StatisticsMsg(float avg, float max, float min, int count) {
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    // only used in test cases, no need for efficiency.
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
