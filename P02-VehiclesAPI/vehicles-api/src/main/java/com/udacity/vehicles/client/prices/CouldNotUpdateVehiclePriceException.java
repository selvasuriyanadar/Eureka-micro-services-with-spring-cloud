package com.udacity.vehicles.client.prices;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Could Not Update Vehicle Price.")
public class CouldNotUpdateVehiclePriceException {

    public CouldNotUpdateVehiclePriceException() {
    }

    public CouldNotUpdateVehiclePriceException(String message) {
        super(message);
    }

}
