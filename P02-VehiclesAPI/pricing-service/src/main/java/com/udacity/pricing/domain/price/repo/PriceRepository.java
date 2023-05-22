package com.udacity.pricing.domain.price.repo;

import com.udacity.pricing.domain.price.model.Price;
import static com.udacity.pricing.util.Util.BaseRestRepository;
import com.udacity.pricing.util.Util;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.util.*;

@Repository
@RepositoryRestResource(collectionResourceRel = "price", path = "price")
public interface PriceRepository extends BaseRestRepository<Price, Long> {

    /**
     * Gets the price for a requested vehicle.
     * @param vehicleId ID number of the vehicle for which the price is requested
     * @return price of the vehicle, or error that it was not found.
     */
    public Optional<Price> findByVehicleId(@Param("vehicleId") Long vehicleId);

    @RestResource(exported = false)
    public boolean existsByVehicleIdAndPriceIdNot(Long vehicleId, Long priceId);

    public boolean existsByVehicleId(Long vehicleId);

}
