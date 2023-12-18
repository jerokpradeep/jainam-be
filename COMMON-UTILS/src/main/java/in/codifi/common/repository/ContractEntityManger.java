package in.codifi.common.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import in.codifi.common.config.HazelcastConfig;
import in.codifi.common.entity.primary.EQSectorDetailsEntity;
import in.codifi.common.entity.primary.EtfDetailsEntity;
import in.codifi.common.entity.primary.FutureDetailsEntity;
import in.codifi.common.entity.primary.IndicesEntity;
import io.quarkus.hibernate.orm.PersistenceUnit;
import io.quarkus.logging.Log;

@ApplicationScoped
public class ContractEntityManger {

//	@Named("chola")
	@Inject
	@PersistenceUnit("jainam")
	EntityManager entityManager;

	/**
	 * Method to get indices data
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param scrips
	 * @param exch
	 * @return
	 */
	public List<IndicesEntity> getIndicesDetails(List<String> scrips, String exch) {
		List<IndicesEntity> respone = new ArrayList<>();
		try {
			Query query = entityManager.createNativeQuery(
					"select token,symbol,trading_symbol,formatted_ins_name,exch,exchange_segment,expiry_date,pdc from tbl_global_contract_master_details"
							+ " where exch = :exch and symbol IN (:symbol)");

			query.setParameter("exch", exch);
			query.setParameter("symbol", scrips);
			@SuppressWarnings("unchecked")
			List<Object[]> result = query.getResultList();

			for (Object[] values : result) {
				IndicesEntity indicesEntity = new IndicesEntity();
				indicesEntity.setToken((String) values[0]);
				indicesEntity.setSymbol((String) values[1]);
				indicesEntity.setTradingSymbol((String) values[2]);
				indicesEntity.setFormattedInsName((String) values[3]);
				indicesEntity.setExchange((String) values[4]);
				indicesEntity.setSegment((String) values[5]);
				if (values[6] != null) {
					indicesEntity.setExpiry((Date) values[6]);
				}
				indicesEntity.setPdc((String) values[7]);
				respone.add(indicesEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return respone;
	}

	/**
	 * 
	 * Method to load NSE data
	 * 
	 * @author Dinesh Kumar
	 *
	 */
	@SuppressWarnings("unchecked")
	public void loadNSEData() {
		try {
			Query query = entityManager.createNativeQuery(
					"select distinct(symbol) as symbol , token from tbl_global_contract_master_details"
							+ " where exch = :exch ");

			query.setParameter("exch", "NSE");

			List<Object[]> result = query.getResultList();

			for (Object[] values : result) {
				String symbol = values[0].toString();
				String token = values[1].toString();
				HazelcastConfig.getInstance().getNseTokenCache().put(symbol, token);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
	}

	/**
	 * method to get EQ sector details from global contract master table
	 * 
	 * @author SOWMIYA
	 * @param scrips
	 * @return
	 */

	public List<EQSectorDetailsEntity> getEQSectorDetails(List<String> scrips) {
		List<EQSectorDetailsEntity> eqSectorDetails = new ArrayList<>();
		try {
			List<String> exch = new ArrayList<>();
			exch.add("NSE");
			exch.add("BSE");
			Query query = entityManager.createNativeQuery(
					"select formatted_ins_name,exch,exchange_segment,token,trading_symbol from tbl_global_contract_master_details"
							+ " where exch IN (:exch) and symbol IN (:symbol)");

			query.setParameter("exch", exch);
			query.setParameter("symbol", scrips);

			@SuppressWarnings("unchecked")
			List<Object[]> result = query.getResultList();
			for (Object[] values : result) {
				EQSectorDetailsEntity eqsector = new EQSectorDetailsEntity();
				eqsector.setScripName((String) values[0]);
				eqsector.setExchange((String) values[1]);
				eqsector.setSegment((String) values[2]);
				eqsector.setToken((String) values[3]);
				eqsector.setSymbol((String) values[4]);
				eqSectorDetails.add(eqsector);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return eqSectorDetails;
	}

	/**
	 * Method to get alter token
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getToken(String reqToken, String reqSegment) {
		String token = "";
		try {
			Query query = entityManager.createNativeQuery("select alter_token from tbl_global_contract_master_details"
					+ " where exch = :exch and token = :token ");

			query.setParameter("exch", reqSegment);
			query.setParameter("token", reqToken);
			List<String> result = query.getResultList();
			for (String values : result) {
				token = values;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return token;
	}

	/**
	 * Method to get token NFO
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getTokenForNFO(String exch, String symbol, String groupName) {
		String token = "";
		try {
			Query query = entityManager.createNativeQuery("select token from tbl_global_contract_master_details"
					+ " where exch = :exch and symbol = :symbol and group_name= :groupName ");

			query.setParameter("exch", exch);
			query.setParameter("symbol", symbol);
			query.setParameter("groupName", groupName);
			List<String> result = query.getResultList();
			for (String values : result) {
				token = values;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return token;
	}

	/**
	 * Method to get token NFO
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getTokenForBFO(String exch, String symbol, String groupName) {
		String token = "";
		try {
			Query query = entityManager.createNativeQuery("select token from tbl_global_contract_master_details"
					+ " where exch = :exch and symbol = :symbol ");

			query.setParameter("exch", exch);
			query.setParameter("symbol", symbol);
			List<String> result = query.getResultList();
			for (String values : result) {
				token = values;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return token;
	}

	/**
	 * Method to get ETF data
	 * 
	 * @author DINESH KUMAR
	 *
	 * @param scrips
	 * @param exch
	 * @return
	 */
	public List<EtfDetailsEntity> getEtfDetails(List<String> scrips) {
		List<EtfDetailsEntity> respone = new ArrayList<>();
		try {
			List<String> exch = new ArrayList<>();
			exch.add("NSE");
			exch.add("BSE");
			Query query = entityManager.createNativeQuery(
					"select token,symbol,trading_symbol,formatted_ins_name,exch,exchange_segment from tbl_global_contract_master_details"
							+ " where exch IN (:exch) and symbol IN (:symbol)");

			query.setParameter("exch", exch);
			query.setParameter("symbol", scrips);
			List<Object[]> result = query.getResultList();

			for (Object[] values : result) {
				EtfDetailsEntity detailsEntity = new EtfDetailsEntity();
				detailsEntity.setToken((String) values[0]);
				detailsEntity.setSymbol((String) values[1]);
				detailsEntity.setTradingSymbol((String) values[2]);
				detailsEntity.setFormattedInsName((String) values[3]);
				detailsEntity.setExchange((String) values[4]);
				detailsEntity.setSegment((String) values[5]);
				respone.add(detailsEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return respone;
	}

	/**
	 * method to get future details from global contract master
	 * 
	 * @author SOWMIYA
	 * @param exch
	 * @param symbol
	 * @param insType
	 * @return
	 */
	public List<FutureDetailsEntity> getFutureDetails(String exch, String symbol, String insType) {
		List<FutureDetailsEntity> futureDetails = new ArrayList<>();
		try {
			Query query = entityManager.createNativeQuery(
					"select formatted_ins_name,exch,expiry_date,token,pdc,symbol from tbl_global_contract_master_details"
							+ " where exch = :exch and" + " symbol = :symbol and instrument_type = :instType");

			query.setParameter("exch", exch);
			query.setParameter("symbol", symbol);
			query.setParameter("instType", insType);

			@SuppressWarnings("unchecked")
			List<Object[]> result = query.getResultList();
			for (Object[] values : result) {
				FutureDetailsEntity entity = new FutureDetailsEntity();
				entity.setScripName((String) values[0]);
				entity.setExchange((String) values[1]);
				entity.setExpiry((Date) values[2]);
				entity.setToken((String) values[3]);
				entity.setPdc((String) values[4]);
				entity.setSymbol((String) values[5]);
				futureDetails.add(entity);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.error(e.getMessage());
		}
		return futureDetails;
	}

}
