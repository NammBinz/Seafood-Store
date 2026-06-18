package springboot.seafoodstore.controller;

import org.springframework.web.bind.annotation.RequestParam;
import springboot.seafoodstore.entity.Product;
import springboot.seafoodstore.repository.ProductRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.util.List;

@Controller
public class ShopController {

    private final ProductRepository productRepository;

    public ShopController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // Lọc sản phẩm
    @GetMapping("/shop")
    public String shop(@RequestParam(required = false, defaultValue = "TatCa") String type,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String origin,
                       @RequestParam(required = false) BigDecimal minPrice,
                       @RequestParam(required = false) BigDecimal maxPrice,
                       Model model) {

        if (keyword != null)
            keyword = keyword.trim();

        if (origin != null)
            origin = origin.trim();

        List<Product> products = productRepository.searchShopAdvanced(
                keyword,
                type,
                origin,
                minPrice,
                maxPrice
        );

        model.addAttribute("products", products);
        model.addAttribute("typeCurrent", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("origin", origin);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "shop/index";
    }

    // Hàm bỏ dấu tiếng Việt
    private String removeAccent(String text) {
        if (text == null)
            return "";


        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{M}","")
                .replace("Đ","D")
                .replace("đ","d");
    }
}