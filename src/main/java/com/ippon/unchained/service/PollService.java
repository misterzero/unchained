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
     *  Delete the "id" poll.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
