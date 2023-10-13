package in.codifi.feedback.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import in.codifi.feedback.entity.logs.AccessLogModel;
import in.codifi.feedback.entity.logs.RestAccessLogModel;

@ApplicationScoped
public class AccessLogManager {
	@Named("logs")
	@Inject
	DataSource dataSource;

	public void insertAccessLog(AccessLogModel accLogModel) {

		Date inTimeDate;
		if (accLogModel.getInTime() != null) {
			inTimeDate = new Date(accLogModel.getInTime().getTime());
		} else {
			inTimeDate = new Date();
		}

		String date = new SimpleDateFormat("ddMMYYYY").format(inTimeDate);
		String hour = new SimpleDateFormat("HH").format(inTimeDate);
		String tableName = "tbl_" + date + "_access_log_" + hour;
		try {

			Connection connection = dataSource.getConnection();
			Statement state = connection.createStatement();

			String insertQuery = "INSERT INTO " + tableName
					+ "(user_id, ucc, req_id, source, vendor, in_time, out_time, lag_time,  module, method, req_body,"
					+ " res_body, device_ip, user_agent, domain, content_type, session, uri) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			int parampose = 1;
			PreparedStatement statement = connection.prepareStatement(insertQuery);
			statement.setString(parampose++, accLogModel.getUserId());
			statement.setString(parampose++, accLogModel.getUcc());
			statement.setString(parampose++, accLogModel.getReqId());
			statement.setString(parampose++, accLogModel.getSource());
			statement.setString(parampose++, accLogModel.getVendor());
			statement.setTimestamp(parampose++, accLogModel.getInTime());
			statement.setTimestamp(parampose++, accLogModel.getOutTime());
			statement.setLong(parampose++, accLogModel.getLagTime());
			statement.setString(parampose++, accLogModel.getModule());
			statement.setString(parampose++, accLogModel.getMethod());
			statement.setString(parampose++, accLogModel.getReqBody());
			statement.setString(parampose++, accLogModel.getResBody());
			statement.setString(parampose++, accLogModel.getDeviceIp());
			statement.setString(parampose++, accLogModel.getUserAgent());
			statement.setString(parampose++, accLogModel.getDomain());
			statement.setString(parampose++, accLogModel.getContentType());
			statement.setString(parampose++, accLogModel.getSession());
			statement.setString(parampose++, accLogModel.getUri());
			statement.executeUpdate();

			statement.close();
			state.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Method to insert rest access log
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param accLogModel
	 */
	public void insertRestAccessLog(RestAccessLogModel accLogModel) {

		Date inTimeDate = new Date();

		String date = new SimpleDateFormat("ddMMYYYY").format(inTimeDate);
		String tableName = "tbl_" + date + "_rest_access_log";

		try {

			Connection connection = dataSource.getConnection();
			Statement state = connection.createStatement();

			String insertQuery = "INSERT INTO " + tableName + "(user_id, url, in_time, out_time, total_time, module,"
					+ " method, req_body, res_body) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			int parampose = 1;
			PreparedStatement statement = connection.prepareStatement(insertQuery);
			statement.setString(parampose++, accLogModel.getUserId());
			statement.setString(parampose++, accLogModel.getUrl());
			statement.setTimestamp(parampose++, accLogModel.getInTime());
			statement.setTimestamp(parampose++, accLogModel.getOutTime());
			statement.setString(parampose++, accLogModel.getTotalTime());
			statement.setString(parampose++, accLogModel.getModule());
			statement.setString(parampose++, accLogModel.getMethod());
			statement.setString(parampose++, accLogModel.getReqBody());
			statement.setString(parampose++, accLogModel.getResBody());
//			statement.setTimestamp(parampose++, accLogModel.getCreatedOn());
//			statement.setTimestamp(parampose++, accLogModel.getUpdatedOn());
			statement.executeUpdate();

			statement.close();
			state.close();
			connection.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
