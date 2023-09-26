package in.codifi.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.api.entity.primary.PredefinedMwEntity;

public interface PredefinedMwRepo extends JpaRepository<PredefinedMwEntity, Long> {

	PredefinedMwEntity findAllByMwNameAndMwId(String mwName, int mwId);

	List<PredefinedMwEntity> findByMwName(@Param("mwName") String mwName);

}
