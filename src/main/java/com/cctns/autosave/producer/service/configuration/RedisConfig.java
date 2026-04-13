package com.cctns.autosave.producer.service.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.LinkedHashMap;

/**
 * This class represents the spring data redis configurations
 * Lettuce configuration to make the application asynchronous and reactive
 */
@Configuration
@Slf4j
public class RedisConfig {

    /**
     * Redis connection factory for the Redis sentinel
     * @param redisProperties (Redis Properties)
     * @return Redis Connection
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {

        RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration();
        sentinelConfig.setMaster(redisProperties.getSentinel().getMaster());

        redisProperties.getSentinel().getNodes().forEach(node -> {
            String[] hostPort = node.split(":");
            sentinelConfig.sentinel(hostPort[0], Integer.parseInt(hostPort[1]));
        });

        if (redisProperties.getPassword() != null) {
            sentinelConfig.setPassword(RedisPassword.of(redisProperties.getPassword()));
        }

        if (redisProperties.getSentinel().getPassword() != null) {
            sentinelConfig.setSentinelPassword(
                    RedisPassword.of(redisProperties.getSentinel().getPassword()));
        }

        LettuceClientConfiguration clientConfig =
                LettuceClientConfiguration.builder()
                        .commandTimeout(redisProperties.getTimeout())
                        .build();

        return new LettuceConnectionFactory(sentinelConfig, clientConfig);
    }

    /**
     * Redis Template for autosave Json Data
     * @param connectionFactory
     * @return
     */
    @Bean(name = "redisJsonTemplate")
    public RedisTemplate<String, LinkedHashMap<String,Object>> redisAutosaveTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, LinkedHashMap<String,Object>> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Redis Template for draft-key in Z-SET
     * @param connectionFactory
     * @return
     */
    @Bean(name = "redisZSetTemplate")
    public RedisTemplate<String, String> redisZSetTemplate(
            RedisConnectionFactory connectionFactory) {

        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());

        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
