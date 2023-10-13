package in.codifi.common.entity.primary;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "tbl_nfo_fut_expiry_data")
@Getter
@Setter
public class NFOFutureEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "ID")
	private long id;

	@Column(name = "UNDERLYING")
	private String underlying;

	@Column(name = "SPOT_TOKEN")
	private int spotToken;

	@Column(name = "TOKEN")
	private int token;

	@Column(name = "FORMATTED_INS_NAME")
	private String formattedInsName;

	@Column(name = "PDC")
	private double pdc;

	@Column(name = "VOLUME")
	private double volume;

	@Column(name = "OI")
	private double oi;

	@Column(name = "EXPIRY_DATE")
	private String expiryDate;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "CREATED_ON", insertable = false, updatable = false)
	@Temporal(TemporalType.TIMESTAMP)
	@CreationTimestamp
	private Date createdOn;

}
