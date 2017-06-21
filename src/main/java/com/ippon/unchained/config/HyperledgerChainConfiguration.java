package com.ippon.unchained.config;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.hyperledger.fabric.sdk.Chain;
import org.hyperledger.fabric.sdk.ChainConfiguration;
import org.hyperledger.fabric.sdk.EventHub;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.Peer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.ippon.unchained.hyperledger.SampleOrg;
import com.ippon.unchained.hyperledger.TestConfig;
import com.ippon.unchained.hyperledger.TestConfigHelper;
import com.ippon.unchained.hyperledger.Util;

@Configuration
public class HyperledgerChainConfiguration {
	private final Logger log = LoggerFactory.getLogger(HyperledgerChainConfiguration.class);

	private TestConfig testConfig = TestConfig.getConfig();
	private static final String TEST_FIXTURES_PATH = "src/main/resources";

	private static final String FOO_CHAIN_NAME = "foo";

	
	@Autowired
	private HFClient client;
	@Autowired
	Collection<SampleOrg> testSampleOrgs;

	@Bean
	@Scope(value = "singleton")
	public Chain getChain() {
		testConfig = TestConfig.getConfig();
		Chain chain = null;
		Collection<Orderer> orderers = new LinkedList<>();
		SampleOrg sampleOrg = TestConfigHelper.getSampleOrgByName("peerOrg1", testSampleOrgs);

		String name = FOO_CHAIN_NAME;
		try {
			for (String orderName : sampleOrg.getOrdererNames()) {
				orderers.add(client.newOrderer(orderName, sampleOrg.getOrdererLocation(orderName),
						testConfig.getOrdererProperties(orderName)));
			}

			// Just pick the first orderer in the list to create the chain.

			Orderer anOrderer = orderers.iterator().next();
			orderers.remove(anOrderer);

			ChainConfiguration chainConfiguration = new ChainConfiguration(
					new File(TEST_FIXTURES_PATH + "/e2e-2Orgs/channel/" + name + ".tx"));

			// Only peer Admin org
			client.setUserContext(sampleOrg.getPeerAdmin());

			// Create chain that has only one signer that is this orgs peer
			// admin. If chain creation policy needed more signature they
			// would
			// need to be added too.
			byte[] signature = client.getChainConfigurationSignature(chainConfiguration, sampleOrg.getPeerAdmin());
			if (anOrderer != null) {
				chain = client.newChain(name, anOrderer, chainConfiguration, signature);
			} else {
				log.error("AnOrderer is NULL");
				System.exit(0);
			}
			Util.out("Created chain %s", name);
			
			for (String peerName : sampleOrg.getPeerNames()) {
	            String peerLocation = sampleOrg.getPeerLocation(peerName);

	            Properties peerProperties = testConfig.getPeerProperties(peerName);//test properties for peer.. if any.
	            if (peerProperties == null) {
	                peerProperties = new Properties();
	            }
	            //Example of setting specific options on grpc's ManagedChannelBuilder
	            peerProperties.put("grpc.ManagedChannelBuilderOption.maxInboundMessageSize", 9000000);

	            Peer peer = client.newPeer(peerName, peerLocation, peerProperties);
	            chain.joinPeer(peer);
	            Util.out("Peer %s joined chain %s", peerName, name);
	            sampleOrg.addPeer(peer);
	        }

	        for (Orderer orderer : orderers) { //add remaining orderers if any.
	            chain.addOrderer(orderer);
	        }

	        for (String eventHubName : sampleOrg.getEventHubNames()) {
	            EventHub eventHub = client.newEventHub(eventHubName, sampleOrg.getEventHubLocation(eventHubName),
	                    testConfig.getEventHubProperties(eventHubName));
	            chain.addEventHub(eventHub);
	        }

	        chain.initialize();

	        Util.out("Finished initialization chain %s", name);
	        
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		return chain;
	}

}
