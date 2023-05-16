package com.udacity.pricing.domain.price.service;

import com.udacity.pricing.domain.price.repo.PriceRepository;
import com.udacity.pricing.domain.price.model.Price;
import com.udacity.pricing.domain.price.logic.PriceLogic;
import com.udacity.pricing.util.Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Errors;
import org.springframework.stereotype.Component;
import org.hibernate.Session;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.modelmapper.Conditions;

import java.util.*;

@Component
public class PriceValidator implements Validator {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PriceRepository priceRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Price.class.equals(clazz);
    }

    @Override
    @Transactional
    public void validate(Object obj, Errors errors) {
        Price newPrice = (Price) obj;
        Optional<Price> currentPrice = Util.retrieveCurrentObject(entityManager.unwrap(Session.class), priceRepository, newPrice, Price::getPriceId);

        PriceLogic.validate(errors, newPrice, (vehicleId) -> ((currentPrice.isPresent() && priceRepository.existsByVehicleIdAndPriceIdNot(vehicleId, currentPrice.get().getPriceId()))
          || (!currentPrice.isPresent() && priceRepository.existsByVehicleId(vehicleId))));
    }

}
