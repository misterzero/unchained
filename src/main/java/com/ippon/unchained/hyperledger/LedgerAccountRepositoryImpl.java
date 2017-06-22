package com.ippon.unchained.hyperledger;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.*;

import org.apache.log4j.Logger;
import org.hyperledger.fabric.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import com.ippon.unchained.domain.LedgerAccount;
import com.ippon.unchained.repository.LedgerAccountRepository;

@Repository
public class LedgerAccountRepositoryImpl implements LedgerAccountRepository {

    private static final Logger LOGGER = Logger.getLogger(LedgerAccountRepositoryImpl.class);

    @Autowired
    HFClient client;

    @Autowired
    Chain chain;

    @Autowired
    ChainCodeID chainCodeID;


    /**
     * Call the hyperledger query function
     */
//    @Override
    //TODO come back and adjust for findAll
    public List<LedgerAccount> findAll(String testFixturePath, String chainName) {

        try {

//		    Util.waitOnFabric(0);


//         assertTrue(transactionEvent.isValid()); // must be valid to be here.
      //      Util.out("Finished transaction with transaction id %s", transactionEvent.getTransactionID());
//         testTxID = transactionEvent.getTransactionID(); // used in the channel queries later

            ////////////////////////////
            // Send Query Proposal to all peers
            //

       //     chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {
            Util.out("Now query chain code for the value of b.");
            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
            queryByChaincodeRequest.setArgs(new String[] {"query","a"});
            queryByChaincodeRequest.setFcn("invoke");
            queryByChaincodeRequest.setChaincodeID(chainCodeID);


            Map<String, byte[]> tm2 = new HashMap<>();
            tm2.put("HyperLedgerFabric", "QueryByChaincodeRequest:JavaSDK".getBytes(UTF_8));
            tm2.put("method", "QueryByChaincodeRequest".getBytes(UTF_8));
            queryByChaincodeRequest.setTransientMap(tm2);

            Collection<ProposalResponse> queryProposals = chain.queryByChaincode(queryByChaincodeRequest, chain.getPeers());
            List<LedgerAccount> ret = new ArrayList<>();
            for (ProposalResponse proposalResponse : queryProposals) {
                LedgerAccount ledgeracc = new LedgerAccount();
                if (!proposalResponse.isVerified() || proposalResponse.getStatus() != ProposalResponse.Status.SUCCESS) {
                    LOGGER.error("failed query proposal from peer " + proposalResponse.getPeer().getName() + " status: " + proposalResponse.getStatus() +
                        ". Messages: " + proposalResponse.getMessage()
                        + ". Was verified : " + proposalResponse.isVerified());
                } else {
                    String payload = proposalResponse.getProposalResponse().getResponse().getPayload().toStringUtf8();
                    Util.out("Query payload of b from peer %s returned %s", proposalResponse.getPeer().getName(), payload);
                    LOGGER.info("Payload :"+ payload);
                    ledgeracc.setName("Fake Name");
                    ledgeracc.setId(1L);
                    ledgeracc.setValue(Integer.parseInt(payload));
                    ret.add(ledgeracc);
                }
            }

            return ret;
           // });
        } catch (Exception e) {
            Util.out("Caught exception while running query");
            e.printStackTrace();
            LOGGER.error("failed during chaincode query with error : " + e.getMessage());
        }


        return null;
    }

    @Override
    public List<LedgerAccount> findAll(){
        return findAll("","");
    }

