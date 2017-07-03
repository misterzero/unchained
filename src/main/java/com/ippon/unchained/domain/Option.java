package com.ippon.unchained.domain;

/**
 * An Option.
 * This object is used to store the options in a poll when
 * transferring data on options either from the creation view
 * to the chaincode, or from the chaincode to the details view
 * (i.e. viewing the results of a poll).
 */
public class Option {
    private String name;

    private int count = 0;

    public Option(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Option name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return this.count;
    }

    public Option count(int count) {
        this.count = count;
        return this;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String toString() {
        return "{" +
            "name=" + getName() +
            ", count='" + getCount() + "'" +
            "}";
    }

    public String toJSONString() {
        return "{" +
            "\"name\":\"" + getName() +
            "\", \"count\":" + getCount() +
            "}";
    }
}
