package tacos.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.support.SessionStatus;

import jakarta.validation.Valid;
import java.security.Principal;

import org.springframework.validation.Errors;
import tacos.TacoOrder;
import tacos.User;
import tacos.data.OrderRepository;
import tacos.data.UserRepository;
import tacos.messaging.OrderMessagingService;

@Controller
@RequestMapping("/orders")
public class OrderController {

  private static final Logger log = LoggerFactory.getLogger(OrderController.class);
  private OrderRepository orderRepo;
  private UserRepository userRepo;
  private OrderMessagingService messageService;

  public OrderController(OrderRepository orderRepo, UserRepository userRepo,
      OrderMessagingService messageService) {
    this.orderRepo = orderRepo;
    this.userRepo = userRepo;
    this.messageService = messageService;
  }

  @GetMapping
  public String ordersForUser(Principal principal, Model model) {
    User user = getCurrentUser(principal);
    // Get all orders (in a real app, you'd filter by user)
    model.addAttribute("orders", orderRepo.findAll());
    return "orders";
  }

  @GetMapping("/current")
  public String orderForm(Principal principal, Model model) {
    User user = getCurrentUser(principal);
    TacoOrder order = new TacoOrder();
    if (user != null) {
      order.setDeliveryName(user.getFullname());
      order.setDeliveryStreet(user.getStreet());
      order.setDeliveryCity(user.getCity());
      order.setDeliveryState(user.getState());
      order.setDeliveryZip(user.getZip());
    }
    model.addAttribute("order", order);
    return "orderForm";
  }

  @PostMapping
  public String processOrder(@Valid @ModelAttribute("order") TacoOrder order, Errors errors,
      SessionStatus sessionStatus, 
      Principal principal) {
    if (errors.hasErrors()) {
      System.out.println("Errors: " + errors);
      // order 对象已经通过 @ModelAttribute 添加到模型中
      return "orderForm";
    }
    
    User user = getCurrentUser(principal);
    order.setUser(user);
    order.setPlacedAt(new java.util.Date());
    orderRepo.save(order);
    messageService.sendOrder(order);
    sessionStatus.setComplete();

    log.info("Order submitted: " + order);
    return "redirect:/";
  }

  private User getCurrentUser(Principal principal) {
      if (principal == null) {
          return null;
      }
      
      // Handle Form Login (User is Authentication principal)
      if (principal instanceof Authentication) {
          Authentication auth = (Authentication) principal;
          Object principalObj = auth.getPrincipal();
          
          if (principalObj instanceof User) {
              return (User) principalObj;
          }
          
          // Handle OAuth2 Login
          if (principalObj instanceof OAuth2User) {
              OAuth2User oauth2User = (OAuth2User) principalObj;
              String username = oauth2User.getAttribute("login"); // GitHub username
              if (username == null) {
                  // Fallback for other providers if needed
                  username = oauth2User.getAttribute("email"); 
              }
              
              User user = userRepo.findByUsername(username);
              if (user == null) {
                  // Auto-register OAuth2 user
                  String fullname = oauth2User.getAttribute("name");
                  if (fullname == null) fullname = username;
                  
                  // Create user with dummy data for required fields
                  user = new User(username, "", fullname, "", "", "", "", "");
                  return userRepo.save(user);
              }
              return user;
          }
      }
      
      // Fallback lookup by username (for standard Principal)
      return userRepo.findByUsername(principal.getName());
  }
}