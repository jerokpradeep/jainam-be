package in.codifi.client.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.client.entity.primary.PinStartBarEntity;

public interface PinStartBarRepository extends JpaRepository<PinStartBarEntity, Long> {

	/**
	 * Method to get pin to start bar details by userId
	 *
	 * @author GOWTHAMAN M
	 * @return
	 */
	List<PinStartBarEntity> findByUserId(@Param("user_id") String userId);

	List<PinStartBarEntity> findByUserIdAndSortOrder(@Param("user_id") String userId,
			@Param("sort_order") int soertOrder);

	/**
	 * method to get distinct user id
	 * 
	 * @author GOWTHAMAN M
	 * @return
	 */
	@Transactional
	@Query(value = " select distinct userId from TBL_PIN_START_BAR")
	List<String> getDistinctUserId();

}
