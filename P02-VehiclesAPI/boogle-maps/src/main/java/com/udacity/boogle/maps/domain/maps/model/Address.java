package com.udacity.boogle.maps.domain.maps.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Transient;
import org.springframework.beans.factory.annotation.Value;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Declares a class to store an address, city, state and zip code.
 */
@Entity
public class Address {

    @Id
    @GeneratedValue
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @NotNull
    private Double lat;
    @NotNull
    private Double lon;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String address;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String city;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String state;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String zip;

    public Address() {
    }

    public Address(String address, String city, String state, String zip) {
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return this.lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }
}
