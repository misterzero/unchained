package com.ippon.unchained.repository;

import com.ippon.unchained.domain.LedgerAccount;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the LedgerAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LedgerAccountRepository extends JpaRepository<LedgerAccount,Long> {

	LedgerAccount findOne(String id);
    
}
