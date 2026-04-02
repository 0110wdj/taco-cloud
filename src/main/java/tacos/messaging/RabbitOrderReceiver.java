package tacos.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import tacos.TacoOrder;

@Component
public class RabbitOrderReceiver implements OrderReceiver {

    private static final Logger log = LoggerFactory.getLogger(RabbitOrderReceiver.class);
    private RabbitTemplate rabbit;

    public RabbitOrderReceiver(RabbitTemplate rabbit) {
        this.rabbit = rabbit;
        // 设置接收超时时间为 5 秒，避免长时间阻塞
        this.rabbit.setReceiveTimeout(5000);
    }

    @Override
    public TacoOrder receiveOrder() {
        try {
            TacoOrder order = (TacoOrder) rabbit.receiveAndConvert("tacocloud.order.queue");
            if (order != null) {
                log.info("Received order: {}", order.getId());
            } else {
                log.info("No message available in queue");
            }
            return order;
        } catch (Exception e) {
            log.error("Failed to receive order: {}", e.getMessage());
            return null;
        }
    }
}
