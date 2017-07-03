package com.ippon.unchained.hyperledger;

import com.ippon.unchained.domain.BlockchainUser;
import com.ippon.unchained.repository.BlockchainUserRepository;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.Chain;
import org.hyperledger.fabric.sdk.ChainCodeID;
import org.hyperledger.fabric.sdk.HFClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * Created by slaughter on 6/28/17.
 */
@Repository
public class BlockchainUserRepositoryImpl implements BlockchainUserRepository {

    private static final Logger LOGGER = Logger.getLogger(LedgerAccountRepositoryImpl.class);

    @Autowired
    HFClient client;

    @Autowired
    Chain chain;

    @Autowired
    ChainCodeID chainCodeID;

    @Autowired
    private Collection<SampleOrg> testSampleOrgs;

    @Override
    public <S extends BlockchainUser> S save(S s) {
        return null;
    }

    @Override
    public BlockchainUser findOne(Long aLong) {
        return null;
    }

    @Override
    public boolean exists(Long aLong) {
        return false;
    }

    @Override
    public List<BlockchainUser> findAll() {
        return null;
    }

    @Override
    public List<BlockchainUser> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<BlockchainUser> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<BlockchainUser> findAll(Iterable<Long> iterable) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {

    }

    @Override
    public void delete(BlockchainUser blockchainUser) {

    }

    @Override
    public void delete(Iterable<? extends BlockchainUser> iterable) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void flush() {

    }

    @Override
    public void deleteInBatch(Iterable<BlockchainUser> iterable) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public BlockchainUser getOne(Long aLong) {
        return null;
    }

    @Override
    public <S extends BlockchainUser> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends BlockchainUser> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends BlockchainUser> S saveAndFlush(S s) {
        return null;
    }

    @Override
    public <S extends BlockchainUser> List<S> save(Iterable<S> iterable) {
        return null;
    }

    @Override
    public <S extends BlockchainUser> S findOne(Example<S> example) {
        return null;
    }

    @Override
    public <S extends BlockchainUser> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends BlockchainUser> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends BlockchainUser> boolean exists(Example<S> example) {
        return false;
    }
}
