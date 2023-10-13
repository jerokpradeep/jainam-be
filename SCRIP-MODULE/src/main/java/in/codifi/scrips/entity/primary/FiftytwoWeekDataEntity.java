package in.codifi.scrips.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_52WK_HL")
public class FiftytwoWeekDataEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@Column(name = "ID")
	private int id;

	@Column(name = "EXCH")
	private String exch;

	@Column(name = "TOKEN")
	private String token;

	@Column(name = "LOW")
	private String low;

	@Column(name = "HIGH")
	private String high;

}
