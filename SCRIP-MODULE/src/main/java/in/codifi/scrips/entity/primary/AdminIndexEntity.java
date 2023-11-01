package in.codifi.scrips.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_INDEX")
public class AdminIndexEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "exch")
	private String exch;

	@Column(name = "exchange_segment")
	private String exchangeSegment;

	@Column(name = "token")
	private String token;

	@Column(name = "alter_token")
	private String alterToken;

}
