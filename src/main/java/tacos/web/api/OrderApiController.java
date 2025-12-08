package tacos.web.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import tacos.TacoOrder;
import tacos.data.OrderRepository;
import tacos.messaging.OrderMessagingService;

@RestController
@RequestMapping(path = "/api/orders", produces = "application/json")
@CrossOrigin(origins = "*")
public class OrderApiController {

    private OrderRepository repo;
    private OrderMessagingService messageService;

    public OrderApiController(OrderRepository repo, OrderMessagingService messageService) {
        this.repo = repo;
        this.messageService = messageService;
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public TacoOrder postOrder(@RequestBody TacoOrder order) {
        messageService.sendOrder(order);
        return repo.save(order);
    }

    @GetMapping("/convertAndSend/order")
    public String convertAndSendOrder() {
        TacoOrder order = buildOrder();
        messageService.sendOrder(order);
        return "Convert and sent order";
    }

    private TacoOrder buildOrder() {
        TacoOrder order = new TacoOrder();
        order.setDeliveryName("Test User");
        order.setDeliveryStreet("123 Test St");
        order.setDeliveryCity("Test City");
        order.setDeliveryState("TS");
        order.setDeliveryZip("12345");
        order.setCcNumber("4111111111111111");
        order.setCcExpiration("12/25");
        order.setCcCVV("123");
        return order;
    }
}
