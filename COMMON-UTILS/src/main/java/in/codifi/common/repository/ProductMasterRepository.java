package in.codifi.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.codifi.common.entity.primary.ProductMasterEntity;

public interface ProductMasterRepository extends JpaRepository<ProductMasterEntity, Long> {

	@Query("SELECT DISTINCT p.tag FROM TBL_PRODUCT_MASTER_MAPPING p")
	List<String> findDistinctByTag();

	List<ProductMasterEntity> findAllByTag(String tag);

}
