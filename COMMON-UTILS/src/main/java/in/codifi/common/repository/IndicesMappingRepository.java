package in.codifi.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.codifi.common.entity.primary.IndicesMappingEntity;

public interface IndicesMappingRepository extends JpaRepository<IndicesMappingEntity, Long> {

	@Query("SELECT DISTINCT p.exchange FROM TBL_INDICES_DATA_MAP p")
	List<String> findDistinctByExchange();

	List<IndicesMappingEntity> findAllByExchange(String exchange);


}
