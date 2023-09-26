package in.codifi.mw.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.mw.entity.primary.PredefinedMwEntity;

public interface PredefinedMwRepository extends JpaRepository<PredefinedMwEntity, Long> {

	PredefinedMwEntity findAllByMwNameAndMwId(String mwName, int mwId);

}
