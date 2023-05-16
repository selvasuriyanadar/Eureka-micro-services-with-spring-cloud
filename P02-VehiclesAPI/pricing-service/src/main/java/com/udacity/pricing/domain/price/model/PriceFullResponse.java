package com.udacity.pricing.domain.price.model;

import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(name = "priceFullResponse", types = { Price.class })
public interface PriceFullResponse {

    public Long getPriceId();

    public Long getVehicleId();

    public Price.Currency getCurrency();

    public BigDecimal getPrice();

}
