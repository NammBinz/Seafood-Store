package springboot.seafoodstore.repository;

import springboot.seafoodstore.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findByFullNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrPositionContainingIgnoreCase(
            String fullName,
            String phone,
            String position     // Chức vụ
    );
}