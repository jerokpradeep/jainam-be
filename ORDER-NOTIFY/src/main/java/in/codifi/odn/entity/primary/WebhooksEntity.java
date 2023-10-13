package in.codifi.odn.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_WEBHOOKS")
@NamedQuery(name = "TBL_WEBHOOKS.listSubscribedUsers", 
query = "SELECT DISTINCT c.callbackUrl FROM TBL_WEBHOOKS c WHERE c.userId = :userId and c.isSubscribed = 1 and c.activeStatus = 1")
public class WebhooksEntity extends CommonEntity implements Serializable {

 
    private static final long serialVersionUID = 1L;

    @Column(name = "USER_ID")
    private String userId;


    @Column(name = "APP_CODE")
    private String appCode;


    @Column(name = "CALL_BACK_URL")
    private String callbackUrl;
    
   
    @Column(name = "ERROR_COUNT")
    private int errorCount;
    
    
    @Column(name = "IS_SUBSCRIBED")
    private int isSubscribed;
    
    @Column(name = "IP")
    private String ip;

 

}