package edu.buffalo.distributedsystems.eventbrokerc;

import edu.buffalo.distributedsystems.serviceutil.dto.ConsumerMessage;
import edu.buffalo.distributedsystems.serviceutil.dto.EventMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@SpringBootApplication
public class EventBrokerCApplication {

    private static final Logger logger = LoggerFactory.getLogger(EventBrokerCApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EventBrokerCApplication.class, args);
    }

    @Bean
    public ReactiveRedisOperations<String, EventMessage> messageTemplate(LettuceConnectionFactory lettuceConnectionFactory){
        RedisSerializer<EventMessage> valueSerializer = new Jackson2JsonRedisSerializer<>(EventMessage.class);
        RedisSerializationContext<String, EventMessage> serializationContext = RedisSerializationContext.<String, EventMessage>newSerializationContext(RedisSerializer.string())
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, serializationContext);
    }

    @Bean
    public ReactiveRedisOperations<String, ConsumerMessage> consumerMessageTemplate(LettuceConnectionFactory lettuceConnectionFactory){
        RedisSerializer<ConsumerMessage> valueSerializer = new Jackson2JsonRedisSerializer<>(ConsumerMessage.class);
        RedisSerializationContext<String, ConsumerMessage> serializationContext = RedisSerializationContext.<String, ConsumerMessage>newSerializationContext(RedisSerializer.string())
                .value(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(lettuceConnectionFactory, serializationContext);
    }
}
