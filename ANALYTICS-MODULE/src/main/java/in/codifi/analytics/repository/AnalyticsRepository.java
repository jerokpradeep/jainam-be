package in.codifi.analytics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.codifi.analytics.entity.primary.AnalyticsEntity;

public interface AnalyticsRepository extends JpaRepository<AnalyticsEntity, Long> {

}
