package in.codifi.client.config;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import in.codifi.auth.ws.model.login.LoginRestResp;
import in.codifi.cache.model.ClientDetailsModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.cache.model.PreferenceModel;
import in.codifi.cache.model.ProductMasterModel;
import in.codifi.client.entity.primary.PinStartBarEntity;
import in.codifi.client.entity.primary.PreferenceEntity;
import in.codifi.client.entity.primary.PreferenceMappingEntity;
import in.codifi.client.entity.primary.StockSuggestEntity;
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

	private Map<String, String> restUserSession = getHz().getMap("restUserSession");
	private Map<String, List<StockSuggestEntity>> stocks = getHz().getMap("stocks");
	private Map<String, List<PreferenceModel>> perference = getHz().getMap("perference");

	private Map<String, List<PreferenceMappingEntity>> masterPerference = getHz().getMap("masterPerference");
//	private Map<String, QuickAuthRespModel> userSessionDetails = getHz().getMap("userSessionDetails");
	private Map<String, List<ProductMasterModel>> productTypes = getHz().getMap("productTypes");
	private Map<String, List<ProductMasterModel>> orderTypes = getHz().getMap("orderTypes");
	private Map<String, List<ProductMasterModel>> priceTypes = getHz().getMap("priceTypes");
	private Map<String, ClientDetailsModel> clientDetails = getHz().getMap("clientDetails");
	private Map<String, List<PinStartBarEntity>> pinTostartbar = getHz().getMap("pinTostartbar");
	private Map<String, ContractMasterModel> contractMaster = getHz().getMap("contractMaster");

	private Map<String, LoginRestResp> userSessionDetails = getHz().getMap("userSessionDetails");

}
