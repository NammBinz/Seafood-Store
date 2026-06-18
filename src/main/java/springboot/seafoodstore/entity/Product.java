package springboot.seafoodstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Column(nullable = false, columnDefinition = "NVARCHAR(100)")
    private String name;

    @NotBlank(message = "Loại sản phẩm không được để trống")
    @Column(columnDefinition = "NVARCHAR(100)")
    private String type;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String origin;

    @Column(columnDefinition = "NVARCHAR(20)")
    private String unit;    // Đơn vị

    @NotNull(message = "Giá không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    private Integer quantity;   // Số lượng

    @Column(columnDefinition = "NVARCHAR(255)")
    private String description;     // Mô tả

    @Column(columnDefinition = "NVARCHAR(255)")
    private String imageUrl;

    private Boolean status = true;
}