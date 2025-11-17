package com.cctns.autosave.core.usecase;

import com.cctns.autosave.constants.Constants;
import com.cctns.autosave.core.domain.AutoSaveDomain;
import com.cctns.autosave.core.domain.AutoSaveRequestDto;
import com.cctns.autosave.core.exception.InvalidModuleNameException;
import com.cctns.autosave.core.repository.*;
import com.cctns.autosave.extAdapters.S3ServiceClient;
import com.cctns.autosave.web.dto.request.JsonDataDto;
import com.cctns.autosave.web.dto.response.AutoSaveResponseDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Service
public class AutoSaveServiceImpl implements AutoSaveUseCase{
    private final UidbSavedFormRepo uidbSavedFormRepo;

    private final RedisTemplate<String,LinkedHashMap<String,Object>> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;
    private final S3ServiceClient s3ServiceClient;
    private final KafkaTemplate<String, AutoSaveRequestDto> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final ComplainantSavedFormRepo complainantSavedFormRepo;
    private final FirSavedFormRepo firSavedFormRepo;
    private final MissingPersonSavedFormRepo missingPersonSavedFormRepo;
    private final NcrSavedFormRepo ncrSavedFormRepo;
    private final UifpSavedFormRepo uifpSavedFormRepo;
    private final MlcSavedFormRepo mlcSavedFormRepo;
    private final ArrestSavedFormRepo arrestSavedFormRepo;

    @Value("${redis.ttl.minutes}")
    private Long timeToLive;

    @Value("${redis.buffer.time.minutes}")
   private Long bufferedTimeInSeconds;

    public AutoSaveServiceImpl(RedisTemplate<String,LinkedHashMap<String,Object>> redisTemplate, StringRedisTemplate stringRedisTemplate, S3ServiceClient s3ServiceClient,
                               KafkaTemplate<String, AutoSaveRequestDto> kafkaTemplate, ObjectMapper objectMapper, ComplainantSavedFormRepo complainantSavedFormRepo, FirSavedFormRepo firSavedFormRepo, MissingPersonSavedFormRepo missingPersonSavedFormRepo, NcrSavedFormRepo ncrSavedFormRepo, UifpSavedFormRepo uifpSavedFormRepo, MlcSavedFormRepo mlcSavedFormRepo,
                               UidbSavedFormRepo uidbSavedFormRepo, ArrestSavedFormRepo arrestSavedFormRepo) {
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.s3ServiceClient = s3ServiceClient;
        this.kafkaTemplate=kafkaTemplate;
        this.objectMapper = objectMapper;
        this.complainantSavedFormRepo = complainantSavedFormRepo;
        this.firSavedFormRepo = firSavedFormRepo;
        this.missingPersonSavedFormRepo = missingPersonSavedFormRepo;
        this.ncrSavedFormRepo = ncrSavedFormRepo;
        this.uifpSavedFormRepo = uifpSavedFormRepo;
        this.mlcSavedFormRepo = mlcSavedFormRepo;
        this.uidbSavedFormRepo = uidbSavedFormRepo;
        this.arrestSavedFormRepo = arrestSavedFormRepo;
    }

    /**
     * This is the service method to insert the data in the data base :
     * @param autoSaveData
     * @return
     */
    @Override
    @Transactional
    public Object submitAutoSaveData(AutoSaveDomain autoSaveData) throws JsonProcessingException {

        log.info("The TTL Configured Is : {}", timeToLive);
        log.info("The Buffered Time Configured Is : {}", bufferedTimeInSeconds);

        LinkedHashMap<String, Object> shadowObject = new LinkedHashMap<>();
        shadowObject.put("", "");


        //Json Data :
//        LinkedHashMap<String, Object> data = autoSaveData.getJsonData();
//        Object complainantNameObj = data.get("complainantDraftName");

        String complainantNameObj = autoSaveData.getComplainantDraftName();

        if (complainantNameObj instanceof String complainantName && !complainantName.trim().isEmpty()) {
            switch (autoSaveData.getModuleName()) {
                case Constants.COMPLAINANT -> {
                    complainantSavedFormRepo.updateComplainantNameByComplSavedNum(complainantName, Long.parseLong(autoSaveData.getSavedNum()));
                }

                case Constants.FIR -> {
                    firSavedFormRepo.updateComplainantNameByFirSavedNum(complainantName, Long.parseLong(autoSaveData.getSavedNum()));
                }

                case Constants.MISSING_PERSON -> {
                    missingPersonSavedFormRepo.updateComplainantNameByMpersSavedNum(complainantName, Long.parseLong(autoSaveData.getSavedNum()));
                }

                case Constants.NCR -> {
                    ncrSavedFormRepo.updateComplainantNameByNcrSavedNum(complainantName, Long.parseLong(autoSaveData.getSavedNum()));
                }

                case Constants.UIFP -> {
                    uifpSavedFormRepo.updateInformantNameBySavedNum(complainantName, Long.parseLong(autoSaveData.getSavedNum()));
                }

                case Constants.MLC -> {
                    mlcSavedFormRepo.updateInjuredNameAndMlcTypeAndMlcSubTypeByMlcSavedNum(complainantName, autoSaveData.getMlcType(), autoSaveData.getMlcSubType(), Long.parseLong(autoSaveData.getSavedNum()));
                }

                case Constants.UIDB -> {
                    uidbSavedFormRepo.updateInformantNameByUidbSavedNum(complainantName, Long.parseLong(autoSaveData.getSavedNum()));
                }

                case Constants.ARREST_MEMO -> {
                    arrestSavedFormRepo.updateAccusedNameByArrestSavedNum(complainantName,Long.parseLong(autoSaveData.getSavedNum()));
                }

                default ->{

                }
            }
        }


        if (Boolean.TRUE.equals(redisTemplate.hasKey(autoSaveData.getSavedNum()))) {
            log.info("The key is in redis : Hit On Redis Data :: Just Updating the TTL ");

            return redisTemplate.execute(new SessionCallback<List<Boolean>>() {
                @Override
                public List<Boolean> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForValue().set("ShadowKey:" + autoSaveData.getSavedNum(), shadowObject, Duration.ofSeconds(timeToLive));
                    operations.opsForValue().set(autoSaveData.getSavedNum(), autoSaveData.getJsonData(), Duration.ofSeconds(timeToLive + bufferedTimeInSeconds));
                    List<Boolean> transactionList = operations.exec();
                    return transactionList;
                }
            });
        } else {

            log.info("The Key Is Not In Redis :: New Entry Of The Key Is Registered");
            return redisTemplate.execute(new SessionCallback<List<Boolean>>() {
                @Override
                public List<Boolean> execute(RedisOperations operations) throws DataAccessException {
                    operations.multi();
                    operations.opsForValue().set("ShadowKey:" + autoSaveData.getSavedNum(), shadowObject, Duration.ofSeconds(timeToLive));
                    operations.opsForValue().set(autoSaveData.getSavedNum(), autoSaveData.getJsonData(), Duration.ofSeconds(timeToLive + bufferedTimeInSeconds));
                    List<Boolean> transactionList = operations.exec();
                    return transactionList;
                }
            });
        }
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
