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

    private int votes = 0;

    public Option(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVotes() {
        return this.votes;
    }

    public void setVotes(int votes) {
       /**
        * Using setVotes is safe here - it will only impact how we show votes to the front-end
        * HOWEVER, if this function is ever used to set votes up when creating a new poll,
        * this function must be removed as it allows for falsification of vote results
        * (i.e. initializing votes for a certain option to be greater than zero)
        */
        this.votes = votes;
    }

    public String toString() {
        return "{" +
            "name=" + getName() +
            ", votes='" + getVotes() + "'" +
            "}";
    }
}
