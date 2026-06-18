package springboot.seafoodstore.controller;

import springboot.seafoodstore.entity.Customer;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springboot.seafoodstore.repository.CustomerRepository;

@Controller
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerRepository customerRepository;

    public CustomerController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String listCustomers(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            model.addAttribute("customers",
                    customerRepository.findByFullNameContainingIgnoreCaseOrPhoneContainingIgnoreCase(
                            keyword, keyword
                    ));
        } else {
            model.addAttribute("customers", customerRepository.findAll());
        }

        model.addAttribute("keyword", keyword);
        return "customers/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Customer customer = new Customer();
        customer.setStatus(true);
        model.addAttribute("customer", customer);
        return "customers/form";
    }

    @PostMapping("/save")
    public String saveCustomer(@Valid @ModelAttribute("customer") Customer customer,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "customers/form";
        }

        customerRepository.save(customer);
        return "redirect:/customers";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        model.addAttribute("customer", customer);
        return "customers/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Long id) {
        customerRepository.deleteById(id);
        return "redirect:/customers";
    }
}