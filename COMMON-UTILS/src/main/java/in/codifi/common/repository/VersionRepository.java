package in.codifi.common.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.codifi.common.entity.primary.VersionEntity;

public interface VersionRepository extends JpaRepository<VersionEntity, Long> {

	@Transactional
	@Modifying
	@Query(value = " update TBL_APP_VERSION set VERSION = :version  where OS = :os ")
	int updateVersion(@Param("version") String version, @Param("os") String os);

}
