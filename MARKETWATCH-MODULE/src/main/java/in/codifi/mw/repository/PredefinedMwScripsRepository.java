package in.codifi.mw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.mw.entity.primary.PredefinedMwScripsEntity;

public interface PredefinedMwScripsRepository extends JpaRepository<PredefinedMwScripsEntity, Long> {

	@Modifying
	@Query(value = "DELETE FROM TBL_PRE_DEFINED_MARKET_WATCH_SCRIPS WHERE mwId = :mwId and token = :token and exch = :exch ")
	long deleteScripFomDataBase(@Param("mwId") int mwId, @Param("mwName") String mwName, @Param("token") String token,
			@Param("exch") String exch);

	/**
	 * Method to delete the scrip
	 * 
	 * @author Gowthaman M
	 * @return
	 */
	long deleteByMwIdAndTokenAndExchange(int mwId, String token, String exchange);

}
