package in.codifi.common.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

//@Entity(name = "TBL_PRODUCT_MASTER_MAPPING")
@Getter
@Setter
public class ProductMasterEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "TAG")
	private String tag;

	@Column(name = "UI_VALUE")
	private String uiValue;

	@Column(name = "ODIN_VALUE")
	private String odinValue;

}
