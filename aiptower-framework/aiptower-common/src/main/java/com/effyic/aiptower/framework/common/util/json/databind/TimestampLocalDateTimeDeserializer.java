package com.effyic.aiptower.framework.common.util.json.databind;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * 基于时间戳的 LocalDateTime 反序列化器
 * <p>
 * 兼容：
 * 1. Long / Number 毫秒时间戳，如 1798715999000
 * 2. 数字字符串时间戳，如 "1798715999000"
 * 3. 日期时间字符串，如 "2026-12-31 23:59:59"、"2026-12-31T23:59:59"
 *
 * @author 老五
 */
public class TimestampLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    public static final TimestampLocalDateTimeDeserializer INSTANCE = new TimestampLocalDateTimeDeserializer();

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonToken token = p.currentToken();
        // 1. 数字时间戳
        if (token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(p.getValueAsLong()), ZoneId.systemDefault());
        }
        // 2. 字符串：优先按数字时间戳，再按日期格式
        if (token == JsonToken.VALUE_STRING) {
            String text = StrUtil.trim(p.getText());
            if (StrUtil.isBlank(text)) {
                return null;
            }
            if (StrUtil.isNumeric(text)) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.parseLong(text)), ZoneId.systemDefault());
            }
            try {
                // 兼容 ISO：2026-12-31T23:59:59
                String normalized = text.contains("T") ? text.replace('T', ' ') : text;
                // 去掉可能的毫秒 / 时区后缀，只取到秒
                if (normalized.length() > 19) {
                    normalized = normalized.substring(0, 19);
                }
                return LocalDateTime.parse(normalized, FORMATTER);
            } catch (DateTimeParseException ex) {
                throw ctxt.weirdStringException(text, LocalDateTime.class,
                        "期望毫秒时间戳或格式 yyyy-MM-dd HH:mm:ss");
            }
        }
        return (LocalDateTime) ctxt.handleUnexpectedToken(LocalDateTime.class, p);
    }

}
