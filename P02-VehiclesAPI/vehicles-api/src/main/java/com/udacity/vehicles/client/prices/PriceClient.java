package com.udacity.vehicles.client.prices;

import com.udacity.vehicles.domain.car.model.Car;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

import java.util.*;

/**
 * Implements a class to interface with the Pricing Client for price data.
 */
@FeignClient(name="${pricing.serviceName}", path="/services/price")
public interface PriceClient {

    static final Logger log = LoggerFactory.getLogger(PriceClient.class);

    // In a real-world application we'll want to add some resilience
    // to this method with retries/CB/failover capabilities
    // We may also want to cache the results so we don't need to
    // do a request every time
    /**
     * Gets a vehicle price from the pricing client, given vehicle ID.
     * @param vehicleId ID number of the vehicle for which to get the price
     * @return Currency and price of the requested vehicle,
     *   error message that the vehicle ID is invalid, or note that the
     *   service is down.
     */
    @GetMapping("/search/findByVehicleId")
    public PriceFull getFull(@RequestParam Long vehicleId, @RequestParam String projection);

    default PriceFull getFull(Long vehicleId) {
        return getFull(vehicleId, "priceFullResponse");
    }

    @GetMapping("/search/findByVehicleId")
    public Price get(@RequestParam Long vehicleId);

    @GetMapping("/search/existsByVehicleId")
    public boolean exists(@RequestParam Long vehicleId);

    @PostMapping
    public Price post(@RequestBody Price price);

    @PutMapping(value="/{priceId}", consumes=MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
    public Price patch(@PathVariable("priceId") Long priceId, @RequestBody PriceFull price);

    default Price updateVehiclePrice(Long vehicleId) {
        try {
            if (exists(vehicleId)) {
                return patch(getFull(vehicleId).getPriceId(), new PriceFull().updatePrice());
            }
            else {
                return post(new Price(vehicleId));
            }
        }
        catch (Exception e) {
            log.warn("Price service is down");
            e.printStackTrace();
            throw new CouldNotUpdateVehiclePriceException();
        }
    }

    default Car fillPriceOptional(Car car, ModelMapper mapper) {
        try {
            Price price = get(car.getId());
            mapper.map(Objects.requireNonNull(price), car);
            return car;
        }
        catch (Exception e) {
            log.warn("Price service is down");
            e.printStackTrace();
            return car;
        }
    }

}
