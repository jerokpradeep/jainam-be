package in.codifi.admin.config;

import java.util.List;
import java.util.Map;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import org.eclipse.microprofile.config.ConfigProvider;

import in.codifi.admin.entity.AdminPreferenceEntity;
import in.codifi.admin.entity.HoldingsJSONEntity;
import in.codifi.admin.entity.PositionAvgPriceEntity;
import in.codifi.admin.model.response.UserPerferencePreDefModel;
import in.codifi.admin.ws.model.kc.GetUserInfoResp;
import in.codifi.cache.model.AdminIndexModel;
import in.codifi.cache.model.AdminPreferenceModel;
import in.codifi.cache.model.ContractMasterModel;
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

	private Map<String, String> positionContract = getHz().getMap("positionContract");
	private Map<String, String> holdingsContract = getHz().getMap("holdingsContract");
	private Map<String, List<PositionAvgPriceEntity>> positionsAvgPrice = getHz().getMap("positionsAvgPrice");
	private Map<String, String> keycloakAdminSession = getHz().getMap("keycloakAdminSession");
	private Map<String, HoldingsJSONEntity> holdingsData = getHz().getMap("holdingsData");
	private Map<String, ContractMasterModel> contractMaster = getHz().getMap("contractMaster");

	private Map<String, AdminPreferenceModel> adminPreferenceModel = getHz().getMap("adminPreferenceModel");
	private Map<String, UserPerferencePreDefModel> userPerferenceModel = getHz().getMap("userPerferenceModel");
	
	private Map<String, List<AdminPreferenceEntity>> adminPreferenceEntity = getHz().getMap("adminPreferenceEntity");

	private Map<String, List<GetUserInfoResp>> keycloakUserDetails = getHz().getMap("keycloakUserDetails");

	private Map<String, List<AdminIndexModel>> indexValue = getHz().getMap("indexValue");
}
