package com.cctns.autosave.producer.service.core.usecase;

import com.cctns.autosave.producer.service.constants.Constants;
import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.domain.DraftNumberDomain;
import com.cctns.autosave.producer.service.core.exception.InvalidDraftNumberFormat;
import com.cctns.autosave.producer.service.core.exception.NoAutoSaveDataFoundException;
import com.cctns.autosave.producer.service.core.exception.SaveNumCannotBeNullException;
import com.cctns.autosave.producer.service.core.external.port.MicroserviceComms;
import com.cctns.autosave.producer.service.core.repository.AutosaveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class AutosaveUseCaseImpl implements AutosaveUseCase{

    @Value("${autosave.fail-safe.ttl}")
    private Long failSafeTtl;

    @Value("${autosave.z-set.ttl}")
    private Long zSetTtl;

    @Qualifier("redisJsonTemplate")
    private final RedisTemplate<String,LinkedHashMap<String,Object>> redisJsonTemplate;

    @Qualifier("redisZSetTemplate")
    private final RedisTemplate<String,String> redisZSetTemplate;

    private final MicroserviceComms microserviceComms;
    private final AutosaveRepository autosaveRepository;

    public AutosaveUseCaseImpl(RedisTemplate<String,LinkedHashMap<String,Object>> redisJsonTemplate, RedisTemplate<String, String> redisZSetTemplate, MicroserviceComms microserviceComms, AutosaveRepository autosaveRepository) {
        this.redisJsonTemplate = redisJsonTemplate;
        this.redisZSetTemplate = redisZSetTemplate;
        this.microserviceComms = microserviceComms;
        this.autosaveRepository = autosaveRepository;
    }

    /**
     * Validates the draft Number format : moduleName_SaveNumber_{shardTag}
     * @param draftNum (Draft Number)
     */
    private void validateDraftNumber(String draftNum) {
        if (draftNum == null || draftNum.isBlank() || !draftNum.matches(Constants.VALIDATION_EXPRESSION)) {
            throw new InvalidDraftNumberFormat("Draft Number Is Not Valid");
        }
    }

    /**
     * Generates The Draft Number For The Autosave Service
     * @param moduleName (Name of the module name)
     * @param saveNum (Save Number)
     * @return String value of the generated Draft Number
     */
    private String generateDraftNumber(String moduleName, Long saveNum, Integer shardTag) {
        if (moduleName == null || moduleName.isBlank()) {
            throw new IllegalArgumentException("Module name cannot be null or blank");
        }
        if (saveNum == null || saveNum < 0) {
            throw new IllegalArgumentException("Save number must be non-negative");
        }
        if (shardTag == null || shardTag < 0 || shardTag >= Constants.SHARD_COUNT) {
            throw new IllegalArgumentException("Invalid shard tag: " + shardTag);
        }
        String draftNumber = String.join(
                Constants.DRAFT_NUMBER_DELIMITER,
                moduleName,
                saveNum.toString(),
                Constants.SHARD_TAG_START + shardTag + Constants.SHARD_TAG_END
        );
        validateDraftNumber(draftNumber);
        return draftNumber;
    }

    /**
     * Generates the key name for the Z-SET (Redis Data-structure )
     * @return String (Z-Set key name)
     */
    private String generateZSetNumber(Integer shardTag) {
        return "AUTOSAVE:EXPIRY:" + Constants.SHARD_TAG_START + shardTag.toString() + Constants.SHARD_TAG_END;
    }


    /**
     * Breaks the draft number
     * @param draftNumber (Draft Number)
     * @return DraftNumberDomain Value object
     */
    private DraftNumberDomain breakDraftNumber(String draftNumber) {
        validateDraftNumber(draftNumber);

        Pattern pattern = Pattern.compile("([A-Z]+)_(\\d+)_\\{(\\d+)}");
        Matcher matcher = pattern.matcher(draftNumber);

        if (!matcher.matches()) {
            throw new InvalidDraftNumberFormat("Invalid draft number: " + draftNumber);
        }

        String module = matcher.group(1);
        Long saveNum = Long.parseLong(matcher.group(2));
        Integer shard = Integer.parseInt(matcher.group(3));

        return new DraftNumberDomain(module, saveNum, shard);
    }

    /**
     * Processes the shard tag for a particular SaveNum
     * @param saveNum (Save Number)
     * @return Integer value of the shard
     */
    private Integer getShard(Long saveNum) {
        if (saveNum == null) {
            throw new SaveNumCannotBeNullException("SaveNum Is Null");
        }
        return (int) (saveNum % Constants.SHARD_COUNT);
    }

    /**
     * Persists the autosave data
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    @Override
    public AutosaveDomain persistAutosaveData(AutosaveDomain request) {

        String complainantName = request.getComplainantDraftName();

        if (complainantName != null && !complainantName.trim().isEmpty()) {

            switch (request.getModuleName()) {
                case Constants.COMPLAINANT -> autosaveRepository.updateComplaintSaveForm(complainantName, request.getSavedNum());

                case Constants.FIR -> autosaveRepository.updateFirSaveForm(complainantName, request.getSavedNum());

                case Constants.MISSING_PERSON -> autosaveRepository.updateMissingPersonSaveForm(complainantName, request.getSavedNum());

                case Constants.NCR -> autosaveRepository.updateNcrSaveForm(complainantName, request.getSavedNum());

                case Constants.UIFP -> autosaveRepository.updateUifpSaveForm(complainantName, request.getSavedNum());

                case Constants.MLC -> autosaveRepository.updateMlcSaveForm(complainantName, request.getMlcType(), request.getMlcSubType(), request.getSavedNum());

                case Constants.UIDB -> autosaveRepository.updateUidbSaveForm(complainantName, request.getSavedNum());

                case Constants.ARREST_MEMO -> autosaveRepository.updateArrestSaveForm(complainantName, request.getSavedNum());

                default -> log.info("The Module : {} Complainant Is Not Getting Updated ", request.getModuleName());
            }
        }

        Integer shardNumber = getShard(request.getSavedNum());
        String draftNumber = generateDraftNumber(request.getModuleName(), request.getSavedNum(), shardNumber);
        String zsetKeyName = generateZSetNumber(shardNumber);
        Long expireScore = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(zSetTtl);

        redisJsonTemplate.opsForValue().set(draftNumber, request.getJsonData(), Duration.ofSeconds(failSafeTtl));
        redisZSetTemplate.opsForZSet().add(zsetKeyName, draftNumber, expireScore);
        log.info("Persisted The JSON in Redis Cache For Save Number : {}", request.getSavedNum());

        AutosaveDomain response = new AutosaveDomain();
        response.setDraftNumber(draftNumber);
        response.setOpTime(LocalDateTime.now());
        response.setModuleName(request.getModuleName());
        response.setSavedNum(request.getSavedNum());
        return response;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public AutosaveDomain fetchAutosaveData(AutosaveDomain request) {
        log.info("HIT ON THE fetchAutosaveData() Method");

        Integer shardNumber = getShard(request.getSavedNum());
        String draftNumber = generateDraftNumber(request.getModuleName(), request.getSavedNum(), shardNumber);
        String zsetKeyName = generateZSetNumber(shardNumber);

        LinkedHashMap<String, Object> jsonData = redisJsonTemplate.opsForValue().get(draftNumber);


        if (jsonData != null) {
            //If data exists in the Redis cluster : Update the score and Json data
            //Update the redis key expiry : Since it is required
            log.info("The JSON Data For The Saved Num : {} Already Exists In Redis ", request.getSavedNum());
            long expireScore = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(zSetTtl);

            redisJsonTemplate.opsForValue().set(draftNumber, jsonData, Duration.ofSeconds(failSafeTtl));
            redisZSetTemplate.opsForZSet().add(zsetKeyName, draftNumber, expireScore);

            AutosaveDomain response = new AutosaveDomain();
            response.setJsonData(jsonData);
            response.setModuleName(request.getModuleName());
            response.setSavedNum(request.getSavedNum());
            return response;
        } else {
            //If data is not in the Redis : Fetch Data from AWS S3 and then populate the redis as well as give it to front end :
            log.info("The JSON Data For The Saved Num : {} Does Not Exists In Redis ", request.getSavedNum());
            request.setDraftNumber(draftNumber);
            AutosaveDomain response = microserviceComms.fetchJsonDataFromS3(request);
            if (response != null && !response.getJsonData().isEmpty()) {

                Long expireScore = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(zSetTtl);
                redisJsonTemplate.opsForValue().set(draftNumber, response.getJsonData(), Duration.ofSeconds(failSafeTtl));
                redisZSetTemplate.opsForZSet().add(zsetKeyName, draftNumber, expireScore);
                return response;
            } else {
                throw new NoAutoSaveDataFoundException("No Autosave Data Found For The Entry : " + request.getSavedNum().toString());
            }
        }
    }
}
