package in.codifi.holdings.config;

import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import in.codifi.cache.model.ClientDetailsModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.holdings.entity.primary.PoaEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HazelcastConfig {

	public static HazelcastConfig HazelcastConfig = null;
	private HazelcastInstance hz = null;

	public static HazelcastConfig getInstance() {
		if (HazelcastConfig == null) {
			HazelcastConfig = new HazelcastConfig();

		}
		return HazelcastConfig;
	}

	public HazelcastInstance getHz() {
		if (hz == null) {
			ClientConfig clientConfig = new ClientConfig();
			clientConfig.setClusterName(ConfigProvider.getConfig().getValue("config.app.hazel.cluster", String.class));
			clientConfig.getNetworkConfig()
					.addAddress(ConfigProvider.getConfig().getValue("config.app.hazel.address", String.class));
			hz = HazelcastClient.newHazelcastClient(clientConfig);
		}
		return hz;
	}

	private Map<String, ContractMasterModel> contractMaster = getHz().getMap("contractMaster");
	private Map<String, String> restUserSession = getHz().getMap("restUserSession");

	// Log
	private Map<String, String> userSessionOtp = getHz().getMap("userSessionOtp");

	// to get edis details
	private Map<String, ClientDetailsModel> clientDetails = getHz().getMap("clientDetails");
	
	private Map<String, PoaEntity> poaEntity = getHz().getMap("poaEntity");
	

}
