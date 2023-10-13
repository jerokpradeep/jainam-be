package in.codifi.mw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.mw.entity.primary.MarketWatchScripEntity;

public interface MarketWatchScripRepository extends JpaRepository<MarketWatchScripEntity, Long> {
	
	@Modifying
	@Query(value = "DELETE FROM TBL_MARKET_WATCH_SCRIPS  WHERE mwId = :mwId and userId = :pUserId and token = :token and exch = :exch ")
	long deleteScripFomDataBase(@Param("pUserId") String pUserId, @Param("exch") String exch,
			@Param("token") String token, @Param("mwId") int mwId);

	long deleteByMwIdAndUserIdAndTokenAndExch(int mwId, String userId, String token, String exch);

}
