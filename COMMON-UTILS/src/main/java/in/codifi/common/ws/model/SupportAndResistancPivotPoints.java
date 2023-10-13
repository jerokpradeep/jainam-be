package in.codifi.common.ws.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupportAndResistancPivotPoints implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private double support3;
	private double resistance3;
	private double pivotPoints;
	private double resistance2;
	private double resistance1;
	private double support2;
	private double support1;

}
