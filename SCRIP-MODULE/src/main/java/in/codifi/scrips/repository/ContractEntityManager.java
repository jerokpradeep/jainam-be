package in.codifi.scrips.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.jboss.resteasy.reactive.RestResponse;

import in.codifi.scrips.model.response.GenericResponse;
import in.codifi.scrips.utility.AppConstants;
import in.codifi.scrips.utility.PrepareResponse;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ContractEntityManager {

	@Inject
	EntityManager entityManager;

	@Inject
	PrepareResponse prepareResponse;

	@Inject
	DataSource dataSource;

	/**
	 * Method to delete expired contract in DB
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param currentDate
	 * @return
	 */
	@Transactional
	public RestResponse<GenericResponse> deleteExpiredContract(String currentDate) {
		try {

			Query query = entityManager
					.createNativeQuery("DELETE FROM tbl_global_contract_master_details a where a.expiry_date < :date");
			query.setParameter("date", currentDate);
			int deleteCount = query.executeUpdate();
			Log.info("Expired Contract ->" + deleteCount + "-" + AppConstants.RECORD_DELETED);
			return prepareResponse.prepareSuccessMessage(deleteCount + "-" + AppConstants.RECORD_DELETED);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.DELETE_FAILED);
	}

	/**
	 * 
	 * Method to Delete BSE contract
	 * 
	 * @author Dinesh Kumar
	 *
	 * @return
	 */
	@Transactional
	public RestResponse<GenericResponse> deleteBSEContract() {
		try {

//			Query query = entityManager.createNativeQuery(
//					"DELETE FROM tbl_global_contract_master_details a where exch in('BSE','BFO','BCD')");
			Query query = entityManager.createNativeQuery(
					"DELETE FROM tbl_global_contract_master_details a where exch not in('BSE','NSE','NFO','CDS')");
			int deleteCount = query.executeUpdate();
			Log.info("BSE Contract ->" + deleteCount + "-" + AppConstants.RECORD_DELETED);
			return prepareResponse.prepareSuccessMessage(deleteCount + "-" + AppConstants.RECORD_DELETED);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return prepareResponse.prepareFailedResponse(AppConstants.DELETE_FAILED);
	}

	/**
	 * Method to Truncate Archive Table For Contract Master
	 * 
	 * @author LOKESH
	 * @return
	 */
	public boolean deleteGlobalContractMaster() {
		PreparedStatement pStmt = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement("TRUNCATE TABLE tbl_global_contract_master_details");
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
	 * Method to Rename Table For Contract Master
	 * 
	 * @author LOKESH
	 * @return
	 */
	public boolean moveGlobalContractMaster() {
		PreparedStatement pStmt = null;
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
			pStmt = conn.prepareStatement(
					"RENAME TABLE tbl_global_contract_master_details_archive TO tbl_global_contract_master_details_temp,"
							+ "tbl_global_contract_master_details TO tbl_global_contract_master_details_archive,"
							+ "tbl_global_contract_master_details_latest TO tbl_global_contract_master_details,"
							+ "tbl_global_contract_master_details_temp TO tbl_global_contract_master_details_latest");
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
