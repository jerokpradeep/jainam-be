package in.codifi.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.auth.entity.primary.VendorEntity;

public interface VendorRepository extends JpaRepository<VendorEntity, Long> {

	List<VendorEntity> findAllByApiKey(String vendorKey);

	List<VendorEntity> findAllByApiKeyAndAuthorizationStatusAndActiveStatus(String vendorKey, int authStatus,
			int activeStatus);

}
