package com.ippon.unchained.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.ippon.unchained.domain.LedgerAccount;

import com.ippon.unchained.repository.LedgerAccountRepository;
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
 * REST controller for managing LedgerAccount.
 */
@RestController
@RequestMapping("/api")
public class LedgerAccountResource {

    private final Logger log = LoggerFactory.getLogger(LedgerAccountResource.class);

    private static final String ENTITY_NAME = "ledgerAccount";

    private final LedgerAccountRepository ledgerAccountRepository;

    public LedgerAccountResource(LedgerAccountRepository ledgerAccountRepository) {
        this.ledgerAccountRepository = ledgerAccountRepository;
    }

    /**
     * POST  /ledger-accounts : Create a new ledgerAccount.
     *
     * @param ledgerAccount the ledgerAccount to create
     * @return the ResponseEntity with status 201 (Created) and with body the new ledgerAccount, or with status 400 (Bad Request) if the ledgerAccount has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/ledger-accounts")
    @Timed
    public ResponseEntity<LedgerAccount> createLedgerAccount(@RequestBody LedgerAccount ledgerAccount) throws URISyntaxException {
        log.debug("REST request to save LedgerAccount : {}", ledgerAccount);
        if (ledgerAccount.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(ENTITY_NAME, "idexists", "A new ledgerAccount cannot already have an ID")).body(null);
        }
        LedgerAccount result = ledgerAccountRepository.save(ledgerAccount);
        return ResponseEntity.created(new URI("/api/ledger-accounts/" + result.getName()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getName().toString()))
            .body(result);
    }

    /**
     * PUT  /ledger-accounts : Updates an existing ledgerAccount.
     *
     * @param ledgerAccount the ledgerAccount to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated ledgerAccount,
     * or with status 400 (Bad Request) if the ledgerAccount is not valid,
     * or with status 500 (Internal Server Error) if the ledgerAccount couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/ledger-accounts")
    @Timed
    public ResponseEntity<LedgerAccount> updateLedgerAccount(@RequestBody LedgerAccount ledgerAccount) throws URISyntaxException {
        log.debug("REST request to update LedgerAccount : {}", ledgerAccount);
        if (ledgerAccount.getId() == null) {
            return createLedgerAccount(ledgerAccount);
        }
        LedgerAccount result = ledgerAccountRepository.save(ledgerAccount);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, ledgerAccount.getId().toString()))
            .body(result);
    }

    /**
     * GET  /ledger-accounts : get all the ledgerAccounts.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of ledgerAccounts in body
     */
    @GetMapping("/ledger-accounts")
    @Timed
    public List<LedgerAccount> getAllLedgerAccounts() {
        log.debug("REST request to get all LedgerAccounts");
        return ledgerAccountRepository.findAll();
    }

    /**
     * GET  /ledger-accounts/:id : get the "id" ledgerAccount.
     *
     * @param id the id of the ledgerAccount to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the ledgerAccount, or with status 404 (Not Found)
     */
//    @GetMapping("/ledger-accounts/{id}")
//    @Timed
//    public ResponseEntity<LedgerAccount> getLedgerAccount(@PathVariable Long id) {
//        log.debug("REST request to get LedgerAccount : {}", id);
//        LedgerAccount ledgerAccount = ledgerAccountRepository.findOne(id);
//        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(ledgerAccount));
//    }

    @GetMapping("/ledger-accounts/{id}")
    @Timed
    public ResponseEntity<LedgerAccount> getLedgerAccount(@PathVariable String id) {
        log.debug("REST request to get LedgerAccount : {}", id);
        LedgerAccount ledgerAccount = ledgerAccountRepository.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(ledgerAccount));
    }

    /**
     * DELETE  /ledger-accounts/:id : delete the "id" ledgerAccount.
     *
     * @param id the id of the ledgerAccount to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/ledger-accounts/{id}")
    @Timed
    public ResponseEntity<Void> deleteLedgerAccount(@PathVariable Long id) {
        log.debug("REST request to delete LedgerAccount : {}", id);
        ledgerAccountRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
