package springboot.seafoodstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.seafoodstore.entity.Product;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameContainingIgnoreCaseOrTypeContainingIgnoreCase(
            String name,
            String type
    );

    List<Product> findByQuantityLessThanEqual(Integer quantity);

    long countByStatus(Boolean status);

    @Query(value = """
            SELECT *
            FROM products p
            WHERE
                (:keyword IS NULL OR :keyword = ''
                    OR p.name COLLATE Vietnamese_CI_AI LIKE '%' + :keyword + '%'
                    OR p.type COLLATE Vietnamese_CI_AI LIKE '%' + :keyword + '%')
    
            AND (:origin IS NULL OR :origin = ''
                    OR p.origin COLLATE Vietnamese_CI_AI LIKE '%' + :origin + '%')
    
            AND (:minPrice IS NULL OR p.price >= :minPrice)
            AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            AND (:status IS NULL OR p.status = :status)
            AND (:lowStock = 0 OR p.quantity <= 10)
    
            ORDER BY p.id DESC
        """, nativeQuery = true)
    List<Product> searchAdvanced(
            @Param("keyword") String keyword,
            @Param("origin") String origin,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            @Param("status") Boolean status,    // Có thể nhận null
            @Param("lowStock") boolean lowStock // chỉ có true / false
    );

    @Query(value = """
        SELECT *
        FROM products p
        WHERE p.status = 1
          AND p.quantity > 0

          AND (:keyword IS NULL OR :keyword = ''
                OR p.name COLLATE Vietnamese_CI_AI LIKE '%' + :keyword + '%'
                OR p.type COLLATE Vietnamese_CI_AI LIKE '%' + :keyword + '%')

          AND (:type IS NULL OR :type = '' OR :type = 'TatCa'
                OR p.type COLLATE Vietnamese_CI_AI LIKE '%' + :type + '%')

          AND (:origin IS NULL OR :origin = ''
                OR p.origin COLLATE Vietnamese_CI_AI LIKE '%' + :origin + '%')

          AND (:minPrice IS NULL OR p.price >= :minPrice)
          AND (:maxPrice IS NULL OR p.price <= :maxPrice)

        ORDER BY p.id DESC
        """, nativeQuery = true)
    List<Product> searchShopAdvanced(
            @Param("keyword") String keyword,
            @Param("type") String type,
            @Param("origin") String origin,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}