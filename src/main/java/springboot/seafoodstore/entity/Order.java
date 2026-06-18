package springboot.seafoodstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne  // Nhiều oder - 1 customer
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime createdAt;

    // Tự tạo time now trước khi thêm vào database
    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (status == null)
            status = "PENDING";
    }

    private BigDecimal totalAmount;

    @Column(columnDefinition = "NVARCHAR(50)")
    private String status;
}