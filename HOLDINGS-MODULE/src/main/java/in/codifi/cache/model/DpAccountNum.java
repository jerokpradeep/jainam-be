package in.codifi.cache.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DpAccountNum implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String dpAccountNumber;
	private String dpId;//TODO remove first two char and put
	private String boId;
	private String repository;
	private String edisClientCode;
	private String dpAccountName;

}
