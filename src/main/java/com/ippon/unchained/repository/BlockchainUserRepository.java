package com.ippon.unchained.repository;

import com.ippon.unchained.domain.BlockchainUser;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the BlockchainUser entity.
 */
@SuppressWarnings("unused")
@Repository
public interface BlockchainUserRepository extends JpaRepository<BlockchainUser,Long> {
    
}
