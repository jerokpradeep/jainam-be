package in.codifi.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.common.entity.primary.FutureEntity;

public interface FutureRepository extends JpaRepository<FutureEntity, Long> {

}
