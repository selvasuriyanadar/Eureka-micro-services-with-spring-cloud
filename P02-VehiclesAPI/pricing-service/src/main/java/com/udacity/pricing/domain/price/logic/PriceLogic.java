package com.udacity.pricing.domain.price.logic;

import com.udacity.pricing.domain.price.model.Price;

import org.springframework.validation.Errors;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class PriceLogic {

    /**
     * Gets a random price to fill in for a given vehicle ID.
     * @return random price for a vehicle
     */
    public static BigDecimal randomPrice() {
        return new BigDecimal(ThreadLocalRandom.current().nextDouble(1, 5))
                .multiply(new BigDecimal(5000d)).setScale(2, RoundingMode.HALF_UP);
    }

    public static interface DoesAnotherPriceExistForTheVehicle {
        public boolean exist(Long vehicleId);
    }

    public static void validate(Errors errors, Price price, DoesAnotherPriceExistForTheVehicle doesAnotherPriceExistForTheVehicle) {
        if (price.getVehicleId() == null) {
            errors.rejectValue("vehicleId", "field.required", "Vehicle Id is required.");
        }
        if (price.getCurrency() == null) {
            errors.rejectValue("currency", "field.required", "Currency is required.");
        }

        if (doesAnotherPriceExistForTheVehicle.exist(price.getVehicleId())) {
            errors.rejectValue("vehicleId", "data.duplicate", "Already a Price entry for this Vehicle Id exist.");
        }
    }

}
