package tacos.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.validation.Valid;
import org.springframework.validation.Errors;
import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;

@Controller
@RequestMapping("/orders")
public class OrderController {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);
  private OrderRepository orderRepo;

  public OrderController(OrderRepository orderRepo) {
    this.orderRepo = orderRepo;
  }

  @GetMapping
  public String ordersForUser(Authentication authentication, Model model) {
    User user = (User) authentication.getPrincipal();
    // Get all orders (in a real app, you'd filter by user)
    model.addAttribute("orders", orderRepo.findAll());
    return "orders";
  }

  @GetMapping("/current")
  public String orderForm(Model model) {
    model.addAttribute("order", new TacoOrder());
    return "orderForm";
  }

  @PostMapping
  public String processOrder(@Valid @ModelAttribute("order") TacoOrder order, Errors errors,
      SessionStatus sessionStatus, Model model) {
    if (errors.hasErrors()) {
      System.out.println("Errors: " + errors);
      // order 对象已经通过 @ModelAttribute 添加到模型中
      return "orderForm";
    }
    order.setPlacedAt(new java.util.Date());
    orderRepo.save(order);
    sessionStatus.setComplete();

    log.info("Order submitted: " + order);
    return "redirect:/";
  }

}