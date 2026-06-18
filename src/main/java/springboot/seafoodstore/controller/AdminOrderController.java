package springboot.seafoodstore.controller;

import springboot.seafoodstore.entity.Order;
import springboot.seafoodstore.entity.OrderDetail;
import springboot.seafoodstore.repository.OrderDetailRepository;
import springboot.seafoodstore.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public AdminOrderController(OrderRepository orderRepository,
                                OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    // Đếm đơn chờ xử lý
    @GetMapping
    public String listOrders(Model model) {
        model.addAttribute("orders", orderRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("pendingCount", orderRepository.countByStatus("PENDING"));

        return "admin-orders/list";
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/detail/{id}")
    public String detailOrder(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        List<OrderDetail> details = orderDetailRepository.findByOrder(order);

        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "admin-orders/detail";
    }

    // Cập nhật trạng thái đơn hàng
    @PostMapping("/update-status")
    public String updateStatus(@RequestParam Long orderId,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        order.setStatus(status);
        orderRepository.save(order);

        redirectAttributes.addFlashAttribute("success", "Cập nhật trạng thái đơn hàng thành công!");
        return "redirect:/admin/orders/detail/" + orderId;
    }
}