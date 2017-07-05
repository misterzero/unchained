package com.ippon.unchained.service.impl;

import com.ippon.unchained.service.BlockchainUserService;
import com.ippon.unchained.domain.BlockchainUser;
import com.ippon.unchained.repository.BlockchainUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service Implementation for managing BlockchainUser.
 */
@Service
@Transactional
public class BlockchainUserServiceImpl implements BlockchainUserService{

    private final Logger log = LoggerFactory.getLogger(BlockchainUserServiceImpl.class);

    private final BlockchainUserRepository blockchainUserRepository;

    public BlockchainUserServiceImpl(BlockchainUserRepository blockchainUserRepository) {
        this.blockchainUserRepository = blockchainUserRepository;
    }

    /**
     * Save a blockchainUser.
     *
     * @param blockchainUser the entity to save
     * @return the persisted entity
     */
    @Override
    public BlockchainUser save(BlockchainUser blockchainUser) {
        log.debug("Request to save BlockchainUser : {}", blockchainUser);
        return blockchainUserRepository.save(blockchainUser);
    }

    /**
     *  Get all the blockchainUsers.
     *
     *  @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<BlockchainUser> findAll() {
        log.debug("Request to get all BlockchainUsers");
        return blockchainUserRepository.findAll();
    }

    /**
     *  Get one blockchainUser by id.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public BlockchainUser findOne(String id) {
        log.debug("Request to get BlockchainUser : {}", id);
        return blockchainUserRepository.findOne(id);
    }

    /**
     *  Delete the  blockchainUser by id.
     *
     *  @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete BlockchainUser : {}", id);
        blockchainUserRepository.delete(id);
    }
}
