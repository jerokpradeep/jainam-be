package in.codifi.funds.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.funds.entity.primary.BankDetailsEntity;

public interface BankDetailsRepository extends JpaRepository<BankDetailsEntity, Long> {

//	BankDetailsEntity getDetail(String ifscCode);

//	BankDetailsEntity fingByIfscCode(String ifscCode);

}
