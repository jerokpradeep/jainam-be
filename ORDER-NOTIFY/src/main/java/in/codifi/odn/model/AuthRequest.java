	package in.codifi.odn.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonPropertyOrder({
"jAPIKey",
"jSecretKey",
"jRequestID"
})
@Getter
@Setter
@Builder
public class AuthRequest {

@JsonProperty("jAPIKey")
public String jAPIKey;
@JsonProperty("jSecretKey")
public String jSecretKey;
@JsonProperty("jRequestID")
public String jRequestID;

}