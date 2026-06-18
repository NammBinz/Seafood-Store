package springboot.seafoodstore.config;

import springboot.seafoodstore.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

// Báo cho Spring Boot đây là class cấu hình
@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean   // SecurityFilterChain: chuỗi cấu hình bảo mật chính
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Tắt cơ chế bảo vệ CSRF > để form POST hoạt động dễ hơn.
                // Nếu không tắt CSRF, khi submit form có thể bị lỗi 403 Forbidden nếu form ko gửi CSRF token.
                .csrf(csrf -> csrf.disable())

                // Cấu hình quyền truy cập các đường dẫn
                .authorizeHttpRequests(auth -> auth
                        // Truy cập ko cần login
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                        .requestMatchers("/", "/login", "/register", "/shop/**").permitAll()

                        .requestMatchers("/cart/**").hasRole("CUSTOMER")
                        .requestMatchers("/orders/**").hasRole("CUSTOMER")

                        .requestMatchers("/employees/**").hasRole("ADMIN")
                        .requestMatchers("/products/delete/**").hasRole("ADMIN")

                        .requestMatchers("/customers/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/products/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/", "/dashboard").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers("/admin/orders/**").hasAnyRole("ADMIN", "STAFF")

                        // Các Request khác chỉ cần đăng nhập
                        .anyRequest().authenticated()
                )

                // Cấu hình form login
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")   // Khi user bấm login, form post đến /login > Spring Security kiểm tra pass
                        .successHandler((request, response, authentication) -> {
                            boolean isCustomer = authentication.getAuthorities()
                                    .stream()
                                    .anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"));

                            if (isCustomer)
                                response.sendRedirect("/shop"); // CUSTOMER log xong → vào /shop
                            else
                                response.sendRedirect("/dashboard"); // ADMIN / STAFF vào /dashboard
                        })
                        .failureUrl("/login?error=true")    // login false thì chuyển tới trang html > báo lỗi
                        .permitAll()    // Cho phép tất cả user truy cập login
                )

                // Cấu hình logout
                .logout(logout -> logout
                        .logoutUrl("/logout")   // Spring Security xử lý đăng xuất
                        .logoutSuccessUrl("/login?logout=true") // HTML báo đăng xuất thành công
                        .permitAll()    // Cho phép tất cả user logout
                )

                // Gọi hàm authenticationProvider() bên dưới để xác thực tài khoản
                .authenticationProvider(authenticationProvider());

        return http.build();    // trả về SecurityFilterChain
    }

    // Cấu hình kiểm tra đăng nhập
    @Bean
    // Cấu hình bằng DaoAuthenticationProvider trong Spring Security
    public DaoAuthenticationProvider authenticationProvider() {
        // gọi loadUserByUsername(username) để lấy pass đã đc mã hóa > đem so sánh
        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(customUserDetailsService);

        // Spring Security dùng BCrypt > mã hóa/so sánh > so sánh password trong database
        provider.setPasswordEncoder(passwordEncoder());

        return provider;    // trả về bộ xử lý đăng nhập đã cấu hình xong cho Spring Security
    }

    // Mã hóa mật khẩu
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}