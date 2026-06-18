package springboot.seafoodstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne  // Nhiều orderDetail - 1 order
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne  // Nhiều orderDetail - 1 product
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(columnDefinition = "NVARCHAR(100)")
    private String productName;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;
}