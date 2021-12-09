package edu.buffalo.distributedsystems.incidenttracker.kafka.core.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${kafka.bootstrapAddress}")
    private String bootstrapAddress;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic internalDomainHealthTopic() {
        return new NewTopic("internal-domain-health", 3, (short) 1);
    }

    @Bean
    public NewTopic externalDomainHealthTopic() {
        return new NewTopic("external-domain-health", 3, (short) 1);
    }

    @Bean
    public NewTopic tpDomainHealthTopic() {
        return new NewTopic("tp-domain-health", 3, (short) 1);
    }
}