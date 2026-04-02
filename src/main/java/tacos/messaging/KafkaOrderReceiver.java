package tacos.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import tacos.TacoOrder;
import org.springframework.kafka.annotation.KafkaListener;

@Component
public class KafkaOrderReceiver implements OrderReceiver {

    private static final Logger log = LoggerFactory.getLogger(KafkaOrderReceiver.class);
    private TacoOrder lastOrder;

    @KafkaListener(topics = "tacocloud.order.queue")
    public void receiveOrder(TacoOrder order) {
        log.info("Received order: {}", order.getId());
        this.lastOrder = order;
    }

    @Override
    public TacoOrder receiveOrder() {
        TacoOrder order = lastOrder;
        lastOrder = null;
        return order;
    }
}
