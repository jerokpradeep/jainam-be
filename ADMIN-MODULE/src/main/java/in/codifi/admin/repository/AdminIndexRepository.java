package in.codifi.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.admin.entity.AdminIndexEntity;

public interface AdminIndexRepository extends JpaRepository<AdminIndexEntity, Long> {

	/**
	 * method to update Preference value
	 * 
	 * @author LOKESH
	 */
	@Modifying
	@Query(value = "UPDATE TBL_INDEX set alterToken = :alterToken  where exch = :exch and  exchangeSegment = :exchangeSegment and token = :token")
	int updateIndex(@Param("alterToken") String alterToken, @Param("exch") String exch,
			@Param("exchangeSegment") String exchangeSegment, @Param("token") String token);

//	/**
//	 * method to truncate index value
//	 * 
//	 * @author LOKESH
//	 */
//	@Transactional
//	@Modifying
//	@Query(value = "TRUNCATE TABLE TBL_INDEX", nativeQuery = true)
//	void truncateIndexvalue();

}
