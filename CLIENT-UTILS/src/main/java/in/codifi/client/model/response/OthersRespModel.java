package in.codifi.client.model.response;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OthersRespModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("userCode")
	public String userCode;
	@JsonProperty("groupId")
	public String groupId;
	@JsonProperty("groupCode")
	public String groupCode;
	@JsonProperty("newsCategories")
	public String newsCategories;
	@JsonProperty("messageSocket")
	public String messageSocket;
	@JsonProperty("broadCastSocket")
	public String broadCastSocket;
	@JsonProperty("secondBroadCastSocket")
	public List<String> secondBroadCastSocket;
	@JsonProperty("POA")
	public String POA;
	@JsonProperty("nGTDDefault")
	public String nGTDDefault;
	@JsonProperty("nGTDMaxDays")
	public String nGTDMaxDays;
	@JsonProperty("sGTDConfigDetails")
	public String sGTDConfigDetails;
	@JsonProperty("ocToken")
	public String ocToken;
	@JsonProperty("managerIP")
	public String managerIP;
	@JsonProperty("participantCode")
	public String participantCode;
	@JsonProperty("dervParticipantCode")
	public String dervParticipantCode;
	@JsonProperty("tslAllowed")
	public List<String> tslAllowed;
	@JsonProperty("edisForMTF")
	public boolean edisForMTF;
	@JsonProperty("showJumpPriceBothLtpAndTriggerPrice")
	public boolean showJumpPriceBothLtpAndTriggerPrice;
	@JsonProperty("CFT")
	public String CFT;
	@JsonProperty("passExpDays")
	public String passExpDays;
	@JsonProperty("bAMOAllowedForUser")
	public boolean bAMOAllowedForUser;

}
