package com.ippon.unchained.service;

import com.ippon.unchained.domain.Poll;
import java.util.List;

/**
 * Service Interface for managing Poll.
 */
public interface PollService {

    /**
     * Save a poll.
     *
     * @param poll the entity to save
     * @return the persisted entity
     */
    Poll save(Poll poll);

    /**
     *  Get all the polls.
     *
     *  @return the list of entities
     */
    List<Poll> findAll();

    /**
     *  Get the "id" poll.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    Poll findOne(Long id);

    /**
     *  Get the "name" poll.
     *
     *  @param name the name of the entity
     *  @return the entity
     */
    Poll findOne(String name);

    /**
     *  Delete the "id" poll.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);

    /**
     *  Close the "id" poll.
     *
     *  @param id the id of the entity
     */
    void close(String id, Long userId);

    /**
     * Cast vote for 'opt' by 'user' in 'poll'
     *
     * @param ballot = ["user", "1_testPoll", "opt1"]
     */
    void vote(String ballot);

    /**
     * Move poll from a user's activePoll list to their inactivePoll list
     *
     * @param userId = ID of currently logged in user
     * @param pollId = ChainCodeName of poll to deactivate
     */
    void deactivatePoll(String userId, String pollId);
}
