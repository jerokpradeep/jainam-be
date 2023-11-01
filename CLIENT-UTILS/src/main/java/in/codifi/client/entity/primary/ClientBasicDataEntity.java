package in.codifi.client.entity.primary;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "TBL_CLIENT_BASIC_DATA")
public class ClientBasicDataEntity extends CommonEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private long id;
	@Column(name = "OOWNCODE")
	private String ownCode;
	@Column(name = "CTERMCODE")
	private String termCode;
	@Column(name = "BRCODE")
	private String brCode;
	@Column(name = "DEALERCODE")
	private String dealerCode;
	@Column(name = "CRELATIONSHIPCODE")
	private String relationshipCode;
	@Column(name = "CTEAMLEADER")
	private String teamLeader;
	@Column(name = "ADDRESS1")
	private String address1;
	@Column(name = "ADDRESS2")
	private String address2;
	@Column(name = "ADDRESS3")
	private String address3;
	@Column(name = "CITY")
	private String city;
	@Column(name = "STATE")
	private String state;
	@Column(name = "PINCODE")
	private String pincode;
	@Column(name = "COUNTRY")
	private String country;
	@Column(name = "TEL1")
	private String tel1;
	@Column(name = "TEL2")
	private String tel2;
	@Column(name = "TEL3")
	private String tel3;
	@Column(name = "FAX")
	private String fax;
	@Column(name = "MOBILE")
	private String mobile;
	@Column(name = "PANGIR")
	private String pangir;
	@Column(name = "CCORRADDRESS1")
	private String ccorraddress1;
	@Column(name = "CCORRADDRESS2")
	private String ccorraddress2;
	@Column(name = "CCORRADDRESS3")
	private String ccorraddress3;
	@Column(name = "CCORRCITY")
	private String ccorrcity;
	@Column(name = "CCORRSTATE")
	private String ccorrstate;
	@Column(name = "CCORRPIN")
	private String corrPin;
	@Column(name = "CCORRCOUNTRY")
	private String corrCountry;
	@Column(name = "COCCUPATION")
	private String occupation;
	@Column(name = "CGENDER")
	private String gender;
	@Column(name = "CMARITALSTATUS")
	private String maritalStatus;
	@Column(name = "ddateofbirth")
	private String dob;
	@Column(name = "CUNIQUEIDENTIFICATION")
	private String uniqueIdentification;
	@Column(name = "cgstno")
	private String gstno;
	@Column(name = "CSTATUS")
	private String status;
	@Column(name = "CAUTHORIZATIONTYPE")
	private String authorizationType;
	@Column(name = "email")
	private String email;
	@Column(name = "cemailcc")
	private String emailcc;
	@Column(name = "ACTIVE")
	private String active;
	@Column(name = "CUCCCLIENTCATEGORY")
	private String uccClientCategory;
	@Column(name = "cnameasperpan")
	private String nameAsperPan;
	@Column(name = "CFATHERSPOUSEFLAG")
	private String fatherSpouseFlag;
	@Column(name = "ACCOUNTOPENDT")
	private String accountOpenDT;
	@Column(name = "PEP")
	private String pep;
	@Column(name = "NINCOME")
	private String nincome;
	@Column(name = "NETWORTH")
	private String networth;
	@Column(name = "SEBIMTF")
	private String SebiMtf;
	@Column(name = "NOM1")
	private String nom1;
	@Column(name = "NOM2")
	private String nom2;
	@Column(name = "NOM3")
	private String nom3;
	@Column(name = "INTROCODE")
	private String introCode;
	@Column(name = "PREFIX")
	private String Prefix;
	@Column(name = "FIRSTNAME")
	private String firstName;
	@Column(name = "MIDDLENAME")
	private String middleName;
	@Column(name = "LASTNAME")
	private String lastName;

}
