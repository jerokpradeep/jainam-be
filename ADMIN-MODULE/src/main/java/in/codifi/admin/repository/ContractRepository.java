package in.codifi.admin.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.codifi.admin.entity.ContractEntity;

public interface ContractRepository extends JpaRepository<ContractEntity, Long> {

	@Transactional
	@Query(value = "SELECT a FROM TBL_GLOBAL_CONTRACT_MASTER_DETAILS a where a.segment like '%idx%' and a.alterToken IS NOT NULL AND TRIM(alterToken) <> ' '")
	List<ContractEntity> findByExchangeSegment();

}
