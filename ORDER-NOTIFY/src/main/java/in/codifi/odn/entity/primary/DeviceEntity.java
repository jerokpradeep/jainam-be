package in.codifi.odn.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_DEVICE_MAPPING")
@NamedQuery(name = "TBL_DEVICE_MAPPING.listUniqueDeviceIds", 
query = "SELECT DISTINCT c.deviceId FROM TBL_DEVICE_MAPPING c WHERE c.userId = :userId and c.activeStatus = 1")
public class DeviceEntity extends CommonEntity implements Serializable {

 
    private static final long serialVersionUID = 1L;

    @Column(name = "USER_ID")
    private String userId;


    @Column(name = "USER_NAME")
    private String userName;

 

    @Column(name = "DEVICE_ID")
    private String deviceId;

 

    @Column(name = "DEVICE_TYPE")
    private String deviceType;

 

}