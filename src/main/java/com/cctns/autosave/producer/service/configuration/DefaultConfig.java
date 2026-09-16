package com.cctns.autosave.producer.service.configuration;

import com.cctns.autosave.producer.service.constants.AutosaveRedisKeySchema;
import com.cctns.autosave.producer.service.constants.Constants;
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
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

@Configuration
public class DefaultConfig {

    @Bean
    public AutosaveUseCaseImpl autosaveUseCase(RedisTemplate<String, LinkedHashMap<String, Object>> redisJsonTemplate,
                                               RedisTemplate<String, String> redisZSetTemplate, MicroserviceComms microserviceComms, AutosaveRepository autosaveRepository,
                                               ObjectMapper objectMapper, AutosaveRedisKeySchema autosaveRedisKeySchema) {
        return new AutosaveUseCaseImpl(redisJsonTemplate, redisZSetTemplate, microserviceComms, autosaveRepository, objectMapper, autosaveRedisKeySchema);
    }


    @Bean
	  Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
	    return builder -> {
	      builder.featuresToEnable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);

	      // Empty strings -> null + validate script tags
	      builder.deserializerByType(String.class, new EmptyStringToNullDeserializer());

	      // Register module for Long serialization
	      SimpleModule module = new SimpleModule();
	      module.addSerializer(Long.class, ToStringSerializer.instance);
	      module.addSerializer(Long.TYPE, ToStringSerializer.instance);

	      builder.modules(module, new JavaTimeModule());

	      // Disable timestamp serialization for dates
	      builder.featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	    };
	  }

	  public static class EmptyStringToNullDeserializer extends JsonDeserializer<String> {

	    private static final Pattern SCRIPT_PATTERN =
	        Pattern.compile(Constants.SCRIPT_REGEX);

	    @Override
	    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

	      String value = p.getValueAsString();

	      // Empty strings -> null
	      if (value == null || value.trim().isEmpty()) {
	        return null;
	      }

	      value = value.trim();

	      // Reject script tags
	      if (SCRIPT_PATTERN.matcher(value).find()) {
	        throw new IllegalArgumentException(Constants.ILLEGAL_ARGS_ERRORS);
	      }

	      return value;
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
      //  mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        return mapper;
    }
}
