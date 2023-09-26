package in.codifi.common.config;

import java.util.List;
import java.util.Map;

import org.eclipse.microprofile.config.ConfigProvider;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;

import in.codifi.cache.model.AnalysisRespModel;
import in.codifi.cache.model.ContractMasterModel;
import in.codifi.cache.model.EventDataModel;
import in.codifi.common.entity.primary.EQSectorEntity;
import in.codifi.common.entity.primary.IndicesEntity;
import in.codifi.common.entity.primary.VersionEntity;
import in.codifi.common.model.response.AnnoucementsDataResp;
import in.codifi.common.model.response.IndexDetailsResp;
import in.codifi.common.model.response.IndexDetailsRespModify;
import in.codifi.common.model.response.NfoFutureResponseModel;
import in.codifi.common.model.response.ResponsecodeStatusModel;
import in.codifi.common.model.response.SectorDetailsRespCN;
import in.codifi.common.model.response.SectorsModel;
import in.codifi.common.utility.TestModel;
import in.codifi.common.ws.model.FIIDIIResp;
import in.codifi.common.ws.model.SupportAndResistanceRestResponse;
import in.codifi.common.ws.model.WIResponse;
import in.codifi.common.ws.model.WIResultResponse;
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

	private Map<String, String> productTypes = getHz().getMap("productTypes");
	private Map<String, String> orderTypes = getHz().getMap("orderTypes");
	private Map<String, String> priceTypes = getHz().getMap("priceTypes");

	private Map<String, String> restUserSession = getHz().getMap("restUserSession");
	private Map<String, ContractMasterModel> contractMaster = getHz().getMap("contractMaster");

	// Indices
	private Map<String, List<IndicesEntity>> indicesDetails = getHz().getMap("indicesDetails");
	private Map<String, WIResponse> worldIndicesData = getHz().getMap("worldIndicesData");

	private Map<String, IndexDetailsResp> indices = getHz().getMap("indices");
	private Map<String, WIResultResponse> worldIndices = getHz().getMap("worldIndices");
	
	private Map<String, IndexDetailsRespModify> indicesModify = getHz().getMap("indices");

	// FII DII
	private Map<String, FIIDIIResp> activityData = getHz().getMap("activityData");

	// version
	private Map<String, List<VersionEntity>> version = getHz().getMap("version");

	// Sectors
	private Map<String, List<AnalysisRespModel>> analysisData = getHz().getMap("analysisData");

	private Map<String, List<AnalysisRespModel>> analysistopGainers = getHz().getMap("analysistopGainers");

	private Map<String, List<AnalysisRespModel>> analysistopLosers = getHz().getMap("analysistopLosers");

	private Map<String, List<AnalysisRespModel>> analysisfiftyTwoWeekHigh = getHz().getMap("analysisfiftyTwoWeekHigh");

	private Map<String, List<AnalysisRespModel>> analysisfiftyTwoWeekLow = getHz().getMap("analysisfiftyTwoWeekLow");

	private Map<String, NfoFutureResponseModel> nfoFutureDetails = getHz().getMap("nfoFutureDetails");

	private Map<String, Long> analysisUpdateTime = getHz().getMap("analysisUpdateTime");
	private Map<String, String> nseTokenCache = getHz().getMap("nseTokenCache");
	private Map<String, List<EQSectorEntity>> eqSectorDetails = getHz().getMap("eqSector");
	private Map<String, List<SectorsModel>> sectors = getHz().getMap("sectors");

	private Map<String, List<SectorDetailsRespCN>> eqSectorDetailsCn = getHz().getMap("eqSectorDetailsCn");

	// AnnoucementsData
	private Map<String, List<AnnoucementsDataResp>> bse = getHz().getMap("bse");
	private Map<String, List<AnnoucementsDataResp>> nse = getHz().getMap("nse");
	private Map<String, EventDataModel> eventData = getHz().getMap("eventData");

	// SupportAndResistance
	private Map<String, SupportAndResistanceRestResponse> supportAndResistanceRestResponse = getHz()
			.getMap("supportAndResistanceRestResponse");

	private Map<String, TestModel> errorCount = getHz().getMap("errorCount");

	private Map<String, List<ResponsecodeStatusModel>> responsecodeCount = getHz().getMap("responsecodeCount");
	private Map<String, Map<String, ResponsecodeStatusModel>> responsecodeCountMap = getHz()
			.getMap("responsecodeCountMap");

}
