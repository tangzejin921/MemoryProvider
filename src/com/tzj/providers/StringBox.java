package com.tzj.providers;

public class StringBox {
    private StringBuffer sb = new StringBuffer("\n");
    private String str;

    public StringBox append(String str) {
        sb.append(str);
        return this;
    }

    @Override
    public String toString() {
        if (str == null) {
            str = sb.toString();
        }
        return str;
    }
}
