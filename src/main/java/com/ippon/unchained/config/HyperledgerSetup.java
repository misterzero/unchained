package com.ippon.unchained.config;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.hyperledger.fabric.sdk.BlockEvent;
import org.hyperledger.fabric.sdk.Chain;
import org.hyperledger.fabric.sdk.ChainCodeID;
import org.hyperledger.fabric.sdk.ChaincodeEndorsementPolicy;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.InstallProposalRequest;
import org.hyperledger.fabric.sdk.InstantiateProposalRequest;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.hyperledger.fabric.sdk.ProposalResponse;
import org.hyperledger.fabric.sdk.exception.TransactionEventException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ippon.unchained.hyperledger.SampleOrg;
import com.ippon.unchained.hyperledger.TestConfig;
import com.ippon.unchained.hyperledger.TestConfigHelper;
import com.ippon.unchained.hyperledger.Util;

@Configuration
public class HyperledgerSetup {
	private final Logger log = LoggerFactory.getLogger(HyperledgerSetup.class);

	private static final TestConfig testConfig = TestConfig.getConfig();

	private static final String TEST_FIXTURES_PATH = "src/main/resources";

	private final int gossipWaitTime = testConfig.getGossipWaitTime();

	private static final String CHAIN_CODE_NAME = "example_cc_go";
	private static final String CHAIN_CODE_PATH = "github.com/example_cc";
	private static final String CHAIN_CODE_VERSION = "1";

	private static final String FOO_CHAIN_NAME = "foo";
	private static final String BAR_CHAIN_NAME = "bar";

	private ChainCodeID chainCodeID;

	@Autowired
	private HFClient client;

	@Autowired
	private Chain chain;

	String testTxID = null; // save the CC invoke TxID and use in queries

	private final TestConfigHelper configHelper = new TestConfigHelper();

	@Autowired
	private Collection<SampleOrg> testSampleOrgs;

	public void setupUsers() {
		try {

			// setup orgs

			////////////////////////////
			// Setup client

			// Create instance of client.
		//	HFClient client = getClient();

			// client.setMemberServices(peerOrg1FabricCA);

			////////////////////////////
			// Set up USERS

			// Persistence is not part of SDK. Sample file store is for
			// demonstration purposes only!
			// MUST be replaced with more robust application implementation
			// (Database, LDAP)

			// sampleStoreFile.deleteOnExit();

			// SampleUser can be any implementation that implements
			// org.hyperledger.fabric.sdk.User Interface

			////////////////////////////
			// get users for all orgs

			for (SampleOrg sampleOrg : testSampleOrgs) {


				// and jump tall blockchains in a single leap!
			}
		} catch (Exception e) {
			e.printStackTrace();

			log.error(e.getMessage());
		}

	}

