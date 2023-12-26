package in.codifi.common.config;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApplicationScoped
public class RestServiceProperties {

	@ConfigProperty(name = "appconfig.odin.url.base")
	private String baseUrl;

	@ConfigProperty(name = "config.odin.url.activityDataFII")
	private String activityDataFii;

	@ConfigProperty(name = "config.odin.url.activityDataDII")
	private String activityDataDii;

	@ConfigProperty(name = "config.odin.url.getWorldIndicesData")
	private String worldIndicesData;

	@ConfigProperty(name = "config.analysis.service.baseurl")
	private String analysisBaseUrl;

	@ConfigProperty(name = "config.analysis.service.url.topgainers")
	private String topGainersUrl;

	@ConfigProperty(name = "config.analysis.service.url.fiftytwoweekhigh")
	private String fiftyTwoWeekHigh;

	@ConfigProperty(name = "config.analysis.service.url.fiftyweeklow")
	private String fiftyTwoWeekLow;

	@ConfigProperty(name = "config.analysis.service.url.riders")
	private String riders;

	@ConfigProperty(name = "config.analysis.service.url.draggers")
	private String draggers;

	@ConfigProperty(name = "config.analysis.service.url.topvolume")
	private String topVolume;

	@ConfigProperty(name = "config.analysis.service.url.meanreversion")
	private String meanreversion;

	@ConfigProperty(name = "config.analysis.service.url.profitloss")
	private String profitLossbaseUrl;

	@ConfigProperty(name = "config.analysis.service.url.profitlossendpoint")
	private String profitLossData;

	@ConfigProperty(name = "config.analysis.service.url.sheetdata")
	private String sheetbaseUrl;

	@ConfigProperty(name = "config.analysis.service.url.sheetdataendpoint")
	private String sheetData;

	@ConfigProperty(name = "config.analysis.service.url.companydataendpoints")
	private String companyData;

	@ConfigProperty(name = "config.analysis.service.url.companydata")
	private String companyDataBaseUrl;

	@ConfigProperty(name = "config.analysis.service.url.indexDetails")
	private String IndexDetails;

	@ConfigProperty(name = "appconfig.odin.xApiKey")
	private String xApiKey;

	@ConfigProperty(name = "config.analysis.service.url.hotPursuitData")
	private String hotPursuitData;

	@ConfigProperty(name = "config.analysis.service.url.sectorlist")
	private String sectorlist;

	@ConfigProperty(name = "config.analysis.service.url.sectorwisenewsdata")
	private String sectorwisenewsdata;

	@ConfigProperty(name = "config.analysis.service.url.annoucementsdata")
	private String annoucementsdata;

	@ConfigProperty(name = "config.analysis.service.url.movingaverage")
	private String movingAverage;

	@ConfigProperty(name = "config.analysis.service.url.putcallratio")
	private String putCallRatio;

	@ConfigProperty(name = "config.analysis.service.url.scripwisenewsdata")
	private String scripwiseNewsDataUrl;

	@ConfigProperty(name = "config.analysis.service.url.scripwisenewsdataurl.endpoints")
	private String scripwiseNewsDataUrlEndpoints;

	@ConfigProperty(name = "config.analysis.service.url.getdividentdata")
	private String dividentData;

	@ConfigProperty(name = "config.analysis.service.url.getdividentdata.endpoints")
	private String dividentDataEndpoints;

	@ConfigProperty(name = "config.analysis.service.url.gethealthtotalscoreurl")
	private String healthTotalScoreUrl;

	@ConfigProperty(name = "config.analysis.service.url.gethealthtotalscoreurl.endpoints")
	private String healthTotalScoreEndpoints;

	@ConfigProperty(name = "config.analysis.service.url.exchangeMessages")
	private String exchangeMessages;

	@ConfigProperty(name = "config.analysis.service.url.brokerMessages")
	private String brokerMessages;

	@ConfigProperty(name = "config.analysis.service.url.supportandresistance")
	private String supportAndResistance;

}
