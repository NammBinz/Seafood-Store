package springboot.seafoodstore.controller;

import springboot.seafoodstore.entity.CartItem;
import springboot.seafoodstore.entity.Customer;
import springboot.seafoodstore.entity.Product;
import springboot.seafoodstore.repository.CartItemRepository;
import springboot.seafoodstore.repository.CustomerRepository;
import springboot.seafoodstore.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;

    public CartController(CartItemRepository cartItemRepository,
                          ProductRepository productRepository,
                          CustomerRepository customerRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
    }

    // Tìm customer theo username của principal (chứa thông tin tài khoản đang đăng nhập)
    private Customer getCurrentCustomer(Principal principal) {
        return customerRepository.findByUserUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng đang đăng nhập"));
    }

    @GetMapping
    public String viewCart(Model model, Principal principal) {
        // Lấy customer đang đăng nhập
        Customer customer = getCurrentCustomer(principal);

        // Lấy tất cả dòng giỏ hàng của customer
        List<CartItem> cartItems = cartItemRepository.findByCustomer(customer);

        BigDecimal totalAmount = cartItems.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);

        return "cart/index";
    }

    @GetMapping("/add/{productId}")
    public String addToCart(@PathVariable Long productId,
                            Principal principal,
                            RedirectAttributes redirectAttributes) {
        // RedirectAttributes: gửi thông báo sau khi redirect

        Customer customer = getCurrentCustomer(principal);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Nếu hết hàng thì hiện lỗi và quay về /shop
        if (product.getQuantity() == null || product.getQuantity() <= 0) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm đã hết hàng!");
            return "redirect:/shop";
        }

        // Tìm trong giỏ của khách hàng xem đã có sản phẩm này chưa
        CartItem cartItem = cartItemRepository.findByCustomerAndProduct(customer, product)
                .orElse(null);

        // Nếu chưa có thì tạo cartItem mới
        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setCustomer(customer);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
        } else {
            if (cartItem.getQuantity() + 1 > product.getQuantity()) {
                redirectAttributes.addFlashAttribute("error", "Số lượng trong giỏ đã vượt quá tồn kho!");
                return "redirect:/cart";
            }

            cartItem.setQuantity(cartItem.getQuantity() + 1);
        }

        cartItemRepository.save(cartItem);

        redirectAttributes.addFlashAttribute("success", "Đã thêm sản phẩm vào giỏ hàng!");
        return "redirect:/cart";
    }

    @GetMapping("/delete/{id}")
    public String deleteCartItem(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        cartItemRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa sản phẩm khỏi giỏ hàng!");
        return "redirect:/cart";
    }
}