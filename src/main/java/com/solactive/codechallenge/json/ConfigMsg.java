package com.solactive.codechallenge.json;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class ConfigMsg {
    public String instrument;
    public int maxMessagesInWindow;

    public ConfigMsg() {}

    public ConfigMsg(String instrument, int maxMessagesInWindow) {
        this.instrument = instrument;
        this.maxMessagesInWindow = maxMessagesInWindow;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }
}
