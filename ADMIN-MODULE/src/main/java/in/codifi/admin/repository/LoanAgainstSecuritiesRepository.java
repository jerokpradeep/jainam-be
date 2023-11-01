package in.codifi.admin.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.admin.entity.LoanAgainstSecuritiesEntity;

public interface LoanAgainstSecuritiesRepository extends JpaRepository<LoanAgainstSecuritiesEntity, Long> {

	@Transactional
	@Query(value = "SELECT a FROM TBL_LOAN_AGAINST_SECURITIES a WHERE a.createdOn BETWEEN :fromDate1 and :toDate1 ")
	List<LoanAgainstSecuritiesEntity> getReturns(@Param("fromDate1") Date fromDate1, @Param("toDate1") Date toDate1);

}
