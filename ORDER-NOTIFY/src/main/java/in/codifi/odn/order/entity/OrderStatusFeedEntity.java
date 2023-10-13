package in.codifi.odn.order.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity(name = "tbl_order_status_feed")
public class OrderStatusFeedEntity {
	
	@javax.persistence.Id @GeneratedValue(strategy=GenerationType.IDENTITY)
	
	@Column(name = "id")
	private Long Id;
		
	@Column(name = "uid")
	private String uid;

	@Column(name = "norenordno")
	private String norenordno;
	
	@Column(name = "actid")
	private String actid;
	
    @Column(name = "qty")
	private String qty;

    @Column(name = "prc")
	private String prc;

    @Column(name = "pcode")
	private String pcode;

    @Column(name = "remarks")
	private String remarks;
    
    
    @Column(name = "prctyp")
	private String prctyp;

    @Column(name = "ret")
	private String ret;
	
    @Column(name = "dscqty")
	private String dscqty;
	
    @Column(name = "trgprc")
	private String trgprc;
	
    @Column(name = "trantype")
	private String trantype;
	
    @Column(name = "exch")
	private String exch;
	
    @Column(name = "tsym")
	private String tsym;
	
    @Column(name = "status")
	private String status;
	
    @Column(name = "reporttype")
	private String reporttype;

    @Column(name = "rejreason")
	private String rejreason;

    @Column(name = "exchordid")
	private String exchordid;

    @Column(name = "exch_tm")
	private String exchtm;

    @Column(name = "amo")
	private String amo;

    @Column(name = "cancelqty")
	private String cancelqty;

    @Column(name = "blprc")
	private String blprc;

    @Column(name = "bpprc")
	private String bpprc;

    @Column(name = "trailprc")
	private String trailprc;
	
}
