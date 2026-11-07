package com.freebie.restaurantlisting.geocoding;

import com.freebie.restaurantlisting.exception.AddressNotFoundException;
import com.freebie.restaurantlisting.exception.GeocodingUnavailableException;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;

@Service
public class GoogleGeocodingService implements GeocodingService {

    private static final Logger log = LoggerFactory.getLogger(GoogleGeocodingService.class);

    private final RestClient client;
    private final String apiKey;
    private final String region;

    public GoogleGeocodingService(@Qualifier("geocodingRestClient") RestClient client,
                                  @Value("${app.geocoding.api-key}") String apiKey,
                                  @Value("${app.geocoding.region}") String region) {
        this.client = client;
        this.apiKey = apiKey;
        this.region = region;
    }

    @Override
    public Coordinates geocode(String address) {
        GoogleGeocodeResponse body;
        try {
            body = client.get()
                    .uri("/maps/api/geocode/json?address={address}&region={region}&key={key}", address, region, apiKey)
                    .retrieve()
                    .body(GoogleGeocodeResponse.class);
        } catch (RestClientException ex) {
            throw new GeocodingUnavailableException("Could not reach the geocoding service", ex);
        }

        if (body == null || body.status() == null) {
            throw new GeocodingUnavailableException("Empty reply from the geocoding service", null);
        }

        switch (body.status()) {
            case "OK" -> {
                var loc = body.results().get(0).geometry().location();
                return new Coordinates(loc.lat(), loc.lng());
            }
            case "ZERO_RESULTS" -> throw new AddressNotFoundException(address);
            default -> {
                // REQUEST_DENIED, INVALID_REQUEST, OVER_QUERY_LIMIT, UNKNOWN_ERROR:
                // our problem (key, billing, quota), not the user's, so log it and hide the details
                log.error("Geocoding failed: status={}, message={}", body.status(), body.errorMessage());
                throw new GeocodingUnavailableException("Geocoding failed: " + body.status(), null);
            }
        }
    }
}
