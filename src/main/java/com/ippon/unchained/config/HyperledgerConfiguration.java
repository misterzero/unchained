package com.ippon.unchained.config;

import static java.lang.String.format;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.hyperledger.fabric.sdk.Chain;
import org.hyperledger.fabric.sdk.ChainConfiguration;
import org.hyperledger.fabric.sdk.HFClient;
import org.hyperledger.fabric.sdk.Orderer;
import org.hyperledger.fabric.sdk.exception.CryptoException;
import org.hyperledger.fabric.sdk.exception.InvalidArgumentException;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.ippon.unchained.hyperledger.SampleOrg;
import com.ippon.unchained.hyperledger.SampleStore;
import com.ippon.unchained.hyperledger.SampleUser;
import com.ippon.unchained.hyperledger.TestConfig;
import com.ippon.unchained.hyperledger.TestConfigHelper;
import com.ippon.unchained.hyperledger.Util;

@Configuration
public class HyperledgerConfiguration {
	private final Logger log = LoggerFactory.getLogger(HyperledgerConfiguration.class);

	private static final TestConfig testConfig = TestConfig.getConfig();
	private static final String TEST_ADMIN_NAME = "admin";
	private static final String TESTUSER_1_NAME = "user1";
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
		List<SampleOrg> testSampleOrgs = new ArrayList<SampleOrg>();
		File sampleStoreFile = new File(System.getProperty("java.io.tmpdir") + "/HFCSampletest.properties");
		if (sampleStoreFile.exists()) { // For testing start fresh
			sampleStoreFile.delete();
		}

		final SampleStore sampleStore = new SampleStore(sampleStoreFile);
		try {

			Collection<SampleOrg> testSampleOrgsTemp = testConfig.getIntegrationTestsSampleOrgs();
			// Set up hfca for each sample org

			for (SampleOrg sampleOrg : testSampleOrgsTemp) {
				sampleOrg.setCAClient(
						HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
				
				HFCAClient ca = sampleOrg.getCAClient();
				final String orgName = sampleOrg.getName();
				final String mspid = sampleOrg.getMSPID();
				if (ca != null) {
					ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
				} else {
					//TODO fix code smell
					sampleOrg.setCAClient(
							HFCAClient.createNewInstance(sampleOrg.getCALocation(), sampleOrg.getCAProperties()));
					ca = sampleOrg.getCAClient();
					log.error("CA WAS NULL");
					ca.setCryptoSuite(CryptoSuite.Factory.getCryptoSuite());
					// System.exit(0);
				}
				SampleUser admin = sampleStore.getMember(TEST_ADMIN_NAME, orgName);
				if (!admin.isEnrolled()) { // Preregistered admin only needs to
											// be enrolled with Fabric caClient.
					admin.setEnrollment(ca.enroll(admin.getName(), "adminpw"));
					admin.setMPSID(mspid);
				}

				sampleOrg.setAdmin(admin); // The admin of this org --

				SampleUser user = sampleStore.getMember(TESTUSER_1_NAME, sampleOrg.getName());
				if (!user.isRegistered()) { // users need to be registered AND
											// enrolled
					RegistrationRequest rr = new RegistrationRequest(user.getName(), "org1.department1");
					user.setEnrollmentSecret(ca.register(rr, admin));
				}
				if (!user.isEnrolled()) {
					user.setEnrollment(ca.enroll(user.getName(), user.getEnrollmentSecret()));
					user.setMPSID(mspid);
				}
				sampleOrg.addUser(user); // Remember user belongs to this Org

				final String sampleOrgName = sampleOrg.getName();
				final String sampleOrgDomainName = sampleOrg.getDomainName();

				SampleUser peerOrgAdmin = sampleStore.getMember(sampleOrgName + "Admin", sampleOrgName,
						sampleOrg.getMSPID(),
						findFile_sk(Paths.get(testConfig.getTestChannlePath(), "crypto-config/peerOrganizations/",
								sampleOrgDomainName, format("/users/Admin@%s/msp/keystore", sampleOrgDomainName))
								.toFile()),
						Paths.get(testConfig.getTestChannlePath(), "crypto-config/peerOrganizations/",
								sampleOrgDomainName, format("/users/Admin@%s/msp/signcerts/Admin@%s-cert.pem",
										sampleOrgDomainName, sampleOrgDomainName))
								.toFile());

				sampleOrg.setPeerAdmin(peerOrgAdmin); // A special user that can
														// crate channels, join
														// peers and install
														// chain code
				testSampleOrgs.add(sampleOrg);
			}
			return testSampleOrgs;

		} catch (Exception e) {
			log.error(e.getMessage());
			return Collections.emptyList();
		}
	}

	

	File findFile_sk(File directory) {

		File[] matches = directory.listFiles((dir, name) -> name.endsWith("_sk"));

		if (null == matches) {
			throw new RuntimeException(
					format("Matches returned null does %s directory exist?", directory.getAbsoluteFile().getName()));
		}

		if (matches.length != 1) {
			throw new RuntimeException(format("Expected in %s only 1 sk file but found %d",
					directory.getAbsoluteFile().getName(), matches.length));
		}

		return matches[0];

	}
}
