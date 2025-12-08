package tacos.messaging;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;

import tacos.Ingredient;
import tacos.Taco;
import tacos.TacoOrder;
import tacos.User;

@Configuration
public class MessagingConfig {

    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        MappingJackson2MessageConverter messageConverter = 
                new MappingJackson2MessageConverter();
        messageConverter.setTypeIdPropertyName("_typeId");

        Map<String, Class<?>> typeIdMappings = new HashMap<>();
        typeIdMappings.put("order", TacoOrder.class);
        typeIdMappings.put("taco", Taco.class);
        typeIdMappings.put("ingredient", Ingredient.class);
        typeIdMappings.put("user", User.class);
        messageConverter.setTypeIdMappings(typeIdMappings);

        return messageConverter;
    }
}
