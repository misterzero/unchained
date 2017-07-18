package com.ippon.unchained.domain;

/**
 * Created by slaughter on 7/18/17.
 */
public class BlockchainDTO {
    private Poll poll;

    private BlockchainUser user;

    public Poll getPoll() {
        return poll;
    }

    public void setPoll(Poll poll) {
        this.poll = poll;
    }

    public BlockchainUser getUser() {
        return user;
    }

    public void setUser(BlockchainUser user) {
        this.user = user;
    }
}
