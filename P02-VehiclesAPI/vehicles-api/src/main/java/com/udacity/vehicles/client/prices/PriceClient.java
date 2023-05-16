package com.udacity.vehicles.client.prices;

import com.udacity.vehicles.client.FeignClientConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.*;

/**
 * Implements a class to interface with the Pricing Client for price data.
 */
@FeignClient(name = "${pricing.serviceName}", path="/services/price", configuration=FeignClientConfig.class)
public interface PriceClient {

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
    public Price get(@RequestParam Long vehicleId);

}
