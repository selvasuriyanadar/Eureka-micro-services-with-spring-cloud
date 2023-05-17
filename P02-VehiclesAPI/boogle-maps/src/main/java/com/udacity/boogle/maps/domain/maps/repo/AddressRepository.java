package com.udacity.boogle.maps.domain.maps.repo;

import com.udacity.boogle.maps.domain.maps.model.Address;
import static com.udacity.boogle.maps.lib.util.Util.BaseRestRepository;
import com.udacity.boogle.maps.lib.util.Util;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;

import java.util.*;

@RepositoryRestResource(itemResourceRel = "address", collectionResourceRel = "addresses", path = "addresses")
public interface AddressRepository extends BaseRestRepository<Address, Long> {

    public Optional<Address> findByLatAndLon(@Param("lat") Double lat, @Param("lon") Double lon);

    @RestResource(exported = false)
    public boolean existsByLatAndLonAndIdNot(Double lat, Double lon, Long id);

    public boolean existsByLatAndLon(@Param("lat") Double lat, @Param("lon") Double lon);

}
