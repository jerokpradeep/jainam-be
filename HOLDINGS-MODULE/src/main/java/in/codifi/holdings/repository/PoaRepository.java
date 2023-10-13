package in.codifi.holdings.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.holdings.entity.primary.PoaEntity;

public interface PoaRepository extends JpaRepository<PoaEntity, Long> {

}
