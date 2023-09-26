package in.codifi.funds.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.funds.entity.primary.ClientBankDetailsEntity;

public interface ClientBankDetailRepository extends JpaRepository<ClientBankDetailsEntity, Long> {

	List<ClientBankDetailsEntity> findByClientId(String userId);

//	ClientBankDetailsEntity findByBankAcno(String bankActNo);

	ClientBankDetailsEntity findByBankAcnoAndClientId(String bankActNo, String userId);

}
