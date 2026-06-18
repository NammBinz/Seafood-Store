package springboot.seafoodstore.repository;

import springboot.seafoodstore.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    List<Customer> findByFullNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(
            String fullName,
            String phone
    );

    // Tìm Customer theo Customer.user.username
    Optional<Customer> findByUserUsername(String username);
}