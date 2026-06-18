package springboot.seafoodstore.config;

import springboot.seafoodstore.entity.User;
import springboot.seafoodstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component  // Spring sẽ tự quản lý và tự chạy khi ứng dụng khởi động
// CommandLineRunner cho phép chạy code ngay sau khi Spring Boot khởi động xong
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Nếu chưa có thì mới tạo mới
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRole("ADMIN");
            admin.setStatus(true);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("staff").isEmpty()) {
            User staff = new User();
            staff.setUsername("staff");
            staff.setPassword(passwordEncoder.encode("123456"));
            staff.setRole("STAFF");
            staff.setStatus(true);
            userRepository.save(staff);
        }
    }
}