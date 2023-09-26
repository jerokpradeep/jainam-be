package in.codifi.orders.utility;

import java.sql.Timestamp;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import in.codifi.orders.entity.logs.ErrorLogModel;
import in.codifi.orders.entity.logs.TpLogModel;
import in.codifi.orders.reposirory.AccessLogManager;
//import in.codifi.orders.log.repository.AccessLogManager;

@ApplicationScoped
public class AccessLogHelper {

	@Inject
	AccessLogManager logRepo;

	TpLogModel tplogModel;

	ErrorLogModel errorlogModel;

	/**
	 * Method to prepare and save tp api log
	 */
	public void saveTpLogs(String reqId, String tpUri, Timestamp inTime, String method, String reqBody, String resBody,
			String contentType) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {

			@Override
			public void run() {
				tplogModel = new TpLogModel();
				tplogModel.setReqId(reqId);
				tplogModel.setTpUri(StringUtil.isNotNullOrEmpty(tpUri) ? tpUri : "");
				tplogModel.setInTime(StringUtil.isNotNullOrEmpty(inTime.toString()) ? inTime : null);
				tplogModel.setOutTime(new Timestamp(System.currentTimeMillis()));
				tplogModel.setMethod(StringUtil.isNotNullOrEmpty(method) ? method : "");
				tplogModel.setReqBody(StringUtil.isNotNullOrEmpty(reqBody) ? reqBody : "");
				tplogModel.setResBody(StringUtil.isNotNullOrEmpty(resBody) ? resBody : "");
				tplogModel.setContentType(StringUtil.isNotNullOrEmpty(contentType) ? contentType : "");
				logRepo.insertTpLog(tplogModel);

			}

		});

	}

	/**
	 * Method to save all the error
	 */
	public void saveErrorLog(String reqId, String Class, String method, String error) {

		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {

			@Override
			public void run() {
				errorlogModel = new ErrorLogModel();
				errorlogModel.setReqId(reqId);
				errorlogModel.setClassName(StringUtil.isNotNullOrEmpty(Class) ? Class : null);
				errorlogModel.setMethod(StringUtil.isNotNullOrEmpty(method) ? method : null);
				errorlogModel.setError(StringUtil.isNotNullOrEmpty(error) ? error : "");
				logRepo.insertErrorLog(errorlogModel);
			}
		});

	}

}
