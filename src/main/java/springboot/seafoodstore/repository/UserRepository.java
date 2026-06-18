package springboot.seafoodstore.repository;

import springboot.seafoodstore.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // Kiểm tra tài khoản đã tồn tại chưa
    boolean existsByUsername(String username);
}