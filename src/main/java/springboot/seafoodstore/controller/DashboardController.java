package springboot.seafoodstore.controller;

import springboot.seafoodstore.entity.Order;
import springboot.seafoodstore.entity.Product;
import springboot.seafoodstore.repository.CustomerRepository;
import springboot.seafoodstore.repository.EmployeeRepository;
import springboot.seafoodstore.repository.OrderRepository;
import springboot.seafoodstore.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

    private final ProductRepository productRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;

    public DashboardController(ProductRepository productRepository,
                               EmployeeRepository employeeRepository,
                               CustomerRepository customerRepository,
                               OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.employeeRepository = employeeRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {

        long totalProducts = productRepository.count();
        long totalEmployees = employeeRepository.count();
        long totalCustomers = customerRepository.count();
        long totalOrders = orderRepository.count();

        long pendingOrders = orderRepository.countByStatus("PENDING");

        long activeProducts = productRepository.countByStatus(true);
        long inactiveProducts = productRepository.countByStatus(false);

        // Trả về list product sắp hết hàng: quantity <= 10
        List<Product> lowStockProducts = productRepository.findByQuantityLessThanEqual(10);

        // Doanh thu chỉ tính đơn hàng có trạng thái COMPLETED.
        BigDecimal totalRevenue = orderRepository.findAll()
                .stream()
                .filter(order -> order.getStatus().equals("COMPLETED"))
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Cộng tất cả lại

        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("totalOrders", totalOrders);
        model.addAttribute("pendingOrders", pendingOrders);
        model.addAttribute("activeProducts", activeProducts);
        model.addAttribute("inactiveProducts", inactiveProducts);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("totalRevenue", totalRevenue);

        return "dashboard/index";
    }
}