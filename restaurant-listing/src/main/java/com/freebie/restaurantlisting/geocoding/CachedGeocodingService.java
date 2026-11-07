package com.freebie.restaurantlisting.geocoding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

@Service
@Primary
public class CachedGeocodingService implements GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(CachedGeocodingService.class);

    private final GoogleGeocodingService delegate;
    private final StringRedisTemplate template;
    private final Duration ttl;
    private final String keyPrefix;

    public CachedGeocodingService(GoogleGeocodingService delegate,
                                  StringRedisTemplate template,
                                  @Value("${app.geocoding.cache-ttl-days}") Duration ttl,
                                  @Value("${app.geocoding.region}") String region
    ) {
        this.delegate = delegate;
        this.template = template;
        this.ttl = ttl;
        this.keyPrefix = "geo:v1:" + region + ":";
    }

    @Override
    public Coordinates geocode(String address) {
        String key = keyForCache(address);

        Coordinates cached = readCache(key);
        if (cached != null) {
            return cached;
        }

        Coordinates fresh = delegate.geocode(address);   // throws on failure, so failures are never cached
        writeCache(key, fresh);
        return fresh;
    }

    private Coordinates readCache(String key) {
        try {
            String value = template.opsForValue().get(key);
            if (value == null) return null;
            String[] parts = value.split(",");
            return new Coordinates(Double.parseDouble(parts[0]), Double.parseDouble(parts[1]));
        } catch (DataAccessException | NumberFormatException | ArrayIndexOutOfBoundsException e) {
            log.warn("Geocode cache read failed, calling Google instead: {}", e.getMessage());
            return null;
        }
    }

    private void writeCache(String key, Coordinates coords) {
        try {
            template.opsForValue().set(key, coords.latitude() + "," + coords.longitude(), ttl);
        } catch (DataAccessException e) {
            log.warn("Geocode cache write failed: {}", e.getMessage());
        }
    }

    private String keyForCache(String address) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(AddressNormalizer.normalize(address).getBytes(StandardCharsets.UTF_8));
            return keyPrefix + HexFormat.of().formatHex(digest, 0, 16);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
