package springboot.seafoodstore.controller;

import jakarta.transaction.Transactional;
import springboot.seafoodstore.entity.*;
import springboot.seafoodstore.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

// Đặt hàng và xem đơn hàng của khách hàng
@Controller
@RequestMapping("/orders")
public class OrderController {

    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public OrderController(CustomerRepository customerRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           OrderRepository orderRepository,
                           OrderDetailRepository orderDetailRepository) {
        this.customerRepository = customerRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    // Tìm customer theo username của principal (chứa thông tin tài khoản đang đăng nhập)
    private Customer getCurrentCustomer(Principal principal) {
        return customerRepository.findByUserUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng đang đăng nhập"));
    }

    // Đặt hàng
    @PostMapping("/checkout")
    @Transactional  // Nếu có lỗi, toàn bộ giao dịch sẽ bị hủy
    public String checkout(Principal principal,
                           @RequestParam(required = false) List<Long> cartItemIds,
                           @RequestParam(required = false) List<Integer> quantities,
                           RedirectAttributes redirectAttributes) {
        // RedirectAttributes: gửi thông báo sau khi redirect

        Customer customer = getCurrentCustomer(principal);

        // Cập nhật lại số lượng trong giỏ trước khi đặt hàng
        if (cartItemIds != null && quantities != null && cartItemIds.size() == quantities.size()) {
            for (int i = 0; i < cartItemIds.size(); i++) {
                Long cartItemId = cartItemIds.get(i);
                Integer newQuantity = quantities.get(i);

                CartItem cartItem = cartItemRepository.findById(cartItemId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ"));

                if (!cartItem.getCustomer().getId().equals(customer.getId()))
                    throw new RuntimeException("Bạn không có quyền cập nhật giỏ hàng này");

                Product product = cartItem.getProduct();

                if (newQuantity == null || newQuantity <= 0) {
                    cartItemRepository.delete(cartItem);
                    continue;
                }

                if (newQuantity > product.getQuantity()) {
                    redirectAttributes.addFlashAttribute(
                            "error",
                            "Sản phẩm " + product.getName() + " không đủ số lượng tồn kho!"
                    );
                    return "redirect:/cart";
                }

                cartItem.setQuantity(newQuantity);
                cartItemRepository.save(cartItem);
            }
        }

        // Lấy tất cả dòng giỏ hàng của customer
        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);

        // Nếu giỏ hàng rỗng thì hiện lỗi và quay về /cart
        if (cartItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Giỏ hàng đang trống!");
            return "redirect:/cart";
        }

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            Product product = item.getProduct();

            if (item.getQuantity() > product.getQuantity()) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        "Sản phẩm " + product.getName() + " không đủ số lượng tồn kho!"
                );
                return "redirect:/cart";
            }

            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // Tạo đơn hàng chính
        Order order = new Order();
        order.setCustomer(customer);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");

        Order savedOrder = orderRepository.save(order);

        // Lưu đơn hàng chính
        for (CartItem item : cartItems) {
            // Với mỗi product tạo 1 OrderDetail
            Product product = item.getProduct();

            OrderDetail detail = new OrderDetail();
            detail.setOrder(savedOrder);
            detail.setProduct(product);
            detail.setProductName(product.getName());
            detail.setPrice(product.getPrice());
            detail.setQuantity(item.getQuantity());
            detail.setSubtotal(item.getSubtotal());

            // Lưu bảng order_details
            orderDetailRepository.save(detail);

            // Trừ tồn kho
            product.setQuantity(product.getQuantity() - item.getQuantity());
            productRepository.save(product);    // Lưu lại product vào db
        }

        // Xóa giỏ hàng sau khi đặt
        cartItemRepository.deleteByCustomer(customer);

        redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
        return "redirect:/orders/my-orders";
    }

    // Xem đơn hàng
    @GetMapping("/my-orders")
    public String myOrders(Model model, Principal principal) {

        // Lấy customer đang đăng nhập
        Customer customer = getCurrentCustomer(principal);

        // Sắp xếp đơn hàng mới nhất lên trước > gửi qua HTML
        model.addAttribute("orders",
                orderRepository.findByCustomerOrderByCreatedAtDesc(customer));

        return "orders/my-orders";
    }

    // Xem chi tiết đơn hàng
    @GetMapping("/detail/{id}")
    public String orderDetail(@PathVariable Long id, Model model, Principal principal) {

        // Lấy customer đang đăng nhập
        Customer customer = getCurrentCustomer(principal);

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng"));

        // Kiểm tra xem order này có thuộc customer đang đăng nhập ko ?
        if (!order.getCustomer().getId().equals(customer.getId()))
            throw new RuntimeException("Bạn không có quyền xem đơn hàng này");

        // Lấy chi tiết đơn hàng
        List<OrderDetail> details = orderDetailRepository.findByOrder(order);

        // Gửi dữ liệu sang HTML
        model.addAttribute("order", order);
        model.addAttribute("details", details);

        return "orders/detail";
    }
}