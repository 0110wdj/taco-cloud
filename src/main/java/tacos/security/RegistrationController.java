package tacos.security;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import tacos.data.UserRepository;
@Controller
@RequestMapping("/register")
public class RegistrationController {

  private UserRepository userRepo;
  private PasswordEncoder passwordEncoder;

  public RegistrationController(
    UserRepository userRepo, PasswordEncoder passwordEncoder) {
      this.userRepo = userRepo;
      this.passwordEncoder = passwordEncoder;
  }

  @GetMapping
  public String registerForm() {
    return "registration";
  }

  @PostMapping
  public String processRegistration(RegistrationForm form) {
    // 检查用户名是否已存在
    if (userRepo.findByUsername(form.getUsername()) != null) {
      // 如果用户名已存在，返回到注册页面（实际项目应该添加错误提示）
      return "redirect:/register?error=usernameExists";
    }
    userRepo.save(form.toUser(passwordEncoder));
    return "redirect:/login";
  }
}