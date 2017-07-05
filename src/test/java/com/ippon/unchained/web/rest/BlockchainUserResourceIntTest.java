package com.ippon.unchained.web.rest;

import com.ippon.unchained.UnchainedApp;

import com.ippon.unchained.domain.BlockchainUser;
import com.ippon.unchained.repository.BlockchainUserRepository;
import com.ippon.unchained.service.BlockchainUserService;
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
 * Test class for the BlockchainUserResource REST controller.
 *
 * @see BlockchainUserResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = UnchainedApp.class)
public class BlockchainUserResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ACTIVE_POLLS = "AAAAAAAAAA";
    private static final String UPDATED_ACTIVE_POLLS = "BBBBBBBBBB";

    private static final String DEFAULT_INACTIVE_POLLS = "AAAAAAAAAA";
    private static final String UPDATED_INACTIVE_POLLS = "BBBBBBBBBB";

    @Autowired
    private BlockchainUserRepository blockchainUserRepository;

    @Autowired
    private BlockchainUserService blockchainUserService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBlockchainUserMockMvc;

    private BlockchainUser blockchainUser;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BlockchainUserResource blockchainUserResource = new BlockchainUserResource(blockchainUserService);
        this.restBlockchainUserMockMvc = MockMvcBuilders.standaloneSetup(blockchainUserResource)
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
    public static BlockchainUser createEntity(EntityManager em) {
        BlockchainUser blockchainUser = new BlockchainUser()
            .name(DEFAULT_NAME)
            .activePolls(DEFAULT_ACTIVE_POLLS)
            .inactivePolls(DEFAULT_INACTIVE_POLLS);
        return blockchainUser;
    }

    @Before
    public void initTest() {
        blockchainUser = createEntity(em);
    }

    @Test
    @Transactional
    public void createBlockchainUser() throws Exception {
        int databaseSizeBeforeCreate = blockchainUserRepository.findAll().size();

        // Create the BlockchainUser
        restBlockchainUserMockMvc.perform(post("/api/blockchain-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blockchainUser)))
            .andExpect(status().isCreated());

        // Validate the BlockchainUser in the database
        List<BlockchainUser> blockchainUserList = blockchainUserRepository.findAll();
        assertThat(blockchainUserList).hasSize(databaseSizeBeforeCreate + 1);
        BlockchainUser testBlockchainUser = blockchainUserList.get(blockchainUserList.size() - 1);
        assertThat(testBlockchainUser.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testBlockchainUser.getActivePolls()).isEqualTo(DEFAULT_ACTIVE_POLLS);
        assertThat(testBlockchainUser.getInactivePolls()).isEqualTo(DEFAULT_INACTIVE_POLLS);
    }

    @Test
    @Transactional
    public void createBlockchainUserWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = blockchainUserRepository.findAll().size();

        // Create the BlockchainUser with an existing ID
        blockchainUser.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBlockchainUserMockMvc.perform(post("/api/blockchain-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blockchainUser)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BlockchainUser> blockchainUserList = blockchainUserRepository.findAll();
        assertThat(blockchainUserList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllBlockchainUsers() throws Exception {
        // Initialize the database
        blockchainUserRepository.saveAndFlush(blockchainUser);

        // Get all the blockchainUserList
        restBlockchainUserMockMvc.perform(get("/api/blockchain-users?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(blockchainUser.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].activePolls").value(hasItem(DEFAULT_ACTIVE_POLLS.toString())))
            .andExpect(jsonPath("$.[*].inactivePolls").value(hasItem(DEFAULT_INACTIVE_POLLS.toString())));
    }

    @Test
    @Transactional
    public void getBlockchainUser() throws Exception {
        // Initialize the database
        blockchainUserRepository.saveAndFlush(blockchainUser);

        // Get the blockchainUser
        restBlockchainUserMockMvc.perform(get("/api/blockchain-users/{id}", blockchainUser.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(blockchainUser.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.activePolls").value(DEFAULT_ACTIVE_POLLS.toString()))
            .andExpect(jsonPath("$.inactivePolls").value(DEFAULT_INACTIVE_POLLS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBlockchainUser() throws Exception {
        // Get the blockchainUser
        restBlockchainUserMockMvc.perform(get("/api/blockchain-users/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBlockchainUser() throws Exception {
        // Initialize the database
        blockchainUserService.save(blockchainUser);

        int databaseSizeBeforeUpdate = blockchainUserRepository.findAll().size();

        // Update the blockchainUser
        BlockchainUser updatedBlockchainUser = blockchainUserRepository.findOne(blockchainUser.getId());
        updatedBlockchainUser
            .name(UPDATED_NAME)
            .activePolls(UPDATED_ACTIVE_POLLS)
            .inactivePolls(UPDATED_INACTIVE_POLLS);

        restBlockchainUserMockMvc.perform(put("/api/blockchain-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBlockchainUser)))
            .andExpect(status().isOk());

        // Validate the BlockchainUser in the database
        List<BlockchainUser> blockchainUserList = blockchainUserRepository.findAll();
        assertThat(blockchainUserList).hasSize(databaseSizeBeforeUpdate);
        BlockchainUser testBlockchainUser = blockchainUserList.get(blockchainUserList.size() - 1);
        assertThat(testBlockchainUser.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testBlockchainUser.getActivePolls()).isEqualTo(UPDATED_ACTIVE_POLLS);
        assertThat(testBlockchainUser.getInactivePolls()).isEqualTo(UPDATED_INACTIVE_POLLS);
    }

    @Test
    @Transactional
    public void updateNonExistingBlockchainUser() throws Exception {
        int databaseSizeBeforeUpdate = blockchainUserRepository.findAll().size();

        // Create the BlockchainUser

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBlockchainUserMockMvc.perform(put("/api/blockchain-users")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(blockchainUser)))
            .andExpect(status().isCreated());

        // Validate the BlockchainUser in the database
        List<BlockchainUser> blockchainUserList = blockchainUserRepository.findAll();
        assertThat(blockchainUserList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBlockchainUser() throws Exception {
        // Initialize the database
        blockchainUserService.save(blockchainUser);

        int databaseSizeBeforeDelete = blockchainUserRepository.findAll().size();

        // Get the blockchainUser
        restBlockchainUserMockMvc.perform(delete("/api/blockchain-users/{id}", blockchainUser.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BlockchainUser> blockchainUserList = blockchainUserRepository.findAll();
        assertThat(blockchainUserList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BlockchainUser.class);
        BlockchainUser blockchainUser1 = new BlockchainUser();
        blockchainUser1.setId(1L);
        BlockchainUser blockchainUser2 = new BlockchainUser();
        blockchainUser2.setId(blockchainUser1.getId());
        assertThat(blockchainUser1).isEqualTo(blockchainUser2);
        blockchainUser2.setId(2L);
        assertThat(blockchainUser1).isNotEqualTo(blockchainUser2);
        blockchainUser1.setId(null);
        assertThat(blockchainUser1).isNotEqualTo(blockchainUser2);
    }
}
