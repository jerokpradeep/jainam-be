package in.codifi.admin.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.sql.DataSource;

import org.json.simple.JSONObject;

import in.codifi.admin.model.request.LoggedInRequestModel;
import in.codifi.admin.model.request.UrlRequestModel;
import in.codifi.admin.model.response.LogDetailsResponseModel;
import in.codifi.admin.model.response.UrlResponsetModel;
import in.codifi.admin.req.model.MobUserReqModel;
import in.codifi.cache.CacheController;
import io.quarkus.logging.Log;

@SuppressWarnings("unchecked")
@ApplicationScoped
public class AdminLogsDAO {

	@Named("logs")
	@Inject
	DataSource datasource;

	/**
	 * Method to get the total logged in details for past days from data base
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<LogDetailsResponseModel> getUserLogDetails() {
		List<LogDetailsResponseModel> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		LogDetailsResponseModel tempDto = null;
		try {
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("SELECT date, web, mob, api,unique_web, unique_mob, "
					+ "unique_api FROM tbl_daily_log_reports order by date desc limit 30");
			rSet = pStmt.executeQuery();

			if (rSet != null) {
				response = new ArrayList<LogDetailsResponseModel>();
				while (rSet.next()) {
					tempDto = new LogDetailsResponseModel();
					tempDto.setDate(rSet.getString("date"));
					tempDto.setTotalWeb(rSet.getString("web"));
					tempDto.setTotalMobile(rSet.getString("mob"));
					tempDto.setTotalApi(rSet.getString("api"));
					tempDto.setUniqueWeb(rSet.getString("unique_web"));
					tempDto.setUniqueMob(rSet.getString("unique_mob"));
					tempDto.setUniqueApi(rSet.getString("unique_api"));
//					tempDto.setUniqueCounts(rSet.getString("total_sum"));
					response.add(tempDto);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				Log.error("getCountBySource -" + e);
			}
		}
		return response;
	}

	public void getUserRec() {
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		try {
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			String outputDate = formatter.format(currentDate);

			for (int i = 1; i < 23; i++) {
				String Formatted = String.format("%02d", i);
				conn = datasource.getConnection();
				pStmt = conn.prepareStatement(
						"SELECT distinct(user_id) as users ,  device_ip, count(*) as counts FROM tbl_" + outputDate
								+ "_access_log_" + Formatted + "group by user_id, device_ip order by counts desc ");
				rSet = pStmt.executeQuery();
				if (rSet != null) {
					while (rSet.next()) {
						CacheController.getUserRec().put(rSet.getString("users") + "_" + rSet.getString("device_ip"),
								rSet.getString("counts"));
					}
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
	}

	/**
	 * method to get the url based records
	 * 
	 * @author LOKESH
	 * @return
	 */

	public List<JSONObject> getUrlBasedRecords() {
		List<JSONObject> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		JSONObject tempJson = null;
		try {
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			String outputDate = formatter.format(currentDate);
			LocalTime time = LocalTime.now();
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH");
			String outputDate1 = formatter1.format(time);
//			String Formatted = String.format("%d", outputDate1);
			int intValue = Integer.parseInt(outputDate1);
			int Hr = 1;
			int hour = ((intValue) - (Hr));
			String Formatted = String.format("%02d", hour);
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("SELECT distinct(uri) as url , count(*) as counts FROM tbl_" + outputDate
					+ "_access_log_" + Formatted + " group by url order by counts desc limit 15");
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				response = new ArrayList<JSONObject>();
				while (rSet.next()) {
					tempJson = new JSONObject();
					tempJson.put("url", rSet.getString("url"));
					tempJson.put("count", rSet.getString("counts"));
					response.add(tempJson);
				}
			}
//			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return response;
	}

	/**
	 * Method to get last 12 hour login count
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<JSONObject> getLast12hourLoginCount() {
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		List<JSONObject> jsonList = new ArrayList<JSONObject>();
		try {
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("SELECT id,access_time,login_count,active_count,active_web_count, "
					+ " active_mob_count,active_api_count FROM tbl_login_count "
					+ " where access_time > DATE_SUB(NOW(), INTERVAL 12 HOUR)");
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				while (rSet.next()) {
					JSONObject result = new JSONObject();
					result.put("id", rSet.getString("id"));
					result.put("access_time", rSet.getString("access_time"));
					result.put("login_count", rSet.getString("login_count"));
					result.put("active_count", rSet.getString("active_count"));
					result.put("active_web_count", rSet.getString("active_web_count"));
					result.put("active_mob_count", rSet.getString("active_mob_count"));
					result.put("active_api_count", rSet.getString("active_api_count"));
					jsonList.add(result);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return jsonList;
	}

	/**
	 * Method to get distinct url for drop down
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<String> getDistinctUrl() {
		List<String> response = new ArrayList<String>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		try {
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			String outputDate = formatter.format(currentDate);

			for (int i = 1; i < 23; i++) {
				String Formatted = String.format("%02d", i);
				conn = datasource.getConnection();
				pStmt = conn.prepareStatement(
						"select distinct uri as uri from tbl_" + outputDate + "_access_log_" + Formatted);
				rSet = pStmt.executeQuery();
				if (rSet != null) {
					while (rSet.next()) {
						response.add(rSet.getString("uri"));
					}
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return response;
	}

	/**
	 * method to get the url based records1
	 * 
	 * @author LOKESH
	 * @return
	 */

