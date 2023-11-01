package in.codifi.admin.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_ADMIN_PREFERENCE")
public class AdminPreferenceEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "admin_key")
	private String adminKey;

	@Column(name = "admin_value")
	private int adminValue;

	@Column(name = "SOURCE")
	private String source;

}
