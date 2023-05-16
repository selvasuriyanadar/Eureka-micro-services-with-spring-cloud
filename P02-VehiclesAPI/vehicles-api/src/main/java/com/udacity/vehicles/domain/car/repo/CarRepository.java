package com.udacity.vehicles.domain.car.repo;

import com.udacity.vehicles.domain.car.model.Car;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

}
