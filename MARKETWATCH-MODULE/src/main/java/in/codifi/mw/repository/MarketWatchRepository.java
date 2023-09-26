package in.codifi.mw.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.mw.entity.primary.MarketWatchEntity;

public interface MarketWatchRepository extends JpaRepository<MarketWatchEntity, Long> {

	List<MarketWatchEntity> findAllByUserId(String userId);

	MarketWatchEntity findAllByUserIdAndMwId(String userId, int mwId);

	@Modifying
	@Query(value = "UPDATE TBL_MARKET_WATCH SET mwName = :mwName WHERE mwId = :mwId AND userId = :userId")
	int updateMWName(@Param("mwName") String mwName, @Param("mwId") int mwId, @Param("userId") String userId);

}
