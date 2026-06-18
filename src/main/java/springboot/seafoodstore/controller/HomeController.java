package springboot.seafoodstore.controller;

import springboot.seafoodstore.entity.Product;
import springboot.seafoodstore.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ProductRepository productRepository;

    public HomeController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Product> featuredProducts = productRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() != null && p.getStatus())
                .filter(p -> p.getQuantity() != null && p.getQuantity() > 0)
                .limit(6)
                .toList();
        // Lấy sản phẩm nổi bật > gửi sang HTML > mở /home

        model.addAttribute("featuredProducts", featuredProducts);

        return "home";
    }
}