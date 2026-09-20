package com.cctns.autosave.producer.service.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AutosaveRedisKeySchema {

    private static final String ROOT = "AUTO-SAVE";
    private static final String POLICE_STATIONS = "POLICE-STATIONS";
    private static final String LOGIN_IDS = "LOGIN-IDS";
    private static final String MODULES = "MODULES";
    private static final String SEQ = "SEQ";
    private static final String DRAFT_DATA = "DRAFT-DATA";
    private static final String SEP = ":";

    private final String envName;

    public AutosaveRedisKeySchema(@Value("${autosave-env-name}") String envName) {
        this.envName = envName;
    }

    /**
     * Redis Cluster hashtag : keys sharing the same psCd land on the
     * same cluster slot, so they can be operated together in a
     * pipeline/transaction
     */
    private String hashTag(String psCd) {
        return "{" + psCd + "}";
    }

    /**
     * Creates the redis sequence key :
     * Key Format : envName:AUTO-SAVE:SEQ:psCd:loginId:module_{psCd}
     *
     * @param psCd    Police station code
     * @param loginId Login id
     * @param module  Module Name
     * @return String value of redis key
     */
    public RedisKey sequenceKey(String psCd, String loginId, String module) {
        requireSafeSegment(psCd, "psCd");
        requireSafeSegment(loginId, "loginId");
        requireSafeSegment(module, "module");
        String base = String.join(SEP, envName, ROOT, SEQ, psCd, loginId, module);
        return new RedisKey(base + "_" + hashTag(psCd));
    }

    /**
     * Creates the redis key for Police-station
     * Police Key Format (policeStationsSetKey): envName:AUTO-SAVE:POLICE-STATIONS
     * Absolute key : envName:AUTO-SAVE:POLICE-STATIONS
     *
     * @return String value of redis police station key
     */
    public RedisKey policeStationsSetKey() {
        return new RedisKey(String.join(SEP, envName, ROOT, POLICE_STATIONS));
    }

    /**
     * Creates the redis key for the user on basis of loginId
     * User Key Format : [policeStationsSetKey]:psCd:LOGIN-IDS
     * Absolute Key : envName:AUTO-SAVE:POLICE-STATIONS:psCd:LOGIN-IDS
     *
     * @param psCd Police station code
     * @return String value of redis user key
     */
    public RedisKey loginIdsSetKey(String psCd) {
        requireSafeSegment(psCd, "psCd");
        return new RedisKey(String.join(SEP, policeStationsSetKey().value(), psCd, LOGIN_IDS));
    }

    /**
     * Creates the redis key for the module on basis of module name
     * Module Key Format :  [loginIdsSetKey]:{loginId}:MODULES
     * Absolute Key : envName:AUTO-SAVE:POLICE-STATIONS:psCd:LOGIN-IDS:loginId:MODULES
     *
     * @param psCd Police station code
     * @param loginId Login Id
     * @return String value of redis module key
     */
    public RedisKey modulesSetKey(String psCd, String loginId) {
        requireSafeSegment(loginId, "loginId");
        return new RedisKey(String.join(SEP, loginIdsSetKey(psCd).value(), loginId, MODULES));
    }

    /**
     * Creates draft list key for a given module
     * Draft list key format: [modulesSetKey]:{module}
     * Absolute Key : envName:AUTO-SAVE:POLICE-STATIONS:psCd:LOGIN-IDS:loginId:MODULES:module
     *
     * @param psCd    Police station code
     * @param loginId Login id
     * @param module  Module name
     * @return String value of draft list key
     */
    public RedisKey draftListKey(String psCd, String loginId, String module) {
        requireSafeSegment(module, "module");
        return new RedisKey(String.join(SEP, modulesSetKey(psCd, loginId).value(), module));
    }

    /**
     * Creates draft data key for the JSON draft data
     * Draft Data Key : envName:AUTO-SAVE:DRAFT-DATA:{draftId}_{psCd}
     * Absolute Key : envName:AUTO-SAVE:DRAFT-DATA:{draftId}_{psCd}
     *
     * @param draftId Draft Id
     * @param psCd Police station code
     * @return String value of draft data key
     */
    public RedisKey draftDataKey(String draftId, String psCd) {
        requireSafeSegment(draftId, "draftId");
        requireSafeSegment(psCd, "psCd");
        String base = String.join(SEP, envName, ROOT, DRAFT_DATA, draftId);
        return new RedisKey(base + "_" + hashTag(psCd));
    }

    /**
     * Guards the actual corruption risk you described: if psCd/loginId/
     * module ever contains ":" or "{"/"}", plain concatenation would
     * silently produce a DIFFERENT key shape — extra segments, or a
     * hash tag landing somewhere unintended. Fail loudly instead of
     * building a subtly wrong key.
     */
    private void requireSafeSegment(String segment, String fieldName) {
        if (segment == null || segment.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be blank for Redis key construction");
        }
        if (segment.contains(SEP) || segment.contains("{") || segment.contains("}")) {
            throw new IllegalArgumentException(
                    fieldName + " contains a reserved character and cannot be used in a Redis key: " + segment);
        }
    }
}