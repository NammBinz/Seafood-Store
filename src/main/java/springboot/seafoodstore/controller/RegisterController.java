package springboot.seafoodstore.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import springboot.seafoodstore.entity.Customer;
import springboot.seafoodstore.entity.User;
import springboot.seafoodstore.repository.CustomerRepository;
import springboot.seafoodstore.repository.UserRepository;
import springboot.seafoodstore.service.CustomUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class RegisterController {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;

    public RegisterController(UserRepository userRepository,
                              CustomerRepository customerRepository,
                              PasswordEncoder passwordEncoder,
                              CustomUserDetailsService customUserDetailsService) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
    }

    @GetMapping("/register")
    public String showRegisterForm() {
        return "auth/register";
    }

    // Nếu lưu User hoặc lưu Customer lỗi thì hủy lưu, ko lưu gì cả
    @Transactional
    @PostMapping("/register")
    public String registerCustomer(@RequestParam String fullName,
                                   @RequestParam String phone,
                                   @RequestParam String email,
                                   @RequestParam String address,
                                   @RequestParam String username,
                                   @RequestParam String password,
                                   Model model,
                                   HttpServletRequest request,
                                   HttpServletResponse response) {

        fullName = fullName.trim();
        phone = phone.trim().replaceAll("[\\s.-]", "");
        username = username.trim();

        if (fullName.isEmpty()) {
            model.addAttribute("error", "Họ tên không được để trống!");
            return "auth/register";
        }

        if (!phone.matches("0\\d{9}")) {
            model.addAttribute("error", "Số điện thoại phải có 10 số và bắt đầu bằng 0. Ví dụ: 0912345678");
            return "auth/register";
        }

        if (username.isEmpty()) {
            model.addAttribute("error", "Tên đăng nhập không được để trống!");
            return "auth/register";
        }

        if (password.length() < 6) {
            model.addAttribute("error", "Mật khẩu phải có ít nhất 6 ký tự!");
            return "auth/register";
        }

        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            return "auth/register";
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole("CUSTOMER");
        user.setStatus(true);

        User savedUser = userRepository.save(user);  // Lưu tài khoản vào bảng users trong db

        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setStatus(true);
        customer.setUser(savedUser);

        customerRepository.save(customer);  // bảng customers sẽ lưu user_id trỏ đến users.id

        // Đăng kí tài khoản xong tự login và mở /shop
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        // Tạo đối tượng Authentication để Spring Security hiểu rằng user này đã đăng nhập
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,   // ko lưu pass nữa
                        userDetails.getAuthorities()    // lấy role
                );

        // Tạo một SecurityContext rỗng - lưu thông tin người dùng đang đăng nhập
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // Đưa thông tin đăng nhập authentication vào SecurityContext
        context.setAuthentication(authentication);
        // Gắn SecurityContext vừa tạo vào SecurityContextHolder
        SecurityContextHolder.setContext(context);  // giữ thông tin đăng nhập trong request hiện tại

        // Tạo repository để lưu SecurityContext vào HttpSession
        HttpSessionSecurityContextRepository securityContextRepository =
                new HttpSessionSecurityContextRepository();

        // Lưu SecurityContext vào session của trình duyệt
        securityContextRepository.saveContext(context, request, response);
        // Khi redirect, hệ thống vẫn nhớ user đã đăng nhập

        return "redirect:/shop";
    }
}