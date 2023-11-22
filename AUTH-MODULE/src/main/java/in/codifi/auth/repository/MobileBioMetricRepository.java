package in.codifi.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.auth.entity.primary.MobileBioMetricEntity;

public interface MobileBioMetricRepository extends JpaRepository<MobileBioMetricEntity, Long> {

	/**
	 * 
	 * Method to find all by userId and deviceType
	 * 
	 * @author Dinesh Kumar
	 *
	 * @param userId
	 * @param deviceType
	 * @return
	 */
	List<MobileBioMetricEntity> findAllByUserIdAndActiveStatus(String userId, int activeStatus);

}
