package com.vamshi.smartroute.cache;

import com.vamshi.smartroute.dto.RouteResponseDto;
import com.vamshi.smartroute.model.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

    private static final Logger log = LoggerFactory.getLogger(CacheService.class);
    private static final Duration DEFAULT_TTL = Duration.ofHours(12);

    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheKeyGenerator keyGenerator;
    private final Map<String, RouteResponseDto> localCache = new ConcurrentHashMap<>();

    public CacheService(@Autowired(required = false) RedisTemplate<String, Object> redisTemplate, CacheKeyGenerator keyGenerator) {
        this.redisTemplate = redisTemplate;
        this.keyGenerator = keyGenerator;
    }

    public RouteResponseDto getCachedRoute(long graphVersion, RoutingContext context) {
        String key = keyGenerator.generateRouteCacheKey(graphVersion, context);
        if (redisTemplate != null) {
            try {
                Object cached = redisTemplate.opsForValue().get(key);
                if (cached instanceof RouteResponseDto dto) {
                    log.info("Redis CACHE HIT for key: {}", key);
                    return dto;
                }
            } catch (Exception ex) {
                log.warn("Redis unavailable, using local cache: {}", ex.getMessage());
            }
        }
        RouteResponseDto localDto = localCache.get(key);
        if (localDto != null) {
            log.info("Local Memory CACHE HIT for key: {}", key);
            return localDto;
        }
        log.info("CACHE MISS for graph version: {}", graphVersion);
        return null;
    }

    public void cacheRoute(long graphVersion, RoutingContext context, RouteResponseDto response) {
        String key = keyGenerator.generateRouteCacheKey(graphVersion, context);
        localCache.put(key, response);
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(key, response, DEFAULT_TTL);
                log.info("Cached route in Redis with key: {}", key);
            } catch (Exception ex) {
                log.warn("Failed to write to Redis cache: {}", ex.getMessage());
            }
        }
    }
}
