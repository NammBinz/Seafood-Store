package springboot.seafoodstore.repository;

import springboot.seafoodstore.entity.Customer;
import springboot.seafoodstore.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerOrderByCreatedAtDesc(Customer customer);

    List<Order> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);
}