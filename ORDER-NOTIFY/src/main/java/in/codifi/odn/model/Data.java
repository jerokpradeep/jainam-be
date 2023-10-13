
package in.codifi.odn.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Data {

    @JsonProperty("type")
    public String type;
    
    @JsonProperty("message")
    public String message;
    
    @JsonProperty("title")
    public String title;
    
}
