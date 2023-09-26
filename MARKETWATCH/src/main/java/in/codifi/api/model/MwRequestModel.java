package in.codifi.api.model;

import java.util.List;

public class MwRequestModel {
	
	private Boolean predefined;
	private String userId;
	private int mwId;
	private String mwName;
	private List<MwScripModel> scripData;
	
	public Boolean isPredefined() {
		return predefined;
	}

	public void setPredefined(Boolean predefined) {
		this.predefined = predefined;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getMwId() {
		return mwId;
	}

	public void setMwId(int mwId) {
		this.mwId = mwId;
	}

	public String getMwName() {
		return mwName;
	}

	public void setMwName(String mwName) {
		this.mwName = mwName;
	}

	public List<MwScripModel> getScripData() {
		return scripData;
	}

	public void setScripData(List<MwScripModel> scripData) {
		this.scripData = scripData;
	}

}
