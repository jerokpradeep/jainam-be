package in.codifi.admin.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.admin.entity.LoanSmallMediumEnterprisesEntity;

public interface LoanSmallMediumEnterprisesRepository extends JpaRepository<LoanSmallMediumEnterprisesEntity, Long> {

	@Transactional
	@Query(value = "SELECT a FROM TBL_LOAN_SMALL_MEDIUM_ENTERPRISES a WHERE a.createdOn BETWEEN :fromDate1 and :toDate1 ")
	List<LoanSmallMediumEnterprisesEntity> getReturns(@Param("fromDate1") Date fromDate1,
			@Param("toDate1") Date toDate1);

}