package in.codifi.common.model.response;

import java.util.List;

import in.codifi.cache.model.AnalysisRespModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScannersRespModel {

	private List<AnalysisRespModel> topgainers;
	private List<AnalysisRespModel> topLosers;
	private List<AnalysisRespModel> fiftyTwoWeekHigh;
	private List<AnalysisRespModel> fiftyTwoWeekLow;
	private List<AnalysisRespModel> riders;
	private List<AnalysisRespModel> draggers;
	private List<AnalysisRespModel> topVolume;
	private List<AnalysisRespModel> meanReversion;
}
