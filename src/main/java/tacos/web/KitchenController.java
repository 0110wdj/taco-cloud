package tacos.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import tacos.TacoOrder;
import tacos.messaging.OrderReceiver;

@Controller
@RequestMapping("/kitchen")
public class KitchenController {

    private OrderReceiver orderReceiver;

    public KitchenController(OrderReceiver orderReceiver) {
        this.orderReceiver = orderReceiver;
    }

    @GetMapping
    public String showKitchen() {
        return "kitchen";
    }

    @PostMapping("/receive")
    public String receiveOrder(Model model) {
        TacoOrder order = orderReceiver.receiveOrder();
        if (order != null) {
            model.addAttribute("order", order);
            model.addAttribute("message", "订单接收成功！");
        } else {
            model.addAttribute("message", "队列中没有待处理的订单");
        }
        return "kitchen";
    }
}
