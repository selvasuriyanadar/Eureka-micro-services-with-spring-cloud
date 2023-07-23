package com.udacity.vehicles.domain.manufacturer.converter;

import com.udacity.vehicles.domain.manufacturer.repo.ManufacturerRepository;
import com.udacity.vehicles.domain.manufacturer.model.Manufacturer;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import jakarta.persistence.EntityManager;

@Component
public class IntegerToManufacturerConverter
  implements Converter<Integer, Manufacturer> {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Override
    public Manufacturer convert(Integer id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new IllegalArgumentException("Manufacturer Not Found.");
        }

        return entityManager.getReference(Manufacturer.class, id);
    }
}
