
package in.codifi.odn.model;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PushNotification {

    public String click_action;
  
    public String collapse_key;
  
    public Notification notification;
   
    public Data data;
   
    public String to;
    
}
