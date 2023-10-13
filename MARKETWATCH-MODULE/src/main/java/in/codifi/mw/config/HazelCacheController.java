package in.codifi.mw.config;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;
import org.json.simple.JSONObject;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import in.codifi.cache.model.ContractMasterModel;
import in.codifi.mw.entity.primary.PredefinedMwEntity;
import in.codifi.mw.entity.primary.TickerTapeEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HazelCacheController {
	public static HazelCacheController HazelCacheController = null;
	private HazelcastInstance hz = null;

	public static HazelCacheController getInstance() {
		if (HazelCacheController == null) {
			HazelCacheController = new HazelCacheController();

		}
		return HazelCacheController;
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

	private Map<String, List<JSONObject>> mwListByUserId = getHz().getMap("mwListByUserId");
	private Map<String, ContractMasterModel> contractMaster = getHz().getMap("contractMaster");
	private Map<String, List<PredefinedMwEntity>> predefinedMwList = getHz().getMap("predefinedMwList");
	private Map<String, List<TickerTapeEntity>> TickerTapeList = getHz().getMap("TickerTapeList");


}
