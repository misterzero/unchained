package com.ippon.unchained.domain;

/**
 * An Active Poll
 * For use with BlockchainUsers - unique from Poll in that it holds
 * the number of tokens a user has to spend on that poll.
 */
public class ActivePoll {
    private String name;

    private int tokens;

    public String getName() {
        return this.name;
    }

    public ActivePoll name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTokens() {
        return tokens;
    }

    public ActivePoll tokens(int tokens) {
        this.tokens = tokens;
        return this;
    }

    public void setTokens(int tokens) {
        this.tokens = tokens;
    }

    public String toJSONString() {
        return "{" +
            "\"name\":\"" + getName() +
            "\", \"tokens\":" + getTokens() +
            "}";
    }
}
