package edu.buffalo.distributedsystems.messageproducer;

//@Configuration
//@ConfigurationProperties(prefix = "spring.redis")
//@Setter
//public class RedisConfig {
//
//    private String host;
//    private String password;
//
//    @Bean
//    @Primary
//    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory(RedisConfiguration defaultRedisConfig) {
//        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
//                .useSsl().build();
//        return new LettuceConnectionFactory(defaultRedisConfig, clientConfig);
//    }
//
//    @Bean
//    public RedisConfiguration defaultRedisConfig() {
//        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
//        config.setHostName(host);
//        config.setPassword(RedisPassword.of(password));
//        return config;
//    }
//}
