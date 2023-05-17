package com.udacity.boogle.maps.domain.maps.service;

import com.udacity.boogle.maps.domain.maps.repo.AddressRepository;
import com.udacity.boogle.maps.domain.maps.logic.MockAddressLogic;
import com.udacity.boogle.maps.domain.maps.model.Address;
import com.udacity.boogle.maps.lib.util.Util;

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
public class AddressEventHandler {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    private Optional<Address> currentAddress = Optional.empty();

    @HandleBeforeCreate
    @HandleBeforeSave
    @Transactional
    public void fetchCurrentAddress(Address newAddress) {
        this.currentAddress = Util.retrieveCurrentObject(entityManager.unwrap(Session.class), addressRepository, newAddress, Address::getId);
        if (this.currentAddress.isPresent()) {
            Util.mapIfDestinationNullModifying(this.currentAddress.get(), newAddress, Address.class);
        }
    }

    @HandleAfterCreate
    @HandleAfterSave
    public void generateOnSave(Address newAddress) {
        if (!this.currentAddress.isPresent()
          || !this.currentAddress.get().getLat().equals(newAddress.getLat())
          || !this.currentAddress.get().getLon().equals(newAddress.getLon())) {
            Util.mapIfNotNull(MockAddressLogic.getRandom(), newAddress);
        }
        addressRepository.save(newAddress);
    }

}
