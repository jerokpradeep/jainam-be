package in.codifi.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.common.entity.primary.IndicesEntity;

public interface IndicesRepository extends JpaRepository<IndicesEntity, Long> {

}
