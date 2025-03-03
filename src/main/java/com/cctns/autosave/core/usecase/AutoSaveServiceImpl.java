package com.cctns.autosave.core.usecase;

import com.cctns.autosave.core.domain.AutoSaveDomain;
import com.cctns.autosave.extAdapters.S3ServiceClient;
import com.cctns.autosave.web.dto.request.AutoSaveRequestDto;
import com.cctns.autosave.web.dto.request.JsonDataDto;
import com.cctns.autosave.web.dto.response.ApiResponse;
import com.cctns.autosave.web.dto.response.AutoSaveResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class AutoSaveServiceImpl implements AutoSaveUseCase{

    private final RedisTemplate<String,LinkedHashMap<String,Object>> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final S3ServiceClient s3ServiceClient;

    @Value("${redis.ttl.minutes}")
    private Long timeToLive;

    @Value("${redis.buffer.time.minutes}")
   private Long bufferedTimeInSeconds;

    public AutoSaveServiceImpl(RedisTemplate<String,LinkedHashMap<String,Object>> redisTemplate,StringRedisTemplate stringRedisTemplate,S3ServiceClient s3ServiceClient) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.s3ServiceClient = s3ServiceClient;
    }

    /**
     * This is the service method to
     * @param autoSaveData
     * @return
     */
    @Override
    public Object submitAutoSaveData(AutoSaveDomain autoSaveData) {

        log.info("The TTL Configured Is : {}", timeToLive);
        log.info("The Buffered Time Configured Is : {}", bufferedTimeInSeconds);

        LinkedHashMap<String,Object> shadowObject = new LinkedHashMap<>();
        shadowObject.put("","");

        // Execute the Redis operations in a transaction
        return redisTemplate.execute((RedisCallback<Object>) connection -> {
            connection.multi();
            try {
                redisTemplate.opsForValue().set("ShadowKey:" + autoSaveData.getKey(), shadowObject, Duration.ofSeconds(timeToLive));
                redisTemplate.opsForValue().set(autoSaveData.getKey(), autoSaveData.getJsonData(), Duration.ofSeconds(timeToLive + bufferedTimeInSeconds));
                LinkedHashMap<String, Object> data = redisTemplate.opsForValue().get(autoSaveData.getKey());
                connection.exec();
                return data;
            } catch (Exception e) {
                connection.discard();
                log.error("Error during Redis transaction: {}", e.getMessage());
                return null;
            }
        });
    }

    @Override
    public Object getAutoSaveData(String key) {
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            //If the key is in redis then get the data + increate the TTL time
            LinkedHashMap<String, Object> jsonData = redisTemplate.opsForValue().get(key);
            LinkedHashMap<String,Object> shadowObject = new LinkedHashMap<>();
            shadowObject.put("","");
            redisTemplate.opsForValue().set("ShadowKey:"+key,shadowObject,Duration.ofSeconds(timeToLive+timeToLive));
            redisTemplate.opsForValue().set(key, jsonData, Duration.ofSeconds(timeToLive + bufferedTimeInSeconds));
            AutoSaveResponseDto response = new AutoSaveResponseDto();
            response.setJsonData(jsonData);
            response.setKey(key);
            return response;
        } else {
            //If the key is expired then get the data from S3 :
            JsonDataDto jsonData = new JsonDataDto();
            jsonData.setKey(key);
           ResponseEntity<AutoSaveResponseDto> response =  s3ServiceClient.getAutoSaveData(jsonData);
            LinkedHashMap<String,Object> shadowObject = new LinkedHashMap<>();
            shadowObject.put("","");
            redisTemplate.opsForValue().set("ShadowKey:"+key,shadowObject,Duration.ofSeconds(timeToLive+timeToLive));
            redisTemplate.opsForValue().set(key, (LinkedHashMap<String, Object>) response.getBody().getJsonData(), Duration.ofSeconds(timeToLive + bufferedTimeInSeconds));
            return response.getBody();
        }
    }
}
