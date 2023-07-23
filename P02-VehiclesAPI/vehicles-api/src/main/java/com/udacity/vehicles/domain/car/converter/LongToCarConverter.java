package com.udacity.vehicles.domain.car.converter;

import com.udacity.vehicles.domain.car.repo.CarRepository;
import com.udacity.vehicles.domain.car.model.Car;
import com.udacity.vehicles.domain.car.exception.CarNotFoundException;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import jakarta.persistence.EntityManager;

@Component
public class LongToCarConverter
  implements Converter<Long, Car> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Override
    public Car convert(Long id) {
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException();
        }

        return entityManager.getReference(Car.class, id);
    }
}
