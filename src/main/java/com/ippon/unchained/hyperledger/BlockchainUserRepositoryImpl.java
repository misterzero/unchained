package com.ippon.unchained.hyperledger;

import com.ippon.unchained.domain.BlockchainUser;
import com.ippon.unchained.repository.BlockchainUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.apache.commons.compress.utils.CharsetNames.UTF_8;

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
    public BlockchainUser findOne(Long id) {
        // Payload for a BlockchainUser should be in the format...
        // {"id":1, "active":[{"id":"1","votes":"1"},{"id":"2","votes":"1"}], "history":[{"id":"3"},{"id":"4"}]}
        try {
            Util.out("Now query chain code for the value of %s.",id);
            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
            queryByChaincodeRequest.setArgs(new String[] {"query",id.toString()});
            queryByChaincodeRequest.setFcn("invoke");
            queryByChaincodeRequest.setChaincodeID(chainCodeID);
            Map<String, byte[]> tm2 = new HashMap<>();
            ObjectMapper mapper = new ObjectMapper();

            // First set up an empty currentPoll to return
            BlockchainUser currentUser = new BlockchainUser();
            currentUser.setId(id);
            currentUser.setName("");
            currentUser.setActivePolls("");
            currentUser.setInactivePolls("");

            tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
            queryByChaincodeRequest.setTransientMap(tm2);

            Collection<ProposalResponse> queryProposals = chain.queryByChaincode(queryByChaincodeRequest, chain.getPeers());
            for (ProposalResponse proposalResponse : queryProposals) {
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                    LOGGER.error("failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() +
                        ". Messages: " + proposalResponse.getMessage()
                        + ". Was verified : " + proposalResponse.isVerified());
                } else {
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    Util.out("Query payload of %s from peer %s returned %s", id, proposalResponse.getPeer().getName(), payload);
                    LOGGER.info("Payload :"+ payload);
                    // TODO: NEEDS TO BE TESTED - OPTIONS MAY NOT GET STORED PROPERLY
                    // mapper stores values first by matching instance member names, then by matching setter/getter names
                    currentUser = mapper.readValue(payload, BlockchainUser.class);
                }
            }

            return currentUser;
        } catch (Exception e) {
            Util.out("Caught exception while running query");
            e.printStackTrace();
            LOGGER.error("failed during chaincode query with error : " + e.getMessage());
        }

        return null;
    }

    /**
     * TODO: Resolve code smell. Opening and closing connection repeatedly is obviously not optional,
     * but the alternative is duplicating code from above and then iterating over each id...
     * Fortunately, we likely will never call findAll, so we may avoid this issue altogether.
     * @param ids
     * @return
     */
    public List<BlockchainUser> findAll(Collection<Long> ids) {
        List<BlockchainUser> users = new ArrayList<>();
        for (Long id : ids) {
            users.add(findOne(id));
        }
        return users;
    }

    @Override
    public <S extends BlockchainUser> S save(S arg0) {
        ArrayList<S> accounts = new ArrayList<S>();
        accounts.add(arg0);
//        save(accounts);
        List<S> saved = new ArrayList<S>();
        saved = save(accounts);
        LOGGER.debug("Account 1 id: " + saved.get(0).getName());
        return saved.get(0);
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
    public <S extends BlockchainUser> List<S> save(Iterable<S> entities) {
        try {
//		    testConfig = TestConfig.getConfig();
            LOGGER.debug("Starting save ledger method");
//			SampleOrg sampleOrg;
//			HFClient client;
//            final ChainCodeID chainCodeID;
//            Chain chain;
            Collection<ProposalResponse> successful = new LinkedList<>();
            Collection<ProposalResponse> failed = new LinkedList<>();


            client.setUserContext(TestConfigHelper.getSampleOrgByName("peerOrg1", testSampleOrgs).getPeerAdmin());

            ///////////////
            /// Send transaction proposal to all peers
            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
            transactionProposalRequest.setChaincodeID(chainCodeID);
            transactionProposalRequest.setFcn("invoke");
            // NOTE: this line was previously
            // transactionProposalRequest.setArgs(new String[] {"move", "a", "b", entities.iterator().next().getValue().toString()});
            // but .getValue() doesn't resolve when run against a Poll object, so it was changed to the line below.
            // It still likely does not complete as expected, but it passes a test for compiling this way!
            transactionProposalRequest.setArgs(new String[] {"addUser", entities.iterator().next().getName()});

            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(StandardCharsets.UTF_8));
            tm2.put("method", "TransactionProposalRequest".getBytes(StandardCharsets.UTF_8));
            tm2.put("result", ":)".getBytes(StandardCharsets.UTF_8));  /// This should be returned see chaincode.
            transactionProposalRequest.setTransientMap(tm2);

            Util.out("sending transactionProposal to all peers with arguments: move(a,b,100)");

            Collection<ProposalResponse> transactionPropResp = chain.sendTransactionProposal(transactionProposalRequest, chain.getPeers());
            for (ProposalResponse response : transactionPropResp) {
                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
                    Util.out("Successful transaction proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName());
                    successful.add(response);
                } else {
                    failed.add(response);
                }
            }
            Util.out("Received %d transaction proposal responses. Successful+verified: %d . failed: %d",
                transactionPropResp.size(), successful.size(), failed.size());
            if (failed.size() > 0) {
                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
                LOGGER.error("Not enough endorsers for invoke(move a,b,100):" + failed.size() + " endorser error: " +
                    firstTransactionProposalResponse.getMessage() +
                    ". Was verified: " + firstTransactionProposalResponse.isVerified());
            }
            Util.out("Successfully received transaction proposal responses.");

            ProposalResponse resp = transactionPropResp.iterator().next();
            byte[] x = resp.getChainCodeActionResponsePayload(); // This is the data returned by the chaincode.
            String resultAsString = null;
            if (x != null) {
                resultAsString = new String(x, "UTF-8");
            }
            //      LOGGER.info(":)", resultAsString);

            LOGGER.info("Chaincode result status = " + resp.getChainCodeActionResponseStatus()); //Chaincode's status.

            TxReadWriteSetInfo readWriteSetInfo = resp.getChainCodeActionResponseReadWriteSetInfo();
            //See blockwaler below how to transverse this

            LOGGER.info("Reset count = " + readWriteSetInfo.getNsRwsetCount());

            ChainCodeID cid = resp.getChainCodeID();

            LOGGER.info("Chaincode path " + cid.getPath());
            LOGGER.info("Chaincode name " + cid.getName());
            LOGGER.info("Chaincode version " + cid.getVersion());

            ////////////////////////////
            // Send Transaction Transaction to orderer
            Util.out("Sending chain code transaction(move a,b,100) to orderer.");
            chain.sendTransaction(successful).get(6, TimeUnit.SECONDS);
            return (List<S>) entities;

        } catch (Exception e) {
            Util.out("Caught an exception while invoking chaincode");
            e.printStackTrace();
            LOGGER.error("failed invoking chaincode with error : " + e.getMessage());
        }

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
