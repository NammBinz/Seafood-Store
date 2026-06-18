package springboot.seafoodstore.controller;

import springboot.seafoodstore.entity.Employee;
import springboot.seafoodstore.repository.EmployeeRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public String listEmployees(@RequestParam(required = false) String keyword, Model model) {
        if (keyword != null && !keyword.trim().isEmpty())
            model.addAttribute("employees",
                    employeeRepository.findByFullNameContainingIgnoreCaseOrPhoneContainingIgnoreCaseOrPositionContainingIgnoreCase(
                            keyword, keyword, keyword
                    ));
        else
            model.addAttribute("employees", employeeRepository.findAll());

        model.addAttribute("keyword", keyword);
        return "employees/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        Employee employee = new Employee();
        employee.setStatus(true);
        model.addAttribute("employee", employee);
        return "employees/form";
    }

    @PostMapping("/save")
    // @ModelAttribute: lấy dữ liệu từ Form gán vào employee
    // @Valid: có lỗi thì gán vào result
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult result) {
        if (result.hasErrors())
            return "employees/form";

        employeeRepository.save(employee);
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        model.addAttribute("employee", employee);
        return "employees/form";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeRepository.deleteById(id);
        return "redirect:/employees";
    }
}