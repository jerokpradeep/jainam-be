package in.codifi.api.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.sql.DataSource;

import in.codifi.api.model.LatestPreDefinedMWReq;
import io.quarkus.logging.Log;

@ApplicationScoped
public class MWEntityManger {

	@Inject
	DataSource dataSource;

	/**
	 * Method to insert position file
	 *
	 * @author Gowthaman M
	 * @param list
	 * @return
	 */
	public boolean insertLatestPreDefinedMW(List<LatestPreDefinedMWReq> list) {

		PreparedStatement pStmt = null;
		Connection conn = null;
		int count = 1;
		try {
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement(
					"INSERT INTO tbl_pre_defined_market_watch_scrips_latest(mw_id, token, exch, symbol, trading_symbol, formatted_ins_name,"
							+ " exch_seg, pdc, lot_size, tick_size, sorting_order) VALUES (?,?,?,?,?,?,?,?,?,?,?)");
			for (LatestPreDefinedMWReq values : list) {
				int paramPos = 1;
				pStmt.setInt(paramPos++, values.getMwId());
				pStmt.setString(paramPos++, values.getToken());
				pStmt.setString(paramPos++, values.getEx());
				pStmt.setString(paramPos++, values.getSymbol());
				pStmt.setString(paramPos++, values.getScripName());
				pStmt.setString(paramPos++, values.getScripName());
				pStmt.setString(paramPos++, values.getExSeg());
				pStmt.setString(paramPos++, values.getPdc());
				pStmt.setString(paramPos++, values.getLotSize());
				pStmt.setString(paramPos++, values.getTickSize());
				pStmt.setInt(paramPos++, values.getSortingOrder());
				count++;
				pStmt.addBatch();
				if (count == 10000) {
					count = 1;
				}
			}
			if (count > 1) {
				pStmt.executeBatch();
			}
			Log.info("Latest PreDefined MW inserted");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		} finally {
			try {
				pStmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return false;
	}

	/**
	 * Method to delete PreDefined MW
	 *
	 * @author Gowthaman M
	 * @return
	 */
	public boolean deletePreDefinedMW() {

		PreparedStatement pStmt = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement("TRUNCATE TABLE tbl_pre_defined_market_watch_scrips_archive");
			pStmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		} finally {
			try {
				pStmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return false;
	}

	/**
	 * Method to move position file
	 *
	 * @author SOWMIYA
	 *
	 * @return
	 */
	public boolean moveAvgPrice() {

		PreparedStatement pStmt = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement(
					"RENAME TABLE tbl_pre_defined_market_watch_scrips_archive TO tbl_pre_defined_market_watch_scrips_temp,"
							+ "tbl_pre_defined_market_watch_scrips TO tbl_pre_defined_market_watch_scrips_archive,"
							+ "tbl_pre_defined_market_watch_scrips_latest TO tbl_pre_defined_market_watch_scrips,"
							+ "tbl_pre_defined_market_watch_scrips_temp TO tbl_pre_defined_market_watch_scrips_latest");
			pStmt.executeUpdate();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		} finally {
			try {
				pStmt.close();
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log.error(e.getMessage());
			}
		}
		return false;
	}

}
