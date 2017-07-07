package com.ippon.unchained.service;

import com.ippon.unchained.domain.BlockchainUser;
import java.util.List;

/**
 * Service Interface for managing BlockchainUser.
 */
public interface BlockchainUserService {

    /**
     * Save a blockchainUser.
     *
     * @param blockchainUser the entity to save
     * @return the persisted entity
     */
    BlockchainUser save(BlockchainUser blockchainUser);

    /**
     *  Get all the blockchainUsers.
     *
     *  @return the list of entities
     */
    List<BlockchainUser> findAll();

    /**
     *  Get the "id" blockchainUser.
     *
     *  @param id the id of the entity
     *  @return the entity
     */
    BlockchainUser findOne(String id);

    /**
     *  Delete the "id" blockchainUser.
     *
     *  @param id the id of the entity
     */
    void delete(Long id);
}
