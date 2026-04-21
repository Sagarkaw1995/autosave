package com.cctns.autosave.producer.service.configuration;

import java.io.IOException;
import java.util.LinkedHashMap;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.cctns.autosave.producer.service.core.external.port.MicroserviceComms;
import com.cctns.autosave.producer.service.core.repository.AutosaveRepository;
import com.cctns.autosave.producer.service.core.usecase.AutosaveUseCaseImpl;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class DefaultConfig {

    @Bean
    public AutosaveUseCaseImpl autosaveUseCase(RedisTemplate<String, LinkedHashMap<String, Object>> redisJsonTemplate,
                                               RedisTemplate<String, String> redisZSetTemplate, MicroserviceComms microserviceComms, AutosaveRepository autosaveRepository,
                                               ObjectMapper objectMapper) {
        return new AutosaveUseCaseImpl(redisJsonTemplate, redisZSetTemplate, microserviceComms, autosaveRepository, objectMapper);
    }


    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> {
            builder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            builder.deserializerByType(String.class, new EmptyStringToNullDeserializer());
        };
    }

    public static class EmptyStringToNullDeserializer extends JsonDeserializer<String> {
        @Override
        public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            return (value == null || value.trim().isEmpty()) ? null : value;
        }
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return mapper;
    }
}