	public List<UrlResponsetModel> getUrlBasedRecords1() {
		List<UrlResponsetModel> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		UrlResponsetModel user1 = null;
		try {
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			String outputDate = formatter.format(currentDate);
			LocalTime time = LocalTime.now();
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH");
			String outputDate1 = formatter1.format(time);
//			String Formatted = String.format("%d", outputDate1);
			int intValue = Integer.parseInt(outputDate1);
			int Hr = 1;
			int hour = ((intValue) - (Hr));
			String Formatted = String.format("%02d", hour);
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("SELECT distinct(uri) as url , count(*) as counts FROM tbl_" + outputDate
					+ "_access_log_" + Formatted + " group by url");
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				response = new ArrayList<UrlResponsetModel>();
				while (rSet.next()) {
					user1 = new UrlResponsetModel();
					user1.setCounts(rSet.getString("counts"));
					user1.setUrl(rSet.getString("url"));
					response.add(user1);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return response;
	}

	/**
	 * method to Insert the url based records
	 * 
	 * @author LOKESH
	 * @return
	 */
	public boolean insertLogDetails(List<UrlResponsetModel> result) {
		Connection conn = null;
		boolean isSuccessful = false;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
//		java.sql.Timestamp timestamp = new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis());
		try {
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
			String outputDate = formatter.format(currentDate);
			LocalTime time = LocalTime.now();
			DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH");
			String outputDate1 = formatter1.format(time);
//			String Formatted = String.format("%d", outputDate1);
			int intValue = Integer.parseInt(outputDate1);
			int Hr = 1;
			int hour = ((intValue) - (Hr));
			String Formatted = String.format("%02d", hour);
			conn = datasource.getConnection();

			for (UrlResponsetModel loggedModel : result) {
				pStmt = conn.prepareStatement("INSERT INTO tbl_url_records(date, time, url, counts) values(?,?,?,?)");
				int paramPos = 1;
				pStmt.setString(paramPos++, outputDate);
				pStmt.setString(paramPos++, Formatted);
				pStmt.setString(paramPos++, loggedModel.getUrl());
				pStmt.setString(paramPos++, loggedModel.getCounts());
				pStmt.addBatch();
				isSuccessful = pStmt.execute();
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isSuccessful;
	}

	/**
	 * method to get the url record
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<UrlResponsetModel> getUrlRecord(UrlRequestModel model) {
		List<UrlResponsetModel> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		UrlResponsetModel user1 = null;
		String from = model.getDate();
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		LocalDate fromDate = LocalDate.parse(from, inputFormatter);
		String outputDate = formatter.format(fromDate);
		try {
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("SELECT * FROM tbl_url_records  WHERE date = ? and time = ? and url = ?");
			int paramPos = 1;
			pStmt.setString(paramPos++, outputDate);
			pStmt.setString(paramPos++, model.getTime());
			pStmt.setString(paramPos++, model.getUrl());
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				response = new ArrayList<UrlResponsetModel>();
				while (rSet.next()) {
					user1 = new UrlResponsetModel();
					user1.setCounts(rSet.getString("counts"));
					user1.setUrl(rSet.getString("url"));
					response.add(user1);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return response;
	}

	public LoggedInRequestModel getSourceRecord() {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		LoggedInRequestModel respModel = new LoggedInRequestModel();
		String source = " ('WEB','MOB','API') ";
		try {
			connection = datasource.getConnection();

			String selectQuery = " select source,count(user_id)as userCount from tbl_user_loggedin_report where source in "
					+ source + " GROUP BY source";
			statement = connection.prepareStatement(selectQuery);
//				statement.setString(1, source);
			resultSet = statement.executeQuery();
			if (resultSet != null) {
				while (resultSet.next()) {
					String sourceValue = resultSet.getString("source");
					String userCount = resultSet.getString("userCount");
					if (sourceValue.equalsIgnoreCase("WEB")) {
						respModel.setWeb(userCount);
					}
					if (sourceValue.equalsIgnoreCase("MOB")) {
						respModel.setMob(userCount);
					}
					if (sourceValue.equalsIgnoreCase("API")) {
						respModel.setApi(userCount);
					}
				}
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("getCountBySource", e);

		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("getCountBySource -" + e);
			}
		}
		return respModel;
	}

	public LoggedInRequestModel getSourceRecord1() {

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		LoggedInRequestModel respModel = new LoggedInRequestModel();
		String source = " ('WEB','MOB','API') ";
		try {
			connection = datasource.getConnection();

			String selectQuery = " select distinct(source),count(distinct user_id)as userCount from tbl_user_loggedin_report where source in "
					+ source + " GROUP BY source";
			statement = connection.prepareStatement(selectQuery);
//				statement.setString(1, source);
			resultSet = statement.executeQuery();
			if (resultSet != null) {
				while (resultSet.next()) {
					String sourceValue = resultSet.getString("source");
					String userCount = resultSet.getString("userCount");
					if (sourceValue.equalsIgnoreCase("WEB")) {
						respModel.setWeb(userCount);
					}
					if (sourceValue.equalsIgnoreCase("MOB")) {
						respModel.setMob(userCount);
					}
					if (sourceValue.equalsIgnoreCase("API")) {
						respModel.setApi(userCount);
					}
				}
			}
			resultSet.close();
			statement.close();
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("getCountBySource", e);

		} finally {
			try {
				if (statement != null) {
					statement.close();
				}
				if (connection != null) {
					connection.close();
				}
			} catch (Exception e) {
				Log.error("getCountBySource -" + e);
			}
		}
		return respModel;
	}

	public boolean InsertLoggedInRecord1(LoggedInRequestModel result, LoggedInRequestModel result1) {
		Connection conn = null;
		boolean isSuccessful = false;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		try {
			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String outputDate = formatter.format(currentDate);
			conn = datasource.getConnection();

			pStmt = conn.prepareStatement(
					"INSERT INTO tbl_daily_log_reports(date, web, mob, api, unique_web, unique_mob, unique_api) values(?,?,?,?,?,?,?)");
			int paramPos = 1;
			pStmt.setString(paramPos++, outputDate);
			pStmt.setString(paramPos++, result.getWeb());
			pStmt.setString(paramPos++, result.getMob());
			pStmt.setString(paramPos++, result.getApi());
			pStmt.setString(paramPos++, result1.getWeb());
			pStmt.setString(paramPos++, result1.getMob());
			pStmt.setString(paramPos++, result1.getApi());
			pStmt.addBatch();
			isSuccessful = pStmt.execute();

			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (rSet != null) {
					rSet.close();
				}
				if (pStmt != null) {
					pStmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return isSuccessful;
	}

	/**
	 * method to get user record mob
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<MobUserReqModel> getMobUser() {
		List<MobUserReqModel> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		MobUserReqModel MobVer = null;
		try {
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("select  user_id ,source FROM tbl_user_loggedin_report where source = 'mob'");
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				response = new ArrayList<MobUserReqModel>();
				while (rSet.next()) {
					MobVer = new MobUserReqModel();
					MobVer.setUserId(rSet.getString("user_id"));
					MobVer.setSource(rSet.getString("source"));
					response.add(MobVer);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * method to get user record web
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<MobUserReqModel> getWebUser() {
		List<MobUserReqModel> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		MobUserReqModel MobVer = null;
		try {
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("select  user_id ,source FROM tbl_user_loggedin_report where source = 'web'");
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				response = new ArrayList<MobUserReqModel>();
				while (rSet.next()) {
					MobVer = new MobUserReqModel();
					MobVer.setUserId(rSet.getString("user_id"));
					MobVer.setSource(rSet.getString("source"));
					response.add(MobVer);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * method to get user record API
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<MobUserReqModel> getApiUser() {
		List<MobUserReqModel> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		MobUserReqModel MobVer = null;
		try {
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("select  user_id ,source FROM tbl_user_loggedin_report where source = 'api'");
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				response = new ArrayList<MobUserReqModel>();
				while (rSet.next()) {
					MobVer = new MobUserReqModel();
					MobVer.setUserId(rSet.getString("user_id"));
					MobVer.setSource(rSet.getString("source"));
					response.add(MobVer);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}

	/**
	 * method to get Unique UserId
	 * 
	 * @author LOKESH
	 * @return
	 */
	public List<MobUserReqModel> getUniqueUserId() {
		List<MobUserReqModel> response = null;
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rSet = null;
		MobUserReqModel MobVer = null;
		try {
			conn = datasource.getConnection();
			pStmt = conn.prepareStatement("select  distinct(user_id) as user_id FROM tbl_user_loggedin_report");
			rSet = pStmt.executeQuery();
			if (rSet != null) {
				response = new ArrayList<MobUserReqModel>();
				while (rSet.next()) {
					MobVer = new MobUserReqModel();
					MobVer.setUserId(rSet.getString("user_id"));
					response.add(MobVer);
				}
			}
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return response;
	}
}
