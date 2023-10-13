package in.codifi.odn.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.codifi.odn.order.entity.OrderStatusFeedEntity;

@Repository
public interface OrderNotificationRepository extends JpaRepository<OrderStatusFeedEntity, Long>{

}
