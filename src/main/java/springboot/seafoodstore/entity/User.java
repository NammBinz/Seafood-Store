package springboot.seafoodstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // username ko đc để trống, ko trùng nhau
    @Column(nullable = false, unique = true, columnDefinition = "NVARCHAR(50)")
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, columnDefinition = "NVARCHAR(20)")
    private String role;

    private Boolean status = true;
}