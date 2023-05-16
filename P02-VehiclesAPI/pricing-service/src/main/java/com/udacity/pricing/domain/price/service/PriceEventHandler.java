package com.udacity.pricing.domain.price.service;

import com.udacity.pricing.domain.price.repo.PriceRepository;
import com.udacity.pricing.domain.price.model.Price;
import com.udacity.pricing.domain.price.logic.PriceLogic;
import com.udacity.pricing.util.Util;

import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Component;
import org.hibernate.Session;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.EntityManager;

import java.util.*;

@RepositoryEventHandler
@Component
public class PriceEventHandler {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PriceRepository priceRepository;

    private Optional<Price> currentPrice = Optional.empty();

    @HandleBeforeCreate
    @HandleBeforeSave
    @Transactional
    public void fetchCurrentPrice(Price newPrice) {
        this.currentPrice = Util.retrieveCurrentObject(entityManager.unwrap(Session.class), priceRepository, newPrice, Price::getPriceId);
    }

    @HandleAfterCreate
    @HandleAfterSave
    public void generateOnSave(Price newPrice) {
        if (!this.currentPrice.isPresent()
          || !this.currentPrice.get().getVehicleId().equals(newPrice.getVehicleId())) {
            newPrice.setPrice(PriceLogic.randomPrice());
        }
        priceRepository.save(newPrice);
    }

}
