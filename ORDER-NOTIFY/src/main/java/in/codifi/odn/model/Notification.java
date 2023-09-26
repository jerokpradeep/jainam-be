
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
    "title_color",
    "title",
    "body",
    "channel_id",
    "android_channel_id"
})
public class Notification {

    @JsonProperty("title_color")
    public String titleColor;
   
    @JsonProperty("title")
    public String title;
   
    @JsonProperty("body")
    public String body;
   
    @JsonProperty("channel_id")
    public String channelId;
   
    @JsonProperty("android_channel_id")
    public String androidChannelId;

}
