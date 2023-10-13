package in.codifi.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.common.entity.primary.EQSectorEntity;

public interface EQSectorRepository extends JpaRepository<EQSectorEntity, Long> {

	EQSectorEntity findAllBySectorList(int i);

}
