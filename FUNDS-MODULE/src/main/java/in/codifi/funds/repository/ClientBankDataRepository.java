package in.codifi.funds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.funds.entity.primary.ClientBankDataEntity;

public interface ClientBankDataRepository extends JpaRepository<ClientBankDataEntity, Long> {

//	List<ClientBankDataEntity> findByIfscCode(String ifscCode);

	List<ClientBankDataEntity> findByIfscCodeAndTermCode(String ifscCode, String userId);

	List<ClientBankDataEntity> findByTermCode(String userId);

	ClientBankDataEntity findByBankAccountNumberAndTermCode(String bankAccountNumber, String userId);

}
