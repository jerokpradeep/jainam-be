package in.codifi.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.api.entity.primary.AccessLogEntity;

public interface AccessLogRepo extends JpaRepository<AccessLogEntity, Long> {

}
