package tacos.messaging;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class MessagingConfig {

    @Bean
    public Queue orderQueue() {
        return new Queue("tacocloud.order.queue", false);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper =
                (DefaultJackson2JavaTypeMapper) converter.getJavaTypeMapper();

        // Allow deserialization of our custom classes in the tacos package
        typeMapper.addTrustedPackages("tacos");
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }
}
