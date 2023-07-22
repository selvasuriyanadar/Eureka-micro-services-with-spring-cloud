package com.udacity.vehicles.client.maps;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Could Not Update Address.")
public class CouldNotUpdateAddressException extends RuntimeException {

    public CouldNotUpdateAddressException() {
    }

    public CouldNotUpdateAddressException(String message) {
        super(message);
    }

}
