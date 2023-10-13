package in.codifi.odn.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class OrderStatusModel implements Serializable {
		

	@JsonProperty("norenordno")
	public String orderId;

	@JsonProperty("actid")
	private String actid;
	
	@JsonProperty("exch")
	private String exch;
	
	@JsonProperty("tsym")
	private String tsym;
	
	@JsonProperty("qty")
	private String qty;
	
	@JsonProperty("prc")
	private String prc;
	
	@JsonProperty("prd")
	private String prd;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("reporttype")
	private String reporttype;
	
	@JsonProperty("trantype")
	private String trantype;
	
	@JsonProperty("prctyp")
	private String prctyp;

	
	@JsonProperty("ret")
	private String ret;

	
	@JsonProperty("fillshares")
	private String fillshares;
	
	@JsonProperty("avgprc")
	private String avgprc;
	
	@JsonProperty("fltm")
	private String fltm;
	
	@JsonProperty("flid")
	private String flid;
	
	@JsonProperty("flqty")
	private String flqty;
	
	@JsonProperty("flprc")
	private String flprc;
	
	@JsonProperty("rejreason")
	private String rejreason;
	
	@JsonProperty("exchordid")
	private String exchordid;
	
	@JsonProperty("cancelqty")
	private String cancelqty;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("dscqty")
	private String dscqty;
	
	@JsonProperty("trgprc")
	private String trgprc;
	
	@JsonProperty("snonum")
	private String snonum;
	
	@JsonProperty("snoordt")
	private String snoordt;
	
	@JsonProperty("blprc")
	private String blprc;
	
	@JsonProperty("bpprc")
	private String bpprc;
	
	@JsonProperty("trailprc")
	private String trailprc;
	
	@JsonProperty("exch_tm")
	private String exchtm;

	
	@JsonProperty("amo")
	private String amo;
	
	@JsonProperty("tm")
	private String tm;
	
	@JsonProperty("ntm")
	private String ntm;
	
	@JsonProperty("kidid")
	private String kidid;
	
	@JsonProperty("sno_fillid")
	private String snofillid;
	
	@JsonProperty("pcode")
	private String pcode;
	

}
