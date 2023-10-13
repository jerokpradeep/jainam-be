package in.codifi.funds.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.funds.entity.primary.PayoutDetailsEntity;

public interface PayoutDetailsRepository extends JpaRepository<PayoutDetailsEntity, Long> {

	@Modifying
	@Query(value = " UPDATE TBL_PAYOUT_DETAILS SET activeStatus = 0 WHERE activeStatus = 1 and userId = :userId and bankActNo = :bankActNo and ifscCode = :ifscCode")
	int canclePayout(@Param("userId") String userId, @Param("bankActNo") String bankActNo, @Param("ifscCode") String ifscCode);

	@Modifying
	@Query(value = " UPDATE TBL_PAYOUT_DETAILS SET amount = :amount WHERE activeStatus = 1 and userId = :userId and bankActNo = :bankActNo and ifscCode = :ifscCode")
	int updatePayout(@Param("amount") String amount, @Param("userId") String userId, @Param("bankActNo") String bankActNo, @Param("ifscCode") String ifscCode);

	@Transactional
	@Query(value = " select a from TBL_PAYOUT_DETAILS a WHERE user_id = :userId ORDER BY ID DESC ")
	List<PayoutDetailsEntity> getpayOutTransactions(@Param("userId") String userId);

}
