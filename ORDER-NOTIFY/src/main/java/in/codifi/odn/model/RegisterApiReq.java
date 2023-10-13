package in.codifi.odn.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"jRequestID",
"jToken",
"jConnMode"
})
@Getter
@Setter
@Builder
public class RegisterApiReq {

@JsonProperty("jRequestID")
public String jRequestID;
@JsonProperty("jToken")
public String jToken;
@JsonProperty("jConnMode")
public Integer jConnMode;

}