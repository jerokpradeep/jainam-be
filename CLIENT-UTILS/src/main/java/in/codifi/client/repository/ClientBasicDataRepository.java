package in.codifi.client.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.client.entity.primary.ClientBasicDataEntity;

public interface ClientBasicDataRepository extends JpaRepository<ClientBasicDataEntity, Long> {

//	ClientBasicDataEntity findBytermcode(String userId);
//
//	ClientBasicDataEntity findBytermcode(@Param("termCode") String termCode);
	
	@Transactional
	@Query(value = "select a FROM TBL_CLIENT_BASIC_DATA a where a.termCode = :termCode")
	ClientBasicDataEntity getTermcode(@Param("termCode") String termCode);

}
