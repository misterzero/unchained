package com.ippon.unchained.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ippon.unchained.domain.BlockchainUser;
import com.ippon.unchained.service.BlockchainUserService;
import com.ippon.unchained.web.rest.util.HeaderUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing BlockchainUser.
 */
@RestController
@RequestMapping("/api")
public class BlockchainUserResource {

    private final Logger log = LoggerFactory.getLogger(BlockchainUserResource.class);

    private static final String ENTITY_NAME = "blockchainUser";

    private final BlockchainUserService blockchainUserService;

    public BlockchainUserResource(BlockchainUserService blockchainUserService) {
        this.blockchainUserService = blockchainUserService;
    }

    /**
     * POST  /blockchain-users : Create a new blockchainUser.
     *
     * @param blockchainUser the blockchainUser to create
     * @return the ResponseEntity with status 201 (Created) and with body the new blockchainUser, or with status 400 (Bad Request) if the blockchainUser has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/blockchain-users")
    @Timed
    public ResponseEntity<BlockchainUser> createBlockchainUser(@RequestBody BlockchainUser blockchainUser) throws URISyntaxException {
        log.debug("REST request to save BlockchainUser : {}", blockchainUser);
        if (blockchainUser.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new blockchainUser cannot already have an ID")).body(null);
        }
        BlockchainUser result = blockchainUserService.save(blockchainUser);
        return ResponseEntity.created(new URI("/api/blockchain-users/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /blockchain-users : Updates an existing blockchainUser.
     *
     * @param blockchainUser the blockchainUser to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated blockchainUser,
     * or with status 400 (Bad Request) if the blockchainUser is not valid,
     * or with status 500 (Internal Server Error) if the blockchainUser couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/blockchain-users")
    @Timed
    public ResponseEntity<BlockchainUser> updateBlockchainUser(@RequestBody BlockchainUser blockchainUser) throws URISyntaxException {
        log.debug("REST request to update BlockchainUser : {}", blockchainUser);
        if (blockchainUser.getId() == null) {
            return createBlockchainUser(blockchainUser);
        }
        BlockchainUser result = blockchainUserService.save(blockchainUser);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, blockchainUser.getId().toString()))
            .body(result);
    }

    /**
     * GET  /blockchain-users : get all the blockchainUsers.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of blockchainUsers in body
     */
    @GetMapping("/blockchain-users")
    @Timed
    public List<BlockchainUser> getAllBlockchainUsers() {
        log.debug("REST request to get all BlockchainUsers");
        return blockchainUserService.findAll();
    }

    /**
     * GET  /blockchain-users/:id : get the "id" blockchainUser.
     *
     * @param id the id of the blockchainUser to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the blockchainUser, or with status 404 (Not Found)
     */
    @GetMapping("/blockchain-users/{id}")
    @Timed
    public ResponseEntity<BlockchainUser> getBlockchainUser(@PathVariable Long id) {
        log.debug("REST request to get BlockchainUser : {}", id);
        BlockchainUser blockchainUser = blockchainUserService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(blockchainUser));
    }

    /**
     * DELETE  /blockchain-users/:id : delete the "id" blockchainUser.
     *
     * @param id the id of the blockchainUser to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/blockchain-users/{id}")
    @Timed
    public ResponseEntity<Void> deleteBlockchainUser(@PathVariable Long id) {
        log.debug("REST request to delete BlockchainUser : {}", id);
        blockchainUserService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
