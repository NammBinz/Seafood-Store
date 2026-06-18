package springboot.seafoodstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Họ tên không được để trống")
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String fullName;

    @Column(columnDefinition = "NVARCHAR(10)")
    private String gender;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private LocalDate birthday;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0\\d{9}", message = "Số điện thoại phải có 10 số và bắt đầu bằng 0")
    @Column(length = 20)
    private String phone;

    @Email(message = "Email không đúng định dạng")
    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String address;

    @NotBlank(message = "Chức vụ không được để trống")
    @Column(columnDefinition = "NVARCHAR(50)")
    private String position;

    @NotNull(message = "Lương không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Lương phải lớn hơn 0")
    private BigDecimal salary;  // salary tối thiểu > 0, ko đc = 0

    private Boolean status = true;
}