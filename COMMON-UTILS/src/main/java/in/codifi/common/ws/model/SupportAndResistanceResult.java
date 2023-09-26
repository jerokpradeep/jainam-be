package in.codifi.common.ws.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportAndResistanceResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double sma30;
	private double stochasticsK;
	private double sma50;
	private double macd;
	private double rsi;
	private double sma15;
	private double adxIndicator;
	private double standardDeviationIndicator;
	private double stochRSI;
	private double ema50;
	private double atrIndicator;
	private double ema12;
	private SupportAndResistancPivotPoints pivotPoints;
	private double ema26;
	private double stochasticsD;

}
