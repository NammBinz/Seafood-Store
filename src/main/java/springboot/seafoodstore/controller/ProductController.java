package springboot.seafoodstore.controller;

import springboot.seafoodstore.entity.Product;
import springboot.seafoodstore.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public String listProducts(@RequestParam(required = false) String keyword,
                               @RequestParam(required = false) String origin,
                               @RequestParam(required = false) BigDecimal minPrice,
                               @RequestParam(required = false) BigDecimal maxPrice,
                               @RequestParam(required = false) Boolean status,
                               @RequestParam(required = false, defaultValue = "false") boolean lowStock,
                               Model model) {

        model.addAttribute("products",
                productRepository.searchAdvanced(keyword, origin, minPrice, maxPrice, status, lowStock));

        // Gửi thông tin tìm kiếm sang trang HTML > giữ lại nội dung đã tìm kiếm trong ô search
        model.addAttribute("keyword", keyword);
        model.addAttribute("origin", origin);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("status", status);
        model.addAttribute("lowStock", lowStock);

        return "products/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Product product = new Product();
        product.setStatus(true);
        model.addAttribute("product", product); // Đưa product sang file HTML với tên là "product"
        return "products/form";
    }

    @PostMapping("/save")
    // @ModelAttribute: tự động lấy dữ liệu từ form và gán vào Product
    // @Valid: nếu form nhập sai hoặc bỏ trống > result chứa kết quả kiểm tra lỗi
    public String saveProduct(@Valid @ModelAttribute("product") Product product,
                              BindingResult result) {
        if (result.hasErrors())
            return "products/form";

        productRepository.save(product);
        return "redirect:/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        model.addAttribute("product", product);
        return "products/form";
    }

    // Khi bấm xóa sẽ chuyển sp thành ngừng bán
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        product.setStatus(false);
        productRepository.save(product);

        return "redirect:/products";
    }
}