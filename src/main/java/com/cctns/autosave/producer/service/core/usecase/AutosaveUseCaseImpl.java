package com.cctns.autosave.producer.service.core.usecase;

import com.cctns.autosave.producer.service.constants.Constants;
import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.domain.DraftNumberDomain;
import com.cctns.autosave.producer.service.core.domain.PageDomain;
import com.cctns.autosave.producer.service.core.exception.DraftIdNotFoundException;
import com.cctns.autosave.producer.service.core.exception.InvalidDraftNumberFormat;
import com.cctns.autosave.producer.service.core.exception.NoAutoSaveDataFoundException;
import com.cctns.autosave.producer.service.core.exception.SaveNumCannotBeNullException;
import com.cctns.autosave.producer.service.core.external.port.MicroserviceComms;
import com.cctns.autosave.producer.service.core.repository.AutosaveRepository;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveCreateResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDeleteResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDraftListResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveResponseDto;
import com.cctns.autosave.producer.service.web.dto.response.GetFormDataResponse;
import com.cctns.autosave.producer.service.web.dto.response.UpdateResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");

    public AutosaveUseCaseImpl(RedisTemplate<String,LinkedHashMap<String,Object>> redisJsonTemplate, RedisTemplate<String, String> redisZSetTemplate, MicroserviceComms microserviceComms, AutosaveRepository autosaveRepository, ObjectMapper objectMapper) {
        this.redisJsonTemplate = redisJsonTemplate;
        this.redisZSetTemplate = redisZSetTemplate;
        this.microserviceComms = microserviceComms;
        this.autosaveRepository = autosaveRepository;
        this.objectMapper = objectMapper;
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
     * Add the data hierarchy order pscd -> login -> module Name -> draft list
     * @param request
     * @return
     */
    @Override
    public AutosaveCreateResponse sentinelPersist(AutosaveDomain request){
        String psCd = request.getPsCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();

        String tag = "{" + psCd + "}";
        String draftId = UUID.randomUUID().toString();

        //Module-Specific Counter For Serial Number
        String seqKey = "AUTO-SAVE:SEQ:" + psCd + ":" + loginId + ":" + module + "_" + tag;
        Long srNo = redisZSetTemplate.opsForValue().increment(seqKey);

        //Hierarchy Navigation
        //Police station set
        redisZSetTemplate.opsForSet().add("AUTO-SAVE:POLICE-STATIONS", psCd);

        //Users Set
        redisZSetTemplate.opsForSet().add("AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS", loginId);

        //Module Set
        redisZSetTemplate.opsForSet().add("AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES", module);

        //Draft List Metadata : List of draft
        String listKey = "AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;

        LinkedHashMap<String, Object> gridMeta = new LinkedHashMap<>();
        gridMeta.put("draftNum",srNo+"/"+LocalDateTime.now().getYear());
        gridMeta.put("draftSrno", srNo);
        gridMeta.put("draftId", draftId);
        gridMeta.put("draftDateTime", LocalDateTime.now().format(FORMATTER));

        // Use your JSON template for the Metadata Map
        redisJsonTemplate.opsForHash().put(listKey, draftId, gridMeta);

        // 4. Actual Heavy Draft Data (String) - The "Big Payload"
        String dataKey = "AUTO-SAVE:DRAFT-DATA:" + draftId + "_" + tag;
        redisJsonTemplate.opsForValue().set(dataKey, request.getJsonData());

        AutosaveCreateResponse responseDto = new AutosaveCreateResponse();
        responseDto.setMessage("New Draft Created Successfully");
        responseDto.setDraftId(draftId);
        responseDto.setDraftSrno(srNo.toString());
        responseDto.setDraftDateTime(LocalDateTime.now());
        responseDto.setDraftNum(srNo+"/"+LocalDateTime.now().getYear());
        return responseDto;
    }

    private void validateDraftExists(String listKey, String draftId) {
        Boolean exists = redisJsonTemplate.opsForHash().hasKey(listKey, draftId);
        if (Boolean.FALSE.equals(exists)) {
            throw new DraftIdNotFoundException("Draft Id Not Found");
        }
    }

    /**
     * Persists the autosave data
     * @param request (AutosaveDomain)
     * @return AutosaveDomain
     */
    @Override
    public UpdateResponseDto persistAutosaveData(AutosaveDomain request) {

            String psCd = request.getPsCd().toString();
            String loginId = request.getLoginId();
            String module = request.getModuleName();
            String draftId = request.getDraftId();

            // 1. Reconstruct the keys (Tag is optional in Sentinel but kept for key consistency)
            String tag = "{" + psCd + "}";
            String listKey = "AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;
            String dataKey = "AUTO-SAVE:DRAFT-DATA:" + draftId + "_" + tag;

              validateDraftExists( listKey,  draftId);

            // 2. Update the Metadata Object in the Hash
            // We fetch the existing map first to preserve the original serial number and creation date
            LinkedHashMap<String, Object> gridMeta = (LinkedHashMap<String, Object>) redisJsonTemplate.opsForHash().get(listKey, draftId);

            if (gridMeta != null) {
                // Update with dynamic fields from the request
                gridMeta.put("complainantDraftName", request.getComplainantDraftName());
                gridMeta.put("mlcType", request.getMlcType());
                gridMeta.put("mlcSubType", request.getMlcSubType());
                gridMeta.put("lastUpdated", LocalDateTime.now().format(FORMATTER));

                // Put the updated map back into the Hash (overwrites the old one for this draftId)
                redisJsonTemplate.opsForHash().put(listKey, draftId, gridMeta);
            }

            //Update the Actual Heavy Draft Data (The JSON payload)
            // .set() will overwrite the existing value at this key
            redisJsonTemplate.opsForValue().set(dataKey, request.getJsonData());

            // 4. Prepare Response
            UpdateResponseDto responseDto = new UpdateResponseDto();
            responseDto.setMessage("Draft Updated Successfully");
            responseDto.setDraftId(draftId);
            responseDto.setDraftUpdateDateTime(LocalDateTime.now());
            return responseDto;
    }

    /**
     * @param request
     * @return
     */
    @Override
    public GetFormDataResponse fetchAutosaveData(AutosaveDomain request) {
        String psCd = request.getPsCd().toString();
        String draftId = request.getDraftId();
        String tag = "{" + psCd + "}";
        String dataKey = "AUTO-SAVE:DRAFT-DATA:" + draftId + "_" + tag;

        //Fetch the raw data from Redis
        Object rawData = redisJsonTemplate.opsForValue().get(dataKey);
        if(!redisJsonTemplate.hasKey(dataKey)){
            throw new NoAutoSaveDataFoundException("No Data Exists For Given Draft Id");
        }
        //Convert to LinkedHashMap
        LinkedHashMap<String, Object> structuredJsonData = null;
        if (rawData != null) {
            // This converts the Object (even if it's a JSON String) into a LinkedHashMap
            structuredJsonData = objectMapper.convertValue(rawData, new TypeReference<LinkedHashMap<String, Object>>() {
            });

        }

            //Prepare the standardized response
            GetFormDataResponse response = new GetFormDataResponse();
            response.setDraftId(draftId);
            response.setJsonData(structuredJsonData);
            return response;
    }

    private void formatField(Map<String, Object> map, String fieldName) {
        Object val = map.get(fieldName);
        if (val != null) {
            try {
                // LocalDateTime.parse is flexible; it handles the long nanoseconds
                // and the short .86 versions automatically.
                LocalDateTime parsedDate = (val instanceof LocalDateTime)
                        ? (LocalDateTime) val
                        : LocalDateTime.parse(val.toString());

                // Forces it into your yyyy-MM-dd'T'HH:mm:ss.SSS format
                map.put(fieldName, parsedDate.format(FORMATTER));
            } catch (Exception e) {
                log.warn("Could not format field {}: {}", fieldName, val);
            }
        }
    }

    @Override
    public PageDomain<List<LinkedHashMap<String, Object>>> fetchAutosaveDraftList(AutosaveDomain request) {

        //Extract pagination parameters with defaults
        int pageNo = (request.getPageable().getPage() != null && request.getPageable().getPage() > 0) ? request.getPageable().getPage() : 1;
        int pageSize = (request.getPageable().getPageSize() != null && request.getPageable().getPageSize() > 0) ? request.getPageable().getPageSize() : 10;

        String psCd = request.getPsCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();

        String listKey = "AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;

        //Fetch all entries from the Hash
        Map<Object, Object> allDraftsMap = redisJsonTemplate.opsForHash().entries(listKey);

        if (allDraftsMap.isEmpty()) {
            return PageDomain.<List<LinkedHashMap<String, Object>>>builder()
                    .list(Collections.emptyList())
                    .totalSize(0L)
                    .pageCount(0L)
                    .build();
        }

        //Map, Sort, and Slicing (Pagination)
        List<LinkedHashMap<String, Object>> sortedDraftList = allDraftsMap.values().stream()
                .map(obj -> {
                    // 1. Convert to Map
                    LinkedHashMap<String, Object> map = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {});

                    // 2. Format draftDateTime (Always present)
                    formatField(map, "draftDateTime");

                    // 3. Format lastUpdated (Optional - only formats if present)
                    if (map.containsKey("lastUpdated") && map.get("lastUpdated") != null) {
                        formatField(map, "lastUpdated");
                    }

                    return map;
                })
                .sorted((m1, m2) -> {
                    // Use standard ISO parse for sorting (it handles both long and short strings)
                    LocalDateTime d1 = LocalDateTime.parse(m1.get("draftDateTime").toString());
                    LocalDateTime d2 = LocalDateTime.parse(m2.get("draftDateTime").toString());
                    return d2.compareTo(d1);
                })
                .collect(Collectors.toList());

        //Calculate total size and total pages
        long totalSize = sortedDraftList.size();
        long pageCount = (int) Math.ceil((double) totalSize / pageSize);

        //Slice the list for the current page
        List<LinkedHashMap<String, Object>> paginatedList = sortedDraftList.stream()
                .skip((long) (pageNo - 1) * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());

        //Response
        return PageDomain.<List<LinkedHashMap<String, Object>>>builder()
                .list(paginatedList)
                .totalSize(totalSize)
                .pageCount(pageCount)
                .build();
    }

    @Override
    public AutosaveDeleteResponse deleteAutosaveDraftList(AutosaveDomain request) {
        String psCd = request.getPsCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();
        String draftId = request.getDraftId();

        //Reconstruct the Keys
        String tag = "{" + psCd + "}";
        String listKey = "AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;
        String dataKey = "AUTO-SAVE:DRAFT-DATA:" + draftId + "_" + tag;

        validateDraftExists( listKey,  draftId);

        //Remove the specific draft from the Module's Hash List
        Long hashRemoved = redisJsonTemplate.opsForHash().delete(listKey, draftId);
        //Remove the actual heavy JSON payload string
        Boolean dataRemoved = redisJsonTemplate.delete(dataKey);
        //Prepare Standardized Response
        AutosaveDeleteResponse response = new AutosaveDeleteResponse();
        if (hashRemoved > 0 || (dataRemoved != null && dataRemoved)) {
            response.setMessage("Draft deleted successfully");
            response.setDraftId(draftId);
        } else {
            response.setMessage("Draft not found or already deleted");
            response.setDraftId(draftId);
        }
        return response;
    }
}
