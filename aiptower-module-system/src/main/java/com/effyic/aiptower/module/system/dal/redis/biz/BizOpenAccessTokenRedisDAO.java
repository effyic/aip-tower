package com.effyic.aiptower.module.system.dal.redis.biz;

import com.effyic.aiptower.module.system.dal.redis.RedisKeyConstants;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

/**
 * B 端开放接口 AccessToken Redis DAO
 */
@Repository
public class BizOpenAccessTokenRedisDAO {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    public void set(String accessToken, Long tenantId, long expiresSeconds) {
        stringRedisTemplate.opsForValue().set(formatKey(accessToken), String.valueOf(tenantId),
                expiresSeconds, TimeUnit.SECONDS);
    }

    public Long get(String accessToken) {
        String value = stringRedisTemplate.opsForValue().get(formatKey(accessToken));
        return value != null ? Long.valueOf(value) : null;
    }

    public void delete(String accessToken) {
        stringRedisTemplate.delete(formatKey(accessToken));
    }

    private static String formatKey(String accessToken) {
        return String.format(RedisKeyConstants.BIZ_OPEN_ACCESS_TOKEN, accessToken);
    }

}
