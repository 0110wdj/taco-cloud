package tacos.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import org.springframework.validation.Errors;
import tacos.TacoOrder;

@Controller
@RequestMapping("/orders")
public class OrderController {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);

  @GetMapping("/current")
  public String orderForm(Model model) {
    model.addAttribute("order", new TacoOrder());
    return "orderForm";
  }

  @PostMapping
  public String processOrder(@Valid @ModelAttribute("order") TacoOrder order, Errors errors, Model model) {
    if (errors.hasErrors()) {
      System.out.println("Errors: " + errors);
      // order 对象已经通过 @ModelAttribute 添加到模型中
      return "orderForm";
    }
    log.info("Order submitted: " + order);
    return "redirect:/";
  }

}