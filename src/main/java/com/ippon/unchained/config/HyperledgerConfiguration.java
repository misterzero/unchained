package com.ippon.unchained.config;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import javax.annotation.PostConstruct;

import org.hyperledger.fabric.sdk.Chain;
import org.hyperledger.fabric.sdk.ChainConfiguration;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.ippon.unchained.hyperledger.SampleOrg;
import com.ippon.unchained.hyperledger.TestConfig;
import com.ippon.unchained.hyperledger.TestConfigHelper;
import com.ippon.unchained.hyperledger.Util;

@Configuration
public class HyperledgerConfiguration {
	private final Logger log = LoggerFactory.getLogger(HyperledgerConfiguration.class);

	private static final TestConfig testConfig = TestConfig.getConfig();
	private static final String TEST_FIXTURES_PATH = "src/main/resources";

	private static final String FOO_CHAIN_NAME = "foo";

	String testTxID = null; // save the CC invoke TxID and use in queries

	private final TestConfigHelper configHelper = new TestConfigHelper();

	@PostConstruct
	public void checkConfig() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, MalformedURLException {
		System.out.println("\n\n\nRUNNING: End2endIT.\n");
		configHelper.clearConfig();
		configHelper.customizeConfig();

	}

	public void clearConfig() {
		try {
			configHelper.clearConfig();
		} catch (Exception e) {
		}
	}

	@Bean
	@Scope(value = "singleton")
	public SampleOrg getSampleOrg() {

		SampleOrg sampleOrg = testConfig.getIntegrationTestsSampleOrg("peerOrg1");

		return sampleOrg;
	}

	@Bean
	@Scope(value = "singleton")
	public HFClient getClient() {

		HFClient client = HFClient.createNewInstance();

		try {
			client.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
		} catch (CryptoException | InvalidArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return client;
	}

	@Bean
	@Scope(value = "singleton")
	public Collection<SampleOrg> getTestSampleOrgs() {
		Collection<SampleOrg> testSampleOrgs;
		try {

			testSampleOrgs = testConfig.getIntegrationTestsSampleOrgs();
			// Set up hfca for each sample org

			for (SampleOrg sampleOrg : testSampleOrgs) {
				sampleOrg.setCAClient(
						HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
			}
			return testSampleOrgs;

		} catch (Exception e) {
			log.error(e.getMessage());
			return Collections.emptySet();
		}
	}

	@Bean
	@Scope(value = "singleton")
	public Chain getChain() {
		Chain chain = null;
		
			Collection<Orderer> orderers = new LinkedList<>();
			SampleOrg sampleOrg = getSampleOrg();

			HFClient client = getClient();
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
				chain = client.newChain(name, anOrderer, chainConfiguration,
						client.getChainConfigurationSignature(chainConfiguration, sampleOrg.getPeerAdmin()));

				Util.out("Created chain %s", name);
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		
		return chain;
	}

}
