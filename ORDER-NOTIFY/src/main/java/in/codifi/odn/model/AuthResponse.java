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
"jResponseID",
"jStatuscode",
"jErrorString",
"jToken"
})
@Getter
@Setter
@Builder
public class AuthResponse {

@JsonProperty("jRequestID")
public String jRequestID;
@JsonProperty("jResponseID")
public Long jResponseID;
@JsonProperty("jStatuscode")
public String jStatuscode;
@JsonProperty("jErrorString")
public String jErrorString;
@JsonProperty("jToken")
public String jToken;

}