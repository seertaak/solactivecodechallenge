package com.solactive.codechallenge.message;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class Statistics {
    public final float avg;
    public final float max;
    public final float min;
    public final int count;

    public Statistics(float avg, float max, float min, int count) {
        this.avg = avg;
        this.max = max;
        this.min = min;
        this.count = count;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
