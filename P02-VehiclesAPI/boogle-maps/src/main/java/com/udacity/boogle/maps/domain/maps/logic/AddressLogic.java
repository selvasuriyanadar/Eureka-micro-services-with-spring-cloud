package com.udacity.boogle.maps.domain.maps.logic;

import com.udacity.boogle.maps.domain.maps.model.Address;

import org.springframework.validation.Errors;

public class AddressLogic {

    public static interface DoesAnotherAddressExistForTheLocation {
        public boolean exist(Double lat, Double lon);
    }

    public static void validate(Errors errors, Address address, DoesAnotherAddressExistForTheLocation doesAnotherAddressExistForTheLocation) {
        if (address.getLat() == null) {
            errors.rejectValue("lat", "field.required", "Latitude is required.");
        }
        if (address.getLon() == null) {
            errors.rejectValue("lon", "field.required", "Longitude is required.");
        }

        if (doesAnotherAddressExistForTheLocation.exist(address.getLat(), address.getLon())) {
            errors.reject("data.duplicate", "Already an Address entry for this Location exist.");
        }
    }

}
