package in.codifi.mw.entity.primary;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_MARKET_WATCH_SCRIPS")
public class MarketWatchScripEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "USER_ID")
	private String userId;

//	@Column(name = "MW_ID", columnDefinition = "integer default 0")
	@Column(name = "MW_ID")
	private int mwId;

	@Column(name = "FORMATTED_INS_NAME")
	private String formattedName;

	@Column(name = "EXCH")
	private String exch;

	@Column(name = "EXCH_SEG")
	private String exSeg;

	@Column(name = "TOKEN")
	private String token;

	@Column(name = "SYMBOL")
	private String symbol;

	@Column(name = "TRADING_SYMBOL")
	private String tradingSymbol;

	@Column(name = "GROUP_NAME")
	private String groupName;

	@Column(name = "INSTRUMENT_TYPE")
	private String instrumentType;

	@Column(name = "OPTION_TYPE")
	private String optionType;

	@Column(name = "STRIKE_PRICE")
	private String strikePrice;

	@Column(name = "PDC")
	private String pdc;

	@Column(name = "EXPIRY_DATE")
	private Date expDt;

	@Column(name = "LOT_SIZE")
	private String lotSize;

	@Column(name = "TICK_SIZE")
	private String tickSize;

	@Column(name = "SORTING_ORDER")
	private int sortingOrder;

	@Column(name = "ALTER_TOKEN")
	private String alterToken;

	@Column(name = "WEEK_TAG")
	private String weekTag;

}
