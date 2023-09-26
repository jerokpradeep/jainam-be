package in.codifi.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.common.entity.primary.NetValueEntity;

public interface NetValueRepository extends JpaRepository<NetValueEntity, Long> {

}
