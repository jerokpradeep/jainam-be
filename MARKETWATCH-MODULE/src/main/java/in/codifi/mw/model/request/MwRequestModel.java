package in.codifi.mw.model.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MwRequestModel {
	
	private String userId;
	private int mwId;
	private String mwName;
	private List<MwScripModel> scripData;

}
