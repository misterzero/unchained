package com.ippon.unchained.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ippon.unchained.domain.ActivePoll;
import com.ippon.unchained.domain.BlockchainUser;
import com.ippon.unchained.domain.Poll;
import com.ippon.unchained.security.SecurityUtils;
import com.ippon.unchained.service.BlockchainUserService;
import com.ippon.unchained.service.PollService;
import com.ippon.unchained.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Poll.
 */
@RestController
@RequestMapping("/api")
public class PollResource {

    private final Logger log = LoggerFactory.getLogger(PollResource.class);

    private static final String ENTITY_NAME = "poll";

    private final PollService pollService;

    private final BlockchainUserService blockchainUserService;

    public PollResource(PollService pollService, BlockchainUserService blockchainUserService) {
        this.pollService = pollService;
        this.blockchainUserService = blockchainUserService;
    }

    /**
     * POST  /polls : Create a new poll.
     *
     * @param poll the poll to create
     * @return the ResponseEntity with status 201 (Created) and with body the new poll, or with status 400 (Bad Request) if the poll has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/polls")
    @Timed
    public ResponseEntity<Poll> createPoll(@RequestBody Poll poll) throws URISyntaxException {
        log.debug("REST request to save Poll : {}", poll);
        if (poll.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new poll cannot already have an ID")).body(null);
        }
        Poll result = pollService.save(poll);
        return ResponseEntity.created(new URI("/api/polls/" + result.getName()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getName().toString()))
            .body(result);
    }

    /**
     * PUT  /polls : Updates an existing poll.
     *
     * @param poll the poll to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated poll,
     * or with status 400 (Bad Request) if the poll is not valid,
     * or with status 500 (Internal Server Error) if the poll couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/polls")
    @Timed
    public ResponseEntity<Poll> updatePoll(@RequestBody Poll poll) throws URISyntaxException {
        log.debug("REST request to update Poll : {}", poll);
        if (poll.getId() == null) {
            return createPoll(poll);
        }
        Poll result = pollService.save(poll);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, poll.getId().toString()))
            .body(result);
    }

    /**
     * GET  /polls : get all the polls.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of polls in body
     */
    @GetMapping("/polls")
    @Timed
    public List<Poll> getAllPolls() {
        List<Poll> pollList = new ArrayList<>();
        log.debug("REST request to get all Polls for user "+SecurityUtils.getCurrentUserId().toString());
        BlockchainUser bcu =  blockchainUserService.findOne(SecurityUtils.getCurrentUserId().toString());
        log.debug("BlockchainUser obtained with activePolls: " + bcu.getActivePolls());
        log.debug("Converting activePolls string to list");
        List<ActivePoll> apl = bcu.getActivePollsAsList();
        log.debug("activePolls obtained as list");
        log.debug("activePolls list: " + apl.toString());
        for (ActivePoll ap : apl) {
            Poll poll = new Poll();
            poll.setName(ap.getName());
            pollList.add(poll);
        }
        return pollList;
    }

    /**
     * GET  /polls/:id : get the "id" poll.
     *
     * @param id the id of the poll to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the poll, or with status 404 (Not Found)
     */
    @GetMapping("/polls/{id}")
    @Timed
    public ResponseEntity<Poll> getPoll(@PathVariable Long id) {
        log.debug("REST request to get Poll : {}", id);
        Poll poll = pollService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(poll));
    }

    /**
     * DELETE  /polls/:id : delete the "id" poll.
     *
     * @param id the id of the poll to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/polls/{id}")
    @Timed
    public ResponseEntity<Void> deletePoll(@PathVariable Long id) {
        log.debug("REST request to delete Poll : {}", id);
        pollService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
