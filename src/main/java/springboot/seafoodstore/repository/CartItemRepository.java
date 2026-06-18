package springboot.seafoodstore.repository;

import springboot.seafoodstore.entity.CartItem;
import springboot.seafoodstore.entity.Customer;
import springboot.seafoodstore.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // Tìm list giỏ hàng
    List<CartItem> findByCustomer(Customer customer);

    // Tìm xem customer này đã có product trong giỏ chưa
    Optional<CartItem> findByCustomerAndProduct(Customer customer, Product product);

    // Xóa giỏ hàng
    void deleteByCustomer(Customer customer);
}