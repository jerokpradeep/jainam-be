package in.codifi.auth.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_USER_LOGGEDIN_REPORT")
public class UserLoggedInEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "VISITORS")
	private String visitors;

	@Column(name = "VENDOR")
	private String vendor;
}
