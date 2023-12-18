package in.codifi.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.common.entity.primary.EtfEntity;

public interface EtfRepository extends JpaRepository<EtfEntity, Long> {

}
