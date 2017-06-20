package com.ippon.unchained.web.rest;

import com.ippon.unchained.UnchainedApp;

import com.ippon.unchained.domain.LedgerAccount;
import com.ippon.unchained.repository.LedgerAccountRepository;
import com.ippon.unchained.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the LedgerAccountResource REST controller.
 *
 * @see LedgerAccountResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UnchainedApp.class)
public class LedgerAccountResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_VALUE = 1;
    private static final Integer UPDATED_VALUE = 2;

    @Autowired
    private LedgerAccountRepository ledgerAccountRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restLedgerAccountMockMvc;

    private LedgerAccount ledgerAccount;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        LedgerAccountResource ledgerAccountResource = new LedgerAccountResource(ledgerAccountRepository);
        this.restLedgerAccountMockMvc = MockMvcBuilders.standaloneSetup(ledgerAccountResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LedgerAccount createEntity(EntityManager em) {
        LedgerAccount ledgerAccount = new LedgerAccount()
            .name(DEFAULT_NAME)
            .value(DEFAULT_VALUE);
        return ledgerAccount;
    }

    @Before
    public void initTest() {
        ledgerAccount = createEntity(em);
    }

    @Test
    @Transactional
    public void createLedgerAccount() throws Exception {
        int databaseSizeBeforeCreate = ledgerAccountRepository.findAll().size();

        // Create the LedgerAccount
        restLedgerAccountMockMvc.perform(post("/api/ledger-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ledgerAccount)))
            .andExpect(status().isCreated());

        // Validate the LedgerAccount in the database
        List<LedgerAccount> ledgerAccountList = ledgerAccountRepository.findAll();
        assertThat(ledgerAccountList).hasSize(databaseSizeBeforeCreate + 1);
        LedgerAccount testLedgerAccount = ledgerAccountList.get(ledgerAccountList.size() - 1);
        assertThat(testLedgerAccount.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testLedgerAccount.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    @Transactional
    public void createLedgerAccountWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ledgerAccountRepository.findAll().size();

        // Create the LedgerAccount with an existing ID
        ledgerAccount.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restLedgerAccountMockMvc.perform(post("/api/ledger-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ledgerAccount)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<LedgerAccount> ledgerAccountList = ledgerAccountRepository.findAll();
        assertThat(ledgerAccountList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllLedgerAccounts() throws Exception {
        // Initialize the database
        ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get all the ledgerAccountList
        restLedgerAccountMockMvc.perform(get("/api/ledger-accounts?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ledgerAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].value").value(hasItem(DEFAULT_VALUE)));
    }

    @Test
    @Transactional
    public void getLedgerAccount() throws Exception {
        // Initialize the database
        ledgerAccountRepository.saveAndFlush(ledgerAccount);

        // Get the ledgerAccount
        restLedgerAccountMockMvc.perform(get("/api/ledger-accounts/{id}", ledgerAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ledgerAccount.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.value").value(DEFAULT_VALUE));
    }

    @Test
    @Transactional
    public void getNonExistingLedgerAccount() throws Exception {
        // Get the ledgerAccount
        restLedgerAccountMockMvc.perform(get("/api/ledger-accounts/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateLedgerAccount() throws Exception {
        // Initialize the database
        ledgerAccountRepository.saveAndFlush(ledgerAccount);
        int databaseSizeBeforeUpdate = ledgerAccountRepository.findAll().size();

        // Update the ledgerAccount
        LedgerAccount updatedLedgerAccount = ledgerAccountRepository.findOne(ledgerAccount.getId());
        updatedLedgerAccount
            .name(UPDATED_NAME)
            .value(UPDATED_VALUE);

        restLedgerAccountMockMvc.perform(put("/api/ledger-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedLedgerAccount)))
            .andExpect(status().isOk());

        // Validate the LedgerAccount in the database
        List<LedgerAccount> ledgerAccountList = ledgerAccountRepository.findAll();
        assertThat(ledgerAccountList).hasSize(databaseSizeBeforeUpdate);
        LedgerAccount testLedgerAccount = ledgerAccountList.get(ledgerAccountList.size() - 1);
        assertThat(testLedgerAccount.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testLedgerAccount.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    @Transactional
    public void updateNonExistingLedgerAccount() throws Exception {
        int databaseSizeBeforeUpdate = ledgerAccountRepository.findAll().size();

        // Create the LedgerAccount

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restLedgerAccountMockMvc.perform(put("/api/ledger-accounts")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ledgerAccount)))
            .andExpect(status().isCreated());

        // Validate the LedgerAccount in the database
        List<LedgerAccount> ledgerAccountList = ledgerAccountRepository.findAll();
        assertThat(ledgerAccountList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteLedgerAccount() throws Exception {
        // Initialize the database
        ledgerAccountRepository.saveAndFlush(ledgerAccount);
        int databaseSizeBeforeDelete = ledgerAccountRepository.findAll().size();

        // Get the ledgerAccount
        restLedgerAccountMockMvc.perform(delete("/api/ledger-accounts/{id}", ledgerAccount.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<LedgerAccount> ledgerAccountList = ledgerAccountRepository.findAll();
        assertThat(ledgerAccountList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LedgerAccount.class);
        LedgerAccount ledgerAccount1 = new LedgerAccount();
        ledgerAccount1.setId(1L);
        LedgerAccount ledgerAccount2 = new LedgerAccount();
        ledgerAccount2.setId(ledgerAccount1.getId());
        assertThat(ledgerAccount1).isEqualTo(ledgerAccount2);
        ledgerAccount2.setId(2L);
        assertThat(ledgerAccount1).isNotEqualTo(ledgerAccount2);
        ledgerAccount1.setId(null);
        assertThat(ledgerAccount1).isNotEqualTo(ledgerAccount2);
    }
}
