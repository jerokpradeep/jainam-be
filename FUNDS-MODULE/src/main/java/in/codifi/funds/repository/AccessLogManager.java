package in.codifi.funds.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import in.codifi.funds.entity.logs.AccessLogModel;
import in.codifi.funds.entity.logs.RestAccessLogModel;
import io.quarkus.logging.Log;

@ApplicationScoped
public class AccessLogManager {
	@Named("logs")
	@Inject
	DataSource dataSource;

	/**
	 * Method to insert rest access log
	 * 
	 * @author Gowthaman M
	 */
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
		Connection connection = null;
		Statement state = null;
		PreparedStatement statement = null;
		try {

			connection = dataSource.getConnection();
			state = connection.createStatement();

			String insertQuery = "INSERT INTO " + tableName
					+ "(user_id, ucc, req_id, source, vendor, in_time, out_time, lag_time,  module, method, req_body,"
					+ " res_body, device_ip, user_agent, domain, content_type, session, uri) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			statement = connection.prepareStatement(insertQuery);
			int paramPos = 1;
			statement.setString(paramPos++, accLogModel.getUserId());
			statement.setString(paramPos++, accLogModel.getUcc());
			statement.setString(paramPos++, accLogModel.getReqId());
			statement.setString(paramPos++, accLogModel.getSource());
			statement.setString(paramPos++, accLogModel.getVendor());
			statement.setTimestamp(paramPos++, accLogModel.getInTime());
			statement.setTimestamp(paramPos++, accLogModel.getOutTime());
			statement.setLong(paramPos++, accLogModel.getLagTime());
			statement.setString(paramPos++, accLogModel.getModule());
			statement.setString(paramPos++, accLogModel.getMethod());
			statement.setString(paramPos++, accLogModel.getReqBody());
			statement.setString(paramPos++, accLogModel.getResBody());
			statement.setString(paramPos++, accLogModel.getDeviceIp());
			statement.setString(paramPos++, accLogModel.getUserAgent());
			statement.setString(paramPos++, accLogModel.getDomain());
			statement.setString(paramPos++, accLogModel.getContentType());
			statement.setString(paramPos++, accLogModel.getSession());
			statement.setString(paramPos++, accLogModel.getUri());
			statement.executeUpdate();
			statement.close();
			state.close();
			connection.close();

		} catch (Exception e) {
			Log.error("insertAccessLog -" + e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (state != null) {
					state.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("insertAccessLog -" + e);
			}
		}
	}

	/**
	 * Method to insert rest access log
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	public void insertRestAccessLog(RestAccessLogModel accLogModel) {
		Date inTimeDate = new Date();
		String date = new SimpleDateFormat("ddMMYYYY").format(inTimeDate);
		String tableName = "tbl_" + date + "_rest_access_log";
		Connection connection = null;
		Statement state = null;
		PreparedStatement statement = null;
		try {
			connection = dataSource.getConnection();
			state = connection.createStatement();

			String insertQuery = "INSERT INTO " + tableName + "(user_id, url, in_time, out_time, total_time, module,"
					+ " method, req_body, res_body) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			statement = connection.prepareStatement(insertQuery);
			int paramPos = 1;
			statement.setString(paramPos++, accLogModel.getUserId());
			statement.setString(paramPos++, accLogModel.getUrl());
			statement.setTimestamp(paramPos++, accLogModel.getInTime());
			statement.setTimestamp(paramPos++, accLogModel.getOutTime());
			statement.setString(paramPos++, accLogModel.getTotalTime());
			statement.setString(paramPos++, accLogModel.getModule());
			statement.setString(paramPos++, accLogModel.getMethod());
			statement.setString(paramPos++, accLogModel.getReqBody());
			statement.setString(paramPos++, accLogModel.getResBody());
//			statement.setTimestamp(paramPos++, accLogModel.getCreatedOn());
//			statement.setTimestamp(paramPos++, accLogModel.getUpdatedOn());
			statement.executeUpdate();

			statement.close();
			state.close();
			connection.close();

		} catch (Exception e) {
			Log.error("insertRestAccessLog -" + e);
		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (state != null) {
					state.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("insertRestAccessLog -" + e);
			}
		}
	}
}
