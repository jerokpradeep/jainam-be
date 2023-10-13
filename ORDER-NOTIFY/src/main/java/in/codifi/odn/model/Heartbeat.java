package in.codifi.odn.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonPropertyOrder({
"jMessageType"
})
public class Heartbeat {

@JsonProperty("jMessageType")
public String jMessageType;

}