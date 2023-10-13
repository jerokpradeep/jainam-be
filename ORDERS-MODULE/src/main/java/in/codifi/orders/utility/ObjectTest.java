package in.codifi.orders.utility;

import org.apache.commons.lang3.ObjectUtils;

import in.codifi.orders.model.request.BrokerageCalculationReq;
import in.codifi.orders.model.request.reqDetailModel;

public class ObjectTest {
	
	public static void main(String[] args) {
		
		BrokerageCalculationReq req = new BrokerageCalculationReq();
		
		if(ObjectUtils.anyNull(req)) {
			System.out.println("null");
		}else{
		 System.out.print("fgsafkja");
		}
		
	}
	
	
	
	

}
