package com.ippon.unchained.repository;

import com.ippon.unchained.domain.Poll;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Poll entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PollRepository extends JpaRepository<Poll,Long> {
    Poll findOne(String name);
}
