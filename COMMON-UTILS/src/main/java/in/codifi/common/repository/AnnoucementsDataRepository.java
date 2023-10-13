package in.codifi.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.common.entity.primary.AnnoucementsDataEntity;

public interface AnnoucementsDataRepository extends JpaRepository<AnnoucementsDataEntity, Long> {

}
