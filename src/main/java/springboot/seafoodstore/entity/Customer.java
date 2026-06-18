package springboot.seafoodstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Họ tên khách hàng không được để trống")
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "0\\d{9}", message = "Số điện thoại phải có 10 số và bắt đầu bằng 0")
    @Column(length = 20)
    private String phone;

    @Email(message = "Email không đúng định dạng")
    @Column(length = 100)
    private String email;

    @Column(columnDefinition = "NVARCHAR(255)")
    private String address;

    private Boolean status = true;

    // Mỗi khách hàng có thể liên kết với một tài khoản đăng nhập
    @OneToOne
    @JoinColumn(name = "user_id")   // Tạo khóa ngoại
    private User user;  // Trỏ tới cột id trong bảng users
}