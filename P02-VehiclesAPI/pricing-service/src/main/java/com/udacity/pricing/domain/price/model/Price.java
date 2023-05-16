package com.udacity.pricing.domain.price.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Represents the price of a given vehicle, including currency.
 */
@Entity
public class Price {

    public enum Currency {
        INR, USD
    }

    @Id
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @GeneratedValue
    private Long priceId;

    @NotNull
    private Long vehicleId;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Currency currency = Currency.USD;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal price;

    @Override
    public String toString() {
        return "priceId=" + priceId + ":vehicleId=" + vehicleId + ":currency=" + currency + ":price=" + price;
    }

    public Long getPriceId() {
        return priceId;
    }

    public void setPriceId(Long priceId) {
        this.priceId = priceId;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

}
