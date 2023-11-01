package in.codifi.admin.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.admin.entity.AdminPreferenceEntity;

public interface AdminPreferenceRepository extends JpaRepository<AdminPreferenceEntity, Long> {

	/**
	 * method to update Preference value
	 * 
	 * @author LOKESH
	 */
	@Modifying
	@Query(value = "UPDATE TBL_ADMIN_PREFERENCE set adminValue = :value  where adminKey = 'mtf' ")
	int updatePreference(@Param("value") int value);

	@Modifying
	@Query(value = "UPDATE TBL_ADMIN_PREFERENCE set alterToken = :alterToken  where adminKey = 'mtf' ")
	int updateIndex(@Param("alterToken")String alterToken);

//	@Query(value = "SELECT a FROM TBL_ADMIN_PREFERENCE a WHERE a.adminValue = :value")
//	List<PreferenceRequestModel> findByValue(@Param("value") int value);

//	@Modifying
//	@Query(value = "UPDATE TBL_VENDOR_APP set authorization_status = :authorization_status  where api_key = :api_key")
//	int updateAuthorization(@Param("authorization_status") int authorization_status, @Param("api_key") String api_key);

}
