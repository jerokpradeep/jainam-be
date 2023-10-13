package in.codifi.odn.model;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonPropertyOrder({
"MessageType",
"ManagerID",
"OrderNumber",
"CliOrderNumber",
"Exchange",
"ScripCode",
"Symbol",
"Series",
"InstrumentName",
"ExpiryDate",
"StrikePrice",
"Option_Type",
"Buy_Sell",
"OrderOriginalQty",
"PendingQty",
"TradedQTY",
"DQ",
"DQRemaining",
"OrderPrice",
"TriggerPrice",
"OrderType",
"MarketType",
"OrderStatus",
"Reason",
"OrderValidity",
"Days",
"ProCli",
"UserID",
"PartCode",
"OrderEntryTime",
"LastModifiedTime",
"CP_ID",
"UCC",
"Product",
"InitiatedBy",
"ModifiedBy",
"InitiatedByUserId",
"ModifiedByUserId",
"LegIndicator",
"UserRemarks",
"Misc",
"SpreadFlag",
"SpreadPrice",
"AMOOrderID",
"GTDOrderStatus",
"MessageSequenceNumber",
"UniqueCode",
"jMessageType",
"jResponseID",
"jStatuscode",
"jErrorString",
"jRequestID",
"Status"
})
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WebsocketConnectionResp {
	
private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

@JsonProperty("MessageType")
public String messageType;
@JsonProperty("ManagerID")
public String managerID;
@JsonProperty("OrderNumber")
public String orderNumber;
@JsonProperty("CliOrderNumber")
public Integer cliOrderNumber;
@JsonProperty("Exchange")
public String exchange;
@JsonProperty("ScripCode")
public String scripCode;
@JsonProperty("Symbol")
public String symbol;
@JsonProperty("Series")
public String series;
@JsonProperty("InstrumentName")
public String instrumentName;
@JsonProperty("ExpiryDate")
public String expiryDate;
@JsonProperty("StrikePrice")
public Integer strikePrice;
@JsonProperty("Option_Type")
public String optionType;
@JsonProperty("Buy_Sell")
public String buySell;
@JsonProperty("OrderOriginalQty")
public Integer orderOriginalQty;
@JsonProperty("PendingQty")
public Integer pendingQty;
@JsonProperty("TradedQTY")
public String tradedQTY;
@JsonProperty("DQ")
public Integer dq;
@JsonProperty("DQRemaining")
public Integer dQRemaining;
@JsonProperty("OrderPrice")
public Integer orderPrice;
@JsonProperty("TriggerPrice")
public Integer triggerPrice;
@JsonProperty("OrderType")
public Integer orderType;
@JsonProperty("MarketType")
public Integer marketType;
@JsonProperty("OrderStatus")
public Integer orderStatus;
@JsonProperty("Reason")
public String reason;
@JsonProperty("OrderValidity")
public String orderValidity;
@JsonProperty("Days")
public String days;
@JsonProperty("ProCli")
public String proCli;
@JsonProperty("UserID")
public String userID;
@JsonProperty("PartCode")
public String partCode;
@JsonProperty("OrderEntryTime")
public String orderEntryTime;
@JsonProperty("LastModifiedTime")
public String lastModifiedTime;
@JsonProperty("CP_ID")
public String cpId;
@JsonProperty("UCC")
public String ucc;
@JsonProperty("Product")
public String product;
@JsonProperty("InitiatedBy")
public String initiatedBy;
@JsonProperty("ModifiedBy")
public String modifiedBy;
@JsonProperty("InitiatedByUserId")
public String initiatedByUserId;
@JsonProperty("ModifiedByUserId")
public String modifiedByUserId;
@JsonProperty("LegIndicator")
public String legIndicator;
@JsonProperty("UserRemarks")
public String userRemarks;
@JsonProperty("Misc")
public String misc;
@JsonProperty("SpreadFlag")
public Integer spreadFlag;
@JsonProperty("SpreadPrice")
public Integer spreadPrice;
@JsonProperty("AMOOrderID")
public String aMOOrderID;
@JsonProperty("GTDOrderStatus")
public Integer gTDOrderStatus;
@JsonProperty("MessageSequenceNumber")
public String messageSequenceNumber;
@JsonProperty("UniqueCode")
public String uniqueCode;


@JsonProperty("jMessageType")
public String jMessageType;

@JsonProperty("jResponseID")
public String jResponseID;

@JsonProperty("jStatuscode")
public String jStatuscode;


@JsonProperty("jErrorString")
public String jErrorString;

@JsonProperty("jRequestID")
public String jRequestID;


@JsonProperty("Status")
public Integer status;

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);

}
}
