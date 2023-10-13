package in.codifi.mw.repository;

import java.util.List;

import javax.enterprise.context.control.ActivateRequestContext;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import in.codifi.mw.entity.primary.TickerTapeEntity;

public interface TickerTapeRepository extends JpaRepository<TickerTapeEntity, Long> {

	@ActivateRequestContext
	List<TickerTapeEntity> findByUserId(@Param("user_id") String user_id);

}
