package com.cctns.autosave.configuration;

import com.cctns.autosave.core.domain.AutoSaveRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class RedisExpireKeyListener  {


    private final RedisTemplate<String, LinkedHashMap<String, Object>> redisTemplate;

    private final KafkaTemplate<String, AutoSaveRequestDto> kafkaTemplate;

    @Value("${redis.stream.name}")
    private String redisStreamName;



    public RedisExpireKeyListener(RedisTemplate<String, LinkedHashMap<String, Object>> redisTemplate,
                                  KafkaTemplate<String, AutoSaveRequestDto> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Redis Key expiry listener which will listen to all expire key notifications
     * @param message (Redis message)
     * @throws JsonProcessingException (When serialization and deserialization fails )
     */
    public void onMessage(String message) throws JsonProcessingException {
        log.info("The Message from the Notification is : {}", message);
        String regex = ".*(ShadowKey:|shadow:).*";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher1 = pattern.matcher(message);
        ObjectMapper objectMapper = new ObjectMapper();
        if (matcher1.find()) {
            log.info("The Shadow Key Expired : {}",message);
            String[] key = message.split(":");
            String expiredKey = key[1];
            log.info("The Key Received : {}",expiredKey);
           LinkedHashMap<String,Object> data = redisTemplate.execute(new SessionCallback<LinkedHashMap<String, Object>>() {
               @Override
               public LinkedHashMap<String, Object> execute(RedisOperations operations) throws DataAccessException {

                   operations.watch(expiredKey);
                   LinkedHashMap<String,Object> jsonData = (LinkedHashMap<String, Object>) operations.opsForValue().get(expiredKey);
                 operations.multi();
                   operations.opsForStream().add(redisStreamName, Map.of("key",expiredKey,"value",jsonData));
                   operations.delete(expiredKey);
                   log.info("Extrated The Value Associated With Key : {} And Value : {}",expiredKey,jsonData);
                   List<Object> result = operations.exec();

                   if(result == null || result.isEmpty()){
                       log.info("The Transaction Is Failded The Expired Key Was Changed !");
                       return null;
                   }
                   return jsonData;
            }});

           if(data instanceof LinkedHashMap<?,?>){
               log.info("Yes The Data is of instance of LHM : {}",data);
               AutoSaveRequestDto messageKafka = new AutoSaveRequestDto();
               messageKafka.setKey(expiredKey);
               messageKafka.setJsonData(objectMapper.writeValueAsString(data));
               //Publis the kafka message
               kafkaTemplate.send("key-topic",expiredKey,messageKafka);
           }
        } else {
            log.info("The Primary/Main Key Expired : {}",message);
        }
    }
}
