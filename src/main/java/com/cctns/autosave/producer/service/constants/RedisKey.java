package com.cctns.autosave.producer.service.constants;

/**
 * Wraps a build redis key
 *
 * @param value
 */
public record RedisKey(String value) {
    @Override
    public String toString() {
        return value;
    }
}