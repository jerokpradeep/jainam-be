package in.codifi.common.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.common.entity.primary.NFOFutureEntity;

public interface NFOFutureRepository extends JpaRepository<NFOFutureEntity, Long> {

	/**
	 * method to find all by underlying
	 * 
	 * @author sowmiya
	 * @param symbol
	 * @return
	 */
	List<NFOFutureEntity> findByUnderlying(@Param("underlying") String underlying);

	/**
	 * method to get distinct symbol from data base
	 * 
	 * @author sowmiya
	 * @return
	 */
	@Query(value = "SELECT DISTINCT underlying FROM tbl_nfo_fut_expiry_data ")
	List<String> getSymbols();

}
