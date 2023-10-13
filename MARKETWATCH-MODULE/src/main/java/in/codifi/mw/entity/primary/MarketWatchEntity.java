package in.codifi.mw.entity.primary;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_MARKET_WATCH")
public class MarketWatchEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "MW_ID")
	private int mwId;

	@Column(name = "USER_ID")
	private String userId;

	@Column(name = "MW_NAME")
	private String mwName;

	@Column(name = "POSITION")
	private Long position;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "MW_ID", referencedColumnName = "MW_ID")
	@OrderBy("id")
	private List<MarketWatchScripEntity> mwDetailsDTO;

}
