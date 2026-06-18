package springboot.seafoodstore.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // id tự tăng
    private Long id;

    @ManyToOne  // Nhiều CartItem - 1 Customer
    @JoinColumn(name = "customer_id")   // Khóa ngoại trỏ tới cột id trong bảng customers
    private Customer customer;

    @ManyToOne  // Nhiều CartItem - 1 Product
    @JoinColumn(name = "product_id")    // Khóa ngoại trỏ tới cột id trong bảng products
    private Product product;

    private Integer quantity;

    public BigDecimal getSubtotal() {
        if (product == null || product.getPrice() == null || quantity == null)
            return BigDecimal.ZERO;

        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}