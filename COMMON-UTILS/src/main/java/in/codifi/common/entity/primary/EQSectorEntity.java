package in.codifi.common.entity.primary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import lombok.Getter;
import lombok.Setter;

@Entity(name = "TBL_EQSECTOR")
@Getter
@Setter
public class EQSectorEntity extends CommonEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "SECTOR_ID")
	private int sectorList;

	@Column(name = "SECTOR_NAME")
	private String sectorName;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "SECTOR_ID", referencedColumnName = "SECTOR_ID")
	@OrderBy("id")
	private List<EQSectorDetailsEntity> scrips = new ArrayList<>();

	@Column(name = "IMAGE_URL")
	private String imageUrl;

	@Column(name = "THREE_MONTHS")
	private String threeMonths;

	@Column(name = "SIX_MONTHS")
	private String sixMonths;

	@Column(name = "ONE_YEAR")
	private String oneYear;

}
