package in.codifi.auth.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.auth.entity.primary.VendorSubcriptionEntity;

public interface VendorSubcriptionRepository extends JpaRepository<VendorSubcriptionEntity, Long> {

	List<VendorSubcriptionEntity> findAllByUserIdAndAppIdAndActiveStatus(String userId, long appId, int i);

}
