package springboot.seafoodstore.repository;

import springboot.seafoodstore.entity.Order;
import springboot.seafoodstore.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrder(Order order);
}