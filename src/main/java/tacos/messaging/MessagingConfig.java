package tacos.messaging;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

@Configuration
public class MessagingConfig {

    @Bean
    public org.apache.kafka.clients.admin.NewTopic orderTopic() {
        return TopicBuilder.name("tacocloud.order.queue")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
