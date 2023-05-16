package com.udacity.vehicles.domain.manufacturer.repo;

import com.udacity.vehicles.domain.manufacturer.model.Manufacturer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Integer> {

}