    @Override
    public List<LedgerAccount> findAll(Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LedgerAccount> findAll(Iterable<Long> ids) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Call the hyperledger invoke function
     */
    @Override
    public <S extends LedgerAccount> List<S> save(Iterable<S> entities) {
//		try {
//			SampleOrg sampleOrg;
//			HFClient client;
//            final ChainCodeID chainCodeID;
//            Chain chain;
//            Collection<ProposalResponse> successful = new LinkedList<>();
//            Collection<ProposalResponse> failed = new LinkedList<>();
//
//
//            client.setUserContext(sampleOrg.getUser(TESTUSER_1_NAME));
//
//            ///////////////
//            /// Send transaction proposal to all peers
//            TransactionProposalRequest transactionProposalRequest = client.newTransactionProposalRequest();
//            transactionProposalRequest.setChaincodeID(chainCodeID);
//            transactionProposalRequest.setFcn("invoke");
//            transactionProposalRequest.setArgs(new String[] {"move", "a", "b", "100"});
//
//            Map<String, byte[]> tm2 = new HashMap<>();
//            tm2.put("HyperLedgerFabric", "TransactionProposalRequest:JavaSDK".getBytes(UTF_8));
//            tm2.put("method", "TransactionProposalRequest".getBytes(UTF_8));
//            tm2.put("result", ":)".getBytes(UTF_8));  /// This should be returned see chaincode.
//            transactionProposalRequest.setTransientMap(tm2);
//
//            Util.out("sending transactionProposal to all peers with arguments: move(a,b,100)");
//
//            Collection<ProposalResponse> transactionPropResp = chain.sendTransactionProposal(transactionProposalRequest, chain.getPeers());
//            for (ProposalResponse response : transactionPropResp) {
//                if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
//                    Util.out("Successful transaction proposal response Txid: %s from peer %s", response.getTransactionID(), response.getPeer().getName());
//                    successful.add(response);
//                } else {
//                    failed.add(response);
//                }
//            }
//            Util.out("Received %d transaction proposal responses. Successful+verified: %d . failed: %d",
//                    transactionPropResp.size(), successful.size(), failed.size());
//            if (failed.size() > 0) {
//                ProposalResponse firstTransactionProposalResponse = failed.iterator().next();
//                LOGGER.error("Not enough endorsers for invoke(move a,b,100):" + failed.size() + " endorser error: " +
//                        firstTransactionProposalResponse.getMessage() +
//                        ". Was verified: " + firstTransactionProposalResponse.isVerified());
//            }
//            Util.out("Successfully received transaction proposal responses.");
//
//            ProposalResponse resp = transactionPropResp.iterator().next();
//            byte[] x = resp.getChainCodeActionResponsePayload(); // This is the data returned by the chaincode.
//            String resultAsString = null;
//            if (x != null) {
//                resultAsString = new String(x, "UTF-8");
//            }
//      //      LOGGER.info(":)", resultAsString);
//
//            LOGGER.info("Chaincode result status = " + resp.getChainCodeActionResponseStatus()); //Chaincode's status.
//
//            TxReadWriteSetInfo readWriteSetInfo = resp.getChainCodeActionResponseReadWriteSetInfo();
//            //See blockwaler below how to transverse this
//
//            LOGGER.info("Reset count = " + readWriteSetInfo.getNsRwsetCount());
//
//            ChainCodeID cid = resp.getChainCodeID();
//
//            LOGGER.info("Chaincode path " + cid.getPath());
//            LOGGER.info("Chaincode name " + cid.getName());
//            LOGGER.info("Chaincode version " + cid.getVersion());
//
//            ////////////////////////////
//            // Send Transaction Transaction to orderer
//            Util.out("Sending chain code transaction(move a,b,100) to orderer.");
//           //  return chain.sendTransaction(successful).get(testConfig.getTransactionWaitTime(), TimeUnit.SECONDS);
//            return null;
//
//        } catch (Exception e) {
//            Util.out("Caught an exception while invoking chaincode");
//            e.printStackTrace();
//            LOGGER.error("failed invoking chaincode with error : " + e.getMessage());
//        }

        return null;
    }

    @Override
    public void flush() {
        // TODO Auto-generated method stub

    }

    @Override
    public <S extends LedgerAccount> S saveAndFlush(S entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteInBatch(Iterable<LedgerAccount> entities) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAllInBatch() {
        // TODO Auto-generated method stub

    }

    @Override
    public LedgerAccount getOne(Long id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends LedgerAccount> List<S> findAll(Example<S> example) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends LedgerAccount> List<S> findAll(Example<S> example, Sort sort) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Page<LedgerAccount> findAll(Pageable arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete(Long arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(LedgerAccount arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void delete(Iterable<? extends LedgerAccount> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean exists(Long arg0) {
        // TODO Auto-generated method stub
        return false;
    }

//	@Override
//	public LedgerAccount findOne(Long arg0) {
//		// TODO Auto-generated method stub
//		return null;
//	}

    public LedgerAccount findOne(String name) {

        try {
            Util.out("Now query chain code for the value of b.");
            QueryByChaincodeRequest queryByChaincodeRequest = client.newQueryProposalRequest();
            queryByChaincodeRequest.setArgs(new String[] {"query",name});
            queryByChaincodeRequest.setFcn("invoke");
            queryByChaincodeRequest.setChaincodeID(chainCodeID);
            int value = 0;
            Long id = (long) 1;

            Map<String, byte[]> tm2 = new HashMap<>();
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
                    Util.out("Query payload of b from peer %s returned %s", proposalResponse.getPeer().getName(), payload);
                    LOGGER.info("Payload :"+ payload);
                    value = Integer.parseInt(payload);
                }
            }
            
            LedgerAccount currentLedgerAccount = new LedgerAccount();
            currentLedgerAccount.setId(id);
            currentLedgerAccount.setName(name);
            currentLedgerAccount.setValue(value);
            
            return currentLedgerAccount;
        } catch (Exception e) {
            Util.out("Caught exception while running query");
            e.printStackTrace();
            LOGGER.error("failed during chaincode query with error : " + e.getMessage());
        }
        
        return null;
    }

    @Override
    public <S extends LedgerAccount> S save(S arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends LedgerAccount> long count(Example<S> arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public <S extends LedgerAccount> boolean exists(Example<S> arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <S extends LedgerAccount> Page<S> findAll(Example<S> arg0, Pageable arg1) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <S extends LedgerAccount> S findOne(Example<S> arg0) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public LedgerAccount findOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
}
