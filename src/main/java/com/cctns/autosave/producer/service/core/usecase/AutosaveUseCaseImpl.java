package com.cctns.autosave.producer.service.core.usecase;

import com.cctns.autosave.producer.service.constants.AutosaveGridField;
import com.cctns.autosave.producer.service.constants.AutosaveRedisKeySchema;
import com.cctns.autosave.producer.service.constants.Constants;
import com.cctns.autosave.producer.service.constants.GridMetaBuilder;
import com.cctns.autosave.producer.service.constants.Module;
import com.cctns.autosave.producer.service.constants.RedisKey;
import com.cctns.autosave.producer.service.core.domain.AutosaveDomain;
import com.cctns.autosave.producer.service.core.domain.PageDomain;
import com.cctns.autosave.producer.service.core.exception.DraftIdNotFoundException;
import com.cctns.autosave.producer.service.core.exception.InvalidDraftRequestException;
import com.cctns.autosave.producer.service.core.exception.NoAutoSaveDataFoundException;
import com.cctns.autosave.producer.service.core.external.port.MicroserviceComms;
import com.cctns.autosave.producer.service.core.repository.AutosaveRepository;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveCreateResponse;
import com.cctns.autosave.producer.service.web.dto.response.AutosaveDeleteResponse;
import com.cctns.autosave.producer.service.web.dto.response.GetFormDataResponse;
import com.cctns.autosave.producer.service.web.dto.response.UpdateResponseDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class AutosaveUseCaseImpl implements AutosaveUseCase{

    @Value("${autosave-env-name}")
    private String envName;

    @Qualifier("redisJsonTemplate")
    private final RedisTemplate<String,LinkedHashMap<String,Object>> redisJsonTemplate;

    @Qualifier("redisZSetTemplate")
    private final RedisTemplate<String,String> redisZSetTemplate;

    private final MicroserviceComms microserviceComms;
    private final AutosaveRepository autosaveRepository;
    private final ObjectMapper objectMapper;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS");
    private final AutosaveRedisKeySchema autosaveRedisKeySchema;

    public AutosaveUseCaseImpl(RedisTemplate<String,LinkedHashMap<String,Object>> redisJsonTemplate, RedisTemplate<String, String> redisZSetTemplate, MicroserviceComms microserviceComms, AutosaveRepository autosaveRepository, ObjectMapper objectMapper, AutosaveRedisKeySchema autosaveRedisKeySchema) {
        this.redisJsonTemplate = redisJsonTemplate;
        this.redisZSetTemplate = redisZSetTemplate;
        this.microserviceComms = microserviceComms;
        this.autosaveRepository = autosaveRepository;
        this.objectMapper = objectMapper;
        this.autosaveRedisKeySchema = autosaveRedisKeySchema;
    }

    /**
     * {@inheritDoc}
     * @param request
     * @return
     */
    @Override
    public AutosaveCreateResponse createDraft(AutosaveDomain request) {

        Module module = Module.fromModuleName(request.getModuleName());

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String moduleName = request.getModuleName();
        String draftId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();

        // Generate Redis sequence key
        RedisKey seqKey = autosaveRedisKeySchema.sequenceKey(psCd, loginId, moduleName);
        Long srNo = redisZSetTemplate.opsForValue().increment(seqKey.value());

        // Generating redis hierarchy
        redisZSetTemplate.opsForSet().add(autosaveRedisKeySchema.policeStationsSetKey().value(), psCd);
        redisZSetTemplate.opsForSet().add(autosaveRedisKeySchema.loginIdsSetKey(psCd).value(), loginId);
        redisZSetTemplate.opsForSet().add(autosaveRedisKeySchema.modulesSetKey(psCd, loginId).value(), moduleName);

        RedisKey listKey = autosaveRedisKeySchema.draftListKey(psCd, loginId, moduleName);

        String draftNum = srNo + "/" + now.getYear();

        LinkedHashMap<String, Object> gridMeta = GridMetaBuilder.forCreate(module)
                //Common draft fields
                .set(AutosaveGridField.DRAFT_NUM, draftNum)
                .set(AutosaveGridField.DRAFT_SRNO, srNo)
                .set(AutosaveGridField.DRAFT_ID, draftId)
                .set(AutosaveGridField.DRAFT_DATE_TIME, now.format(FORMATTER))
                //Module Specific draft fields
                .set(AutosaveGridField.FIR_REG_NUM, request.getFirRegNum())
                .set(AutosaveGridField.ACCUSED_VID, request.getAccusedVid())
                .set(AutosaveGridField.ACCUSED_NAME, request.getAccusedName())
                .set(AutosaveGridField.ARR_SURR_SR_NO, request.getArrSurrSrNo())
                .build();

        redisJsonTemplate.opsForHash().put(listKey.value(), draftId, gridMeta);

        //Store Json Data with the redis data key with the json wrapper
        RedisKey dataKey = autosaveRedisKeySchema.draftDataKey(draftId, psCd);
        LinkedHashMap<String, Object> dataWrapper = new LinkedHashMap<>();
        dataWrapper.put(AutosaveGridField.DRAFT_NUM.getKey(), draftNum);
        dataWrapper.put(AutosaveGridField.JSON_DATA.getKey(), request.getJsonData());
        redisJsonTemplate.opsForValue().set(dataKey.value(), dataWrapper);

        AutosaveCreateResponse responseDto = new AutosaveCreateResponse();
        responseDto.setMessage("New Draft Created Successfully");
        responseDto.setDraftId(draftId);
        responseDto.setDraftSrno(srNo.toString());
        responseDto.setDraftDateTime(now);
        responseDto.setDraftNum(draftNum);
        return responseDto;
    }

    /**
     * Checks if the draft key already exists or not.
     * @param listKey Redis list key
     * @param draftId Redis draft Id
     */
    private void validateDraftExists(RedisKey listKey, String draftId) {
        Boolean exists = redisJsonTemplate.opsForHash().hasKey(listKey.value(), draftId);
        if (Boolean.FALSE.equals(exists)) {
            throw new DraftIdNotFoundException("Draft Id Not Found");
        }
    }


    /**
     * Fetches gridMeta for a draft, centralizing the unchecked cast in one
     * place. If Redis ever returns something that isn't a LinkedHashMap
     * (corrupted data, a schema change elsewhere), this throws a clear,
     * specific exception here — instead of a raw ClassCastException
     * surfacing three statements later from whatever line happens to use it.
     */
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, Object> fetchGridMeta(RedisKey listKey, String draftId) {
        Object raw = redisJsonTemplate.opsForHash().get(listKey.value(), draftId);
        if (raw == null) {
            return null;
        }
        if (!(raw instanceof LinkedHashMap)) {
            throw new IllegalStateException(
                    "Unexpected gridMeta type in Redis for draftId=" + draftId + ": " + raw.getClass());
        }
        return (LinkedHashMap<String, Object>) raw;
    }

    /**
     * Persists the autosave data.
     * @param request (AutosaveDomain)
     * @return UpdateResponseDto
     */
    @Override
    public UpdateResponseDto persistAutosaveData(AutosaveDomain request) {

        Module module = Module.fromModuleName(request.getModuleName());

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String moduleName = request.getModuleName();
        String draftId = request.getDraftId();


        // Keys built through the same schema createDraft uses — structurally
        // impossible for the two flows to disagree on key shape now.
        RedisKey listKey = autosaveRedisKeySchema.draftListKey(psCd, loginId, moduleName);
        RedisKey dataKey = autosaveRedisKeySchema.draftDataKey(draftId, psCd);

        validateDraftExists(listKey, draftId);

        // 1. Fetch existing metadata — cast is centralized and guarded, not inline
        LinkedHashMap<String, Object> gridMeta = fetchGridMeta(listKey, draftId);

        if (gridMeta != null) {

            // 2. Apply ONLY this module's updatable fields onto the existing
            // map. A field not in module.getUpdatableFields() (e.g. mlcType
            // on a non-MLC draft) throws immediately here instead of silently
            // writing into a record it doesn't belong to. Create-only fields
            // (draftId, draftNum, draftSrno) are never touched, since
            // updatableFields never contains them.
            GridMetaBuilder.forUpdate(module)
                    .set(AutosaveGridField.COMPLAINANT_NAME, request.getComplainantDraftName())
                    .set(AutosaveGridField.MLC_TYPE, request.getMlcType())
                    .set(AutosaveGridField.MLC_SUB_TYPE, request.getMlcSubType())
                    .set(AutosaveGridField.ACCUSED_NAME, request.getAccusedName())
                    .set(AutosaveGridField.MAIL_TO, request.getToEmails())
                    .set(AutosaveGridField.MESSAGE_SUBJECT, request.getMessageSubject())
                    .set(AutosaveGridField.LAST_UPDATED, LocalDateTime.now().format(FORMATTER))
                    .applyTo(gridMeta);

            redisJsonTemplate.opsForHash().put(listKey.value(), draftId, gridMeta);

            // 3. Re-wrap: preserve original draftNum, update jsonData —
            // both keys read via the enum, never a raw string literal
            LinkedHashMap<String, Object> dataWrapper = new LinkedHashMap<>();
            dataWrapper.put(AutosaveGridField.DRAFT_NUM.getKey(), gridMeta.get(AutosaveGridField.DRAFT_NUM.getKey()));
            dataWrapper.put(AutosaveGridField.JSON_DATA.getKey(), request.getJsonData());

            // 4. Save the wrapper back to Redis
            redisJsonTemplate.opsForValue().set(dataKey.value(), dataWrapper);
        }

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
        String psCd = request.getOfficeCd().toString();
        String draftId = request.getDraftId();
        String tag = "{" + psCd + "}";
        String dataKey = envName+":AUTO-SAVE:DRAFT-DATA:" + draftId + "_" + tag;

        // 1. Fetch the Wrapper Object
        Object rawWrapper = redisJsonTemplate.opsForValue().get(dataKey);

        if (rawWrapper == null) {
            throw new NoAutoSaveDataFoundException("No Data Exists For Given Draft Id");
        }

        // 2. Convert raw data to a Map to access the internal fields
        Map<String, Object> wrapperMap = objectMapper.convertValue(rawWrapper,
                new TypeReference<Map<String, Object>>() {});

        // 3. Extract parts
        String draftNum = (String) wrapperMap.get("draftNum");
        Object rawJsonData = wrapperMap.get("jsonData");

        // 4. Convert internal jsonData to LinkedHashMap
        LinkedHashMap<String, Object> structuredJsonData = objectMapper.convertValue(rawJsonData,
                new TypeReference<LinkedHashMap<String, Object>>() {});

        GetFormDataResponse response = new GetFormDataResponse();
        response.setDraftId(draftId);
        response.setDraftNum(draftNum); // Now returning the number stored in the wrapper
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

    private PageDomain<List<LinkedHashMap<String, Object>>> fetchDrafListForFinalForm(AutosaveDomain request){
        // For Final Form :
        if(request.getFirRegNum()==null){
            throw new InvalidDraftRequestException("For Final Form Autosave FIR Reg Num Is Mandatory");
        }
        //Extract pagination parameters with defaults
        // Treat page 0 as the first page. If null, default to 0.
        int pageNo = (request.getPageable().getPage() != null) ? request.getPageable().getPage() : 0;
        // Ensure we don't go below 0
        pageNo = Math.max(0, pageNo);

        int pageSize = (request.getPageable().getPageSize() != null && request.getPageable().getPageSize() > 0) ? request.getPageable().getPageSize() : 10;

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();

        //Added for final form :
        Long firRegNum = request.getFirRegNum();

        String listKey = envName+":AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;

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
        List<LinkedHashMap<String, Object>> sortedDraftList = allDraftsMap.values().stream().filter(obj -> {
                    // Extract a temporary map to check the condition
                    LinkedHashMap<String, Object> tempMap = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {});

                    Object regNum = tempMap.get("firRegNum");
                    if(regNum==null) {
                        return false;
                    }

                    Long firNum = Long.parseLong(regNum.toString());
                    return firRegNum.equals(firNum);
                })
                .map(obj -> {
                    // 1. Convert to Map
                    LinkedHashMap<String, Object> map = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {
                            });

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

        long skipCount = (long) pageNo * pageSize;

        //Slice the list for the current page
        List<LinkedHashMap<String, Object>> paginatedList = sortedDraftList.stream()
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());

        //Response
        return PageDomain.<List<LinkedHashMap<String, Object>>>builder()
                .list(paginatedList)
                .totalSize(totalSize)
                .pageCount(pageCount)
                .build();
    }

    /**
     * Fetches normal draft list
     * @param request
     * @return
     */
    private PageDomain<List<LinkedHashMap<String, Object>>> fetchNormalDraft(AutosaveDomain request){
        //Extract pagination parameters with defaults
        // Treat page 0 as the first page. If null, default to 0.
        int pageNo = (request.getPageable().getPage() != null) ? request.getPageable().getPage() : 0;
        // Ensure we don't go below 0
        pageNo = Math.max(0, pageNo);

        int pageSize = (request.getPageable().getPageSize() != null && request.getPageable().getPageSize() > 0) ? request.getPageable().getPageSize() : 10;

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();

        String listKey = envName+":AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;

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
                            new TypeReference<LinkedHashMap<String, Object>>() {
                            });

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

        //Skip for 1 based pagination :
        // long skipCount = (long) (pageNo - 1) * pageSize;

        //Skip for 0 based pagination :
        long skipCount = (long) pageNo * pageSize;

        //Slice the list for the current page
        List<LinkedHashMap<String, Object>> paginatedList = sortedDraftList.stream()
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());

        //Response
        return PageDomain.<List<LinkedHashMap<String, Object>>>builder()
                .list(paginatedList)
                .totalSize(totalSize)
                .pageCount(pageCount)
                .build();
    }


    private PageDomain<List<LinkedHashMap<String, Object>>> fetchDraftListForArrestWarrant(AutosaveDomain request){
        // For Final Form :
        if(request.getAccusedVid()==null){
            throw new InvalidDraftRequestException("For Arrest Warrant Autosave Accused Vid Num Is Mandatory");
        }
        //Extract pagination parameters with defaults
        // Treat page 0 as the first page. If null, default to 0.
        int pageNo = (request.getPageable().getPage() != null) ? request.getPageable().getPage() : 0;
        // Ensure we don't go below 0
        pageNo = Math.max(0, pageNo);

        int pageSize = (request.getPageable().getPageSize() != null && request.getPageable().getPageSize() > 0) ? request.getPageable().getPageSize() : 10;

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();

        //Added for Arrest Warrant form :
        Long accusedVid = request.getAccusedVid();

        String listKey = envName+":AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;

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
        List<LinkedHashMap<String, Object>> sortedDraftList = allDraftsMap.values().stream().filter(obj -> {
                    // Extract a temporary map to check the condition
                    LinkedHashMap<String, Object> tempMap = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {});

                    Object regNum = tempMap.get("accusedVid");
                    if(regNum==null) {
                        return false;
                    }

                    Long accVid = Long.parseLong(regNum.toString());
                    return accusedVid.equals(accVid);
                })
                .map(obj -> {
                    // 1. Convert to Map
                    LinkedHashMap<String, Object> map = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {
                            });

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

        long skipCount = (long) pageNo * pageSize;

        //Slice the list for the current page
        List<LinkedHashMap<String, Object>> paginatedList = sortedDraftList.stream()
                .skip(skipCount)
                .limit(pageSize)
                .collect(Collectors.toList());

        //Response
        return PageDomain.<List<LinkedHashMap<String, Object>>>builder()
                .list(paginatedList)
                .totalSize(totalSize)
                .pageCount(pageCount)
                .build();
    }


    private PageDomain<List<LinkedHashMap<String, Object>>> fetchDraftListForBailCancellation(AutosaveDomain request){
        // For Final Form :
        if(request.getArrSurrSrNo()==null){
            throw new InvalidDraftRequestException("For Bail Cancellation Autosave Arrest Num Is Mandatory");
        }
        //Extract pagination parameters with defaults
        // Treat page 0 as the first page. If null, default to 0.
        int pageNo = (request.getPageable().getPage() != null) ? request.getPageable().getPage() : 0;
        // Ensure we don't go below 0
        pageNo = Math.max(0, pageNo);

        int pageSize = (request.getPageable().getPageSize() != null && request.getPageable().getPageSize() > 0) ? request.getPageable().getPageSize() : 10;

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();

        //Added for final form :
        Long arrSurrSrNo = request.getArrSurrSrNo();

        String listKey = envName+":AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;

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
        List<LinkedHashMap<String, Object>> sortedDraftList = allDraftsMap.values().stream().filter(obj -> {
                    // Extract a temporary map to check the condition
                    LinkedHashMap<String, Object> tempMap = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {});

                    Object regNum = tempMap.get("arrSurrSrNo");
                    if(regNum==null) {
                        return false;
                    }

                    Long arrNum = Long.parseLong(regNum.toString());
                    return arrSurrSrNo.equals(arrNum);
                })
                .map(obj -> {
                    // 1. Convert to Map
                    LinkedHashMap<String, Object> map = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {
                            });

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

        long skipCount = (long) pageNo * pageSize;

        //Slice the list for the current page
        List<LinkedHashMap<String, Object>> paginatedList = sortedDraftList.stream()
                .skip(skipCount)
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
    public PageDomain<List<LinkedHashMap<String, Object>>> fetchAutosaveDraftList(AutosaveDomain request) {

        return switch (request.getModuleName()) {
            case Constants.FINAL_FORM_MODULE_NAME_VALIDATION -> fetchDrafListForFinalForm(request);
            case Constants.ARREST_WARRANT_MODULE -> fetchDraftListForArrestWarrant(request);
            case Constants.BAIL_CANCEL_MODULE -> fetchDraftListForBailCancellation(request);
            case null, default -> fetchNormalDraft(request);
        };
    }

    private List<LinkedHashMap<String, Object>> fetchNormalDraftWithoutPagination(AutosaveDomain request) {

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String module = request.getModuleName();

        String listKey = envName+":AUTO-SAVE:POLICE-STATIONS:" + psCd + ":LOGIN-IDS:" + loginId + ":MODULES:" + module;

        //Fetch all entries from the Hash
        Map<Object, Object> allDraftsMap = redisJsonTemplate.opsForHash().entries(listKey);

//        if (!allDraftsMap.isEmpty() && request.getModuleName().equals(Constants.FIR)) {
//            allDraftsMap.put("status", "Draft");
//        }

        //Map, Sort, and Slicing (Pagination)
        return allDraftsMap.values().stream()
                .map(obj -> {
                    // Convert to Map
                    LinkedHashMap<String, Object> map = objectMapper.convertValue(obj,
                            new TypeReference<LinkedHashMap<String, Object>>() {
                            });

                    if (Constants.FIR.equals(request.getModuleName())) {
                        map.put("status", "Draft");
                        map.put("statusCd", 1);
                    }

                    // Format draftDateTime (Always present)
                    formatField(map, "draftDateTime");

                    // Format lastUpdated (Optional - only formats if present)
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
    }


    @Override
    public List<LinkedHashMap<String, Object>> fetchAutosaveDraftListWithoutPagination(AutosaveDomain request) {
        return fetchNormalDraftWithoutPagination(request);
    }

    /**
     * Builds the delete response, distinguishing three outcomes instead
     * of collapsing them into a binary success/failure:
     *   FULL    — both the list entry AND the data payload were removed
     *   PARTIAL — only one of the two was removed (the other was already
     *             gone, or something went wrong) — logged as a warning,
     *             since an orphaned dataKey or a dangling list entry both
     *             indicate state that should be looked at, even though the
     *             caller's immediate request technically "did something"
     *   NONE    — neither existed; nothing to delete
     */
    private AutosaveDeleteResponse buildDeleteResponse(String draftId, Long hashRemoved, Boolean dataRemoved) {
        boolean hashDeleted = hashRemoved != null && hashRemoved > 0;
        boolean dataDeleted = dataRemoved != null && dataRemoved;

        AutosaveDeleteResponse response = new AutosaveDeleteResponse();
        response.setDraftId(draftId);

        if (hashDeleted && dataDeleted) {
            response.setMessage("Draft deleted successfully");
        } else if (hashDeleted != dataDeleted) {
            // exactly one succeeded — this is the case the original code
            // silently swallowed
            log.warn("Partial draft deletion for draftId={}: listEntryRemoved={}, dataPayloadRemoved={}",
                    draftId, hashDeleted, dataDeleted);
            response.setMessage("Draft partially deleted — please contact support if this recurs");
        } else {
            response.setMessage("Draft not found or already deleted");
        }
        return response;
    }

    @Override
    public AutosaveDeleteResponse deleteAutosaveDraftList(AutosaveDomain request) {

        String psCd = request.getOfficeCd().toString();
        String loginId = request.getLoginId();
        String moduleName = request.getModuleName();
        String draftId = request.getDraftId();

        // Same schema every other method uses — this delete can never
        // target a different key than what create/persist/fetch wrote to.
        RedisKey listKey = autosaveRedisKeySchema.draftListKey(psCd, loginId, moduleName);
        RedisKey dataKey = autosaveRedisKeySchema.draftDataKey(draftId, psCd);

        validateDraftExists(listKey, draftId);

        Long hashRemoved = redisJsonTemplate.opsForHash().delete(listKey.value(), draftId);
        Boolean dataRemoved = redisJsonTemplate.delete(dataKey.value());

        return buildDeleteResponse(draftId, hashRemoved, dataRemoved);
    }
}
