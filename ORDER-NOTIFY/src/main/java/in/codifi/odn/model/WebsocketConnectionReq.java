package in.codifi.odn.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"jMessageType",
"jAPIKey",
"jSecretKey",
"jToken",
"jRequestID"
})
@Getter
@Setter
@Builder
public class WebsocketConnectionReq {

@JsonProperty("jMessageType")
public String jMessageType;
@JsonProperty("jAPIKey")
public String jAPIKey;
@JsonProperty("jSecretKey")
public String jSecretKey;
@JsonProperty("jToken")
public String jToken;
@JsonProperty("jRequestID")
public String jRequestID;

}