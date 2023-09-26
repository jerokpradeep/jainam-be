package in.codifi.holdings.model.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class EdisReqModel {

	private String userCode;
	private String sellQty;
	private String segId;
	private String token;
	private String depository;
	private String summaryMktSegId;
	private String productType;
	private String orderType;// AMO OR NOT

	private String iSinNO;
	private String iSinName;
	private List<EdisHoldModel> edisHoldList;

}
