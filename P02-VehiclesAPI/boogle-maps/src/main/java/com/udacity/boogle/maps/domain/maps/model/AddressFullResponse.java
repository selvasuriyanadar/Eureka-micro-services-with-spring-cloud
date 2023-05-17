package com.udacity.boogle.maps.domain.maps.model;

import org.springframework.data.rest.core.config.Projection;

@Projection(name = "addressFullResponse", types = { Address.class })
public interface AddressFullResponse {

    public Long getId();

    public Double getLat();

    public Double getLon();

    public String getAddress();

    public String getCity();

    public String getState();

    public String getZip();

}
