package in.codifi.holdings.service.spec;

import java.util.List;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.cache.model.ClinetInfoModel;
import in.codifi.holdings.model.request.EdisHoldModel;
import in.codifi.holdings.model.request.EdisSummaryRequest;
import in.codifi.holdings.model.response.GenericResponse;

public interface EdisServiceSpec {

	RestResponse<GenericResponse> getRedirectUrl(List<EdisHoldModel> model, String userId);

	RestResponse<GenericResponse> getEdisSummary(EdisSummaryRequest req, ClinetInfoModel info);

}