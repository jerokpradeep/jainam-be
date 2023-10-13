package in.codifi.orders.reposirory;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.codifi.orders.entity.primary.ProductMasterEntity;

public interface ProductMasterRepository extends JpaRepository<ProductMasterEntity, Long> {

	@Query("SELECT DISTINCT p.tag FROM TBL_PRODUCT_MASTER_MAPPING p")
	List<String> findDistinctByTag();

	List<ProductMasterEntity> findAllByTag(String tag);

//	/**
//	 * Method to get the UI and ODI Value using group by
//	 **/
//	@Query("SELECT DISTINCT p.tag FROM TBL_PRODUCT_MASTER_MAPPING p order by tag")
//	List<String> findDistinctByTag();

}
