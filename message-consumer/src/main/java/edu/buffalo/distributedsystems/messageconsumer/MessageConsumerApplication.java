package edu.buffalo.distributedsystems.messageconsumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.buffalo.distributedsystems.serviceutil.dto.ConsumerMessage;
import edu.buffalo.distributedsystems.serviceutil.dto.EventMessage;
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
public class MessageConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageConsumerApplication.class, args);
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
