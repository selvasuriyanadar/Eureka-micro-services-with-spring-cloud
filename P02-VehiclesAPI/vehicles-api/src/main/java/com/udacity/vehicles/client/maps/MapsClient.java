package com.udacity.vehicles.client.maps;

import com.udacity.vehicles.domain.car.model.Location;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

/**
 * Implements a class to interface with the Maps Client for location data.
 */
@Component
public class MapsClient {

    private static final Logger log = LoggerFactory.getLogger(MapsClient.class);

    private final WebClient client;
    private final ModelMapper mapper;

    public MapsClient(WebClient maps,
            ModelMapper mapper) {
        this.client = maps;
        this.mapper = mapper;
    }

    public AddressFull getAddressFull(double lat, double lon) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/boogle-maps/addresses/search/findByLatAndLon")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .queryParam("projection", "addressFullResponse")
                        .build()
                )
                .retrieve().bodyToMono(AddressFull.class).block();
    }

    public Address getAddress(double lat, double lon) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/boogle-maps/addresses/search/findByLatAndLon")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .build()
                )
                .retrieve().bodyToMono(Address.class).block();
    }

    public boolean exists(double lat, double lon) {
        return client
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/boogle-maps/addresses/search/existsByLatAndLon")
                        .queryParam("lat", lat)
                        .queryParam("lon", lon)
                        .build()
                )
                .retrieve().bodyToMono(Boolean.class).block();
    }

    public Address post(AddressFull addressFull) {
        return client
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/boogle-maps/addresses")
                        .build()
                )
                .bodyValue(addressFull)
                .retrieve().bodyToMono(Address.class).block();
    }

    public Address patch(Long id, AddressFull addressFull) {
        return client
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path("/boogle-maps/addresses/{id}")
                        .build(id)
                )
                .bodyValue(addressFull)
                .retrieve().bodyToMono(Address.class).block();
    }

    public Address updateAddress(Double lat, Double lon) {
        try {
            if (exists(lat, lon)) {
                return patch(getAddressFull(lat, lon).getId(), new AddressFull(lat, lon));
            }
            else {
                return post(new AddressFull(lat, lon));
            }
        }
        catch (Exception e) {
            log.warn("Map service is down");
            e.printStackTrace();
            throw new CouldNotUpdateAddressException();
        }
    }

    /**
     * Gets an address from the Maps client, given latitude and longitude.
     * @param location An object containing "lat" and "lon" of location
     * @return An updated location including street, city, state and zip,
     *   or an exception message noting the Maps service is down
     */
    public Location fillAddressOptional(Location location) {
        try {
            Address address = getAddress(location.getLat(), location.getLon());
            mapper.map(Objects.requireNonNull(address), location);
            return location;
        } catch (Exception e) {
            log.warn("Map service is down");
            return location;
        }
    }

}
