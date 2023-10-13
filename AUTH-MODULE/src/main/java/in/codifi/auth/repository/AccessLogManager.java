package in.codifi.auth.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import in.codifi.auth.config.HazelcastConfig;
import in.codifi.auth.entity.logs.AccessLogModel;
import in.codifi.auth.entity.logs.RestAccessLogModel;
import in.codifi.auth.model.response.UsersLoggedInRespModel;
import in.codifi.auth.utility.StringUtil;
import in.codifi.cache.AccessLogCache;
import io.quarkus.logging.Log;

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
//		String tableName = "tbl_access_log";
//		int hours = accLogModel.getInTime().getHours();
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

	/**
	 * Method to insert user count log
	 * 
	 * @param userId
	 * @param source
	 * @param hazelKeySsoVendor
	 */
	public void insertUserLogginedInDetails(String userId, String source, String hazelKeySsoVendor) {
		UsersLoggedInRespModel loggedModel = new UsersLoggedInRespModel();
		if (source.equalsIgnoreCase("WEB")) {
			loggedModel = HazelcastConfig.getInstance().getWebLoggedInUsers().get(userId);
		}
		if (source.equalsIgnoreCase("MOB")) {
			loggedModel = HazelcastConfig.getInstance().getMobLoggedInUsers().get(userId);
		}
		if (source.equalsIgnoreCase("API")) {
			loggedModel = HazelcastConfig.getInstance().getApiLoggedInUsers().get(userId);
		}
		if (StringUtil.isNotNullOrEmpty(hazelKeySsoVendor) && hazelKeySsoVendor != null) {
			loggedModel = HazelcastConfig.getInstance().getSsoLoggedInUsers().get(hazelKeySsoVendor);
		}
		AccessLogCache.getInstance().getUsersLoggedInModel().add(loggedModel);
		if (AccessLogCache.getInstance().getUsersLoggedInModel().size() >= 1) {
			List<UsersLoggedInRespModel> accessLogModels = new ArrayList<>(
					AccessLogCache.getInstance().getUsersLoggedInModel());
			AccessLogCache.getInstance().getUsersLoggedInModel().clear();
			AccessLogCache.getInstance().setUsersLoggedInModel(new ArrayList<>());
			insertUserLogginedInDetailsIntoDB(accessLogModels);
		}
	}

	/**
	 * Method to insert user count log
	 * 
	 * @param accessLogModels
	 */
	private void insertUserLogginedInDetailsIntoDB(List<UsersLoggedInRespModel> accessLogModels) {
		ExecutorService pool = Executors.newSingleThreadExecutor();
		pool.execute(new Runnable() {
			@Override
			public void run() {
				List<UsersLoggedInRespModel> logRespModel = new ArrayList<>();
				logRespModel = accessLogModels;
				String tableName = "tbl_user_loggedin_report";
				PreparedStatement statement = null;
				Connection connection = null;

				String insertQuery = "INSERT INTO " + tableName
						+ "(user_id, source, visitors, vendor) VALUES ( ?, ?, ?, ?)";
				try {
					connection = dataSource.getConnection();
					if (logRespModel != null && logRespModel.size() > 0) {
						statement = connection.prepareStatement(insertQuery);
						for (UsersLoggedInRespModel loggedModel : logRespModel) {
							int paramPos = 1;
							statement.setString(paramPos++, loggedModel.getUserId());
							statement.setString(paramPos++, loggedModel.getSource());
							statement.setInt(paramPos++, 1);
							statement.setString(paramPos++, loggedModel.getVendor());
							statement.addBatch();
						}
						statement.executeBatch();

					} else {
						System.out.println("0");
					}
					statement.close();
					connection.close();
				} catch (Exception e) {
					Log.error("KB - Auth - insertUserLogginedInDetails -" + e);
				} finally {
					try {
						if (statement != null) {
							statement.close();
						}
						if (connection != null) {
							connection.close();
						}
					} catch (Exception e) {
						Log.error("KB - Auth - insertUserLogginedInDetails -" + e);
					}
				}
			}
		});
	}

}