	@PostConstruct
	public void installChaincode() {

		try {

			boolean installChainCode = true;
			// SampleOrg sampleOrg = getTestSampleOrgs();

			int delta = 100;
			final String chainName = chain.getName();
			Util.out("Running Chain %s", chainName);
			chain.setTransactionWaitTime(testConfig.getTransactionWaitTime());
			chain.setDeployWaitTime(testConfig.getDeployWaitTime());

			Collection<Peer> channelPeers = chain.getPeers();
			Collection<Orderer> orderers = chain.getOrderers();
			final ChainCodeID ccId;
			Collection<ProposalResponse> responses;
			Collection<ProposalResponse> successful = new LinkedList<>();
			Collection<ProposalResponse> failed = new LinkedList<>();

			ccId = ChainCodeID.newBuilder().setName(CHAIN_CODE_NAME).setVersion(CHAIN_CODE_VERSION)
					.setPath(CHAIN_CODE_PATH).build();
			this.setChainCodeId(ccId);
			if (installChainCode) {
				////////////////////////////
				// Install Proposal Request
				//

				client.setUserContext(TestConfigHelper.getSampleOrgByName("peerOrg1", testSampleOrgs).getPeerAdmin());

				Util.out("Creating install proposal");

				InstallProposalRequest installProposalRequest = client.newInstallProposalRequest();
				installProposalRequest.setChaincodeID(chainCodeID);

				if (FOO_CHAIN_NAME.equals(chain.getName())) {
					// on foo chain install from directory.

					//// For GO language and serving just a single user,
					//// chaincodeSource is mostly likely the users GOPATH
					installProposalRequest.setChaincodeSourceLocation(new File(TEST_FIXTURES_PATH + "/gocc/sample1"));
				} else {
					// On bar chain install from an input stream.

					installProposalRequest.setChainCodeInputStream(Util.generateTarGzInputStream(
							(Paths.get(TEST_FIXTURES_PATH, "/gocc/sample1", "src", CHAIN_CODE_PATH).toFile()),
							Paths.get("src", CHAIN_CODE_PATH).toString()));

				}

				installProposalRequest.setChaincodeVersion(CHAIN_CODE_VERSION);

				Util.out("Sending install proposal");

				////////////////////////////
				// only a client from the same org as the peer can issue an
				//////////////////////////// install request
				int numInstallProposal = 0;
				SampleOrg testOrg = TestConfigHelper.getSampleOrgByName("peerOrg1", testSampleOrgs);

				Set<Peer> peersFromOrg = testOrg.getPeers();
				numInstallProposal = numInstallProposal + peersFromOrg.size();
				responses = client.sendInstallProposal(installProposalRequest, peersFromOrg);

				for (ProposalResponse response : responses) {
					if (response.getStatus() == ProposalResponse.Status.SUCCESS) {
						Util.out("Successful install proposal response Txid: %s from peer %s",
								response.getTransactionID(), response.getPeer().getName());
						successful.add(response);
					} else {
						failed.add(response);
					}
				}
				Util.out("Received %d install proposal responses. Successful+verified: %d . failed: %d",
						numInstallProposal, successful.size(), failed.size());

				if (failed.size() > 0) {
					ProposalResponse first = failed.iterator().next();
					log.error("Not enough endorsers for install :" + successful.size() + ".  " + first.getMessage());
				}
			}
			// Instantiate chain code.
			InstantiateProposalRequest instantiateProposalRequest = client.newInstantiationProposalRequest();
			instantiateProposalRequest.setProposalWaitTime(60000);
			instantiateProposalRequest.setChaincodeID(chainCodeID);
			instantiateProposalRequest.setFcn("init");
			instantiateProposalRequest.setArgs(new String[] { "1", "3"}); // admin = 3, system = 1
			Map<String, byte[]> tm = new HashMap<>();
			tm.put("HyperLedgerFabric", "InstantiateProposalRequest:JavaSDK".getBytes(UTF_8));
			tm.put("method", "InstantiateProposalRequest".getBytes(UTF_8));
			instantiateProposalRequest.setTransientMap(tm);

			/*
			 * policy OR(Org1MSP.member, Org2MSP.member) meaning 1 signature
			 * from someone in either Org1 or Org2 See README.md Chaincode
			 * endorsement policies section for more details.
			 */
			ChaincodeEndorsementPolicy chaincodeEndorsementPolicy = new ChaincodeEndorsementPolicy();
			chaincodeEndorsementPolicy.fromYamlFile(new File(TEST_FIXTURES_PATH + "/chaincodeendorsementpolicy.yaml"));
			instantiateProposalRequest.setChaincodeEndorsementPolicy(chaincodeEndorsementPolicy);

			Util.out("Sending instantiateProposalRequest to all peers with arguments: admin and user");
			successful.clear();
			failed.clear();

			responses = chain.sendInstantiationProposal(instantiateProposalRequest, chain.getPeers());
			for (ProposalResponse response : responses) {
				if (response.isVerified() && response.getStatus() == ProposalResponse.Status.SUCCESS) {
					successful.add(response);
					Util.out("Succesful instantiate proposal response Txid: %s from peer %s",
							response.getTransactionID(), response.getPeer().getName());
				} else {
					failed.add(response);
				}
			}
			Util.out("Received %d instantiate proposal responses. Successful+verified: %d . failed: %d",
					responses.size(), successful.size(), failed.size());
			if (failed.size() > 0) {
				ProposalResponse first = failed.iterator().next();
				log.error("Not enough endorsers for instantiate :" + successful.size() + "endorser failed with "
						+ first.getMessage() + ". Was verified:" + first.isVerified());
			}

			///////////////
			/// Send instantiate transaction to orderer
			Util.out("Sending instantiateTransaction to orderer with admin and user");
			chain.sendTransaction(successful, orderers).thenApply(transactionEvent -> {

				Util.waitOnFabric(0);

				log.info("transaction valid = " + transactionEvent.isValid());
				Util.out("Finished instantiate transaction with transaction id %s",
						transactionEvent.getTransactionID());
				return null;
			}).exceptionally(e -> {
				if (e instanceof TransactionEventException) {
					BlockEvent.TransactionEvent te = ((TransactionEventException) e).getTransactionEvent();
					if (te != null) {
						log.error(format("Transaction with txid %s failed. %s", te.getTransactionID(), e.getMessage()));
					}
				}
				log.error(format("Test failed with %s exception %s", e.getClass().getName(), e.getMessage()));

				return null;
			}).get(testConfig.getTransactionWaitTime(), TimeUnit.SECONDS);
		} catch (Exception e) {
			Util.out("Caught an exception running chain %s", chain.getName());
			e.printStackTrace();
			log.error("Test failed with error : " + e.getMessage());
		}
	}

	@Bean
	public ChainCodeID getChainCodeID() {
		return this.chainCodeID;
	}
	private void setChainCodeId(ChainCodeID ccId) {
		this.chainCodeID = ccId;

	}





}
