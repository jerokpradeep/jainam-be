package in.codifi.orders.service.spec;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.orders.model.request.OrderReqModel;
import in.codifi.orders.model.response.GenericResponse;

public interface OrdersServiceSpec {

	RestResponse<GenericResponse> placeOrder(OrderReqModel orderReqModel, ClinetInfoModel info);

	RestResponse<GenericResponse> modifyOrder(OrderReqModel orderReqModel, ClinetInfoModel info);

	RestResponse<GenericResponse> cancelOrder(OrderReqModel orderReqModel, ClinetInfoModel info);

}
