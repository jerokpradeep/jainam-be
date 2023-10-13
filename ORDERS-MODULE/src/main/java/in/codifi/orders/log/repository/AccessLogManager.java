//package in.codifi.orders.log.repository;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.Statement;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
//import javax.enterprise.context.ApplicationScoped;
//import javax.inject.Inject;
//import javax.inject.Named;
//import javax.sql.DataSource;
//
//import in.codifi.orders.entity.logs.AccessLogModel;
//import in.codifi.orders.entity.logs.ErrorLogModel;
//import in.codifi.orders.entity.logs.TpLogModel;
//
//@ApplicationScoped
//public class AccessLogManager {
//
//	@Named("logs")
//	@Inject
//	DataSource dataSource;
//
//	public void insertAccessLog(AccessLogModel accLogModel) {
//
//		String sdf = new SimpleDateFormat("ddMMYYYY").format(new Date());
//		int hours = accLogModel.getInTime().getHours();
////		String tableName = "tbl" + "_" + sdf + "_" + "access" + "_" + "log" + "_" + hours;
//		String tableName = "tbl_access_log";
//		try {
//
//			Connection connection = dataSource.getConnection();
//			Statement state = connection.createStatement();
//
//			String insertQuery = "INSERT INTO " + tableName
//					+ "(uri , user_id, ucc, req_id, source, vendor, in_time, out_time, lag_time,  module, method, req_body, res_body, device_ip, user_agent, domain, content_type, session, created_on, updated_on) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?)";
//
//			PreparedStatement statement = connection.prepareStatement(insertQuery);
//			int param = 1;
//			statement.setString(param++, accLogModel.getUri());
//			statement.setString(param++, accLogModel.getUserId());
//			statement.setString(param++, accLogModel.getUcc());
//			statement.setString(param++, accLogModel.getReqId());
//			statement.setString(param++, accLogModel.getSource());
//			statement.setString(param++, accLogModel.getVendor());
//			statement.setTimestamp(param++, accLogModel.getInTime());
//			statement.setTimestamp(param++, accLogModel.getOutTime());
//			statement.setLong(param++, accLogModel.getLagTime());
////			Date elapsed_time = new Date(accLogModel.getElapsed_time().getTime());
////			statement.setDate(9, elapsed_time);
//			statement.setString(param++, accLogModel.getModule());
//			statement.setString(param++, accLogModel.getMethod());
//			statement.setString(param++, accLogModel.getReqBody());
//			statement.setString(param++, accLogModel.getResBody());
//			statement.setString(param++, accLogModel.getDeviceIp());
//			statement.setString(param++, accLogModel.getUserAgent());
//			statement.setString(param++, accLogModel.getDomain());
//			statement.setString(param++, accLogModel.getContentType());
//			statement.setString(param++, accLogModel.getSession());
//			statement.setTimestamp(param++, accLogModel.getCreatedOn());
//			statement.setTimestamp(param++, accLogModel.getUpdatedOn());
//			statement.executeUpdate();
//
//			statement.close();
//			state.close();
//			connection.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public void insertTpLog(TpLogModel tpLogModel) {
//
//		String sdf = new SimpleDateFormat("ddMMYYYY").format(new Date());
//		int hours = tpLogModel.getInTime().getHours();
////		String tableName = "tbl" + "_" + sdf + "_" + "access" + "_" + "log" + "_" + hours;
//		String tableName = "tbl_tp_api_log";
//		try {
//
//			Connection connection = dataSource.getConnection();
//			Statement state = connection.createStatement();
//
//			String insertQuery = "INSERT INTO " + tableName
//					+ "( req_id, tp_uri, in_time, out_time,  method, req_body, res_body, content_type) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
//
//			PreparedStatement statement = connection.prepareStatement(insertQuery);
//			int param = 1;
//			statement.setString(param++, tpLogModel.getReqId());
//			statement.setString(param++, tpLogModel.getTpUri());
//			statement.setTimestamp(param++, tpLogModel.getInTime());
//			statement.setTimestamp(param++, tpLogModel.getOutTime());
//			statement.setString(param++, tpLogModel.getMethod());
//			statement.setString(param++, tpLogModel.getReqBody());
//			statement.setString(param++, tpLogModel.getResBody());
//			statement.setString(param++, tpLogModel.getContentType());
//			statement.executeUpdate();
//
//			statement.close();
//			state.close();
//			connection.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	public void insertErrorLog(ErrorLogModel errorlogModel) {
//
////		String tableName = "tbl" + "_" + sdf + "_" + "access" + "_" + "log" + "_" + hours;
//		String tableName = "tbl_error_log";
//		try {
//
//			Connection connection = dataSource.getConnection();
//			Statement state = connection.createStatement();
//
//			String insertQuery = "INSERT INTO " + tableName + "( req_id, class, method, error) VALUES ( ?, ?, ?, ?)";
//
//			PreparedStatement statement = connection.prepareStatement(insertQuery);
//			int param = 1;
//			statement.setString(param++, errorlogModel.getReqId());
//			statement.setString(param++, errorlogModel.getClassName());
//			statement.setString(param++, errorlogModel.getMethod());
//			statement.setString(param++, errorlogModel.getError());
//			statement.executeUpdate();
//
//			statement.close();
//			state.close();
//			connection.close();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//}
