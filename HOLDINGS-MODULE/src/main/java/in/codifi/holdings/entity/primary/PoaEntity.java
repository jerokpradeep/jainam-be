package in.codifi.holdings.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "CRM_CLIENT_DP")
public class PoaEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private int id;

	@Column(name = "CLIENT_CODE")
	private String clientCode;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "DPID")
	private String dpid;

	@Column(name = "DP_CODE")
	private String dpCode;

	@Column(name = "CL_DEFAULT")
	private String clDefault;

	@Column(name = "POA")
	private String poa;

	@Column(name = "CREATED_ON")
	@CreationTimestamp
	private java.sql.Timestamp createdon;

	@Column(name = "CREATED_BY")
	private String createdBy;

}
