package in.codifi.auth.repository;

import in.codifi.auth.entity.primary.QrLoginEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QrLoginRepository extends JpaRepository<QrLoginEntity, Long> {

}
