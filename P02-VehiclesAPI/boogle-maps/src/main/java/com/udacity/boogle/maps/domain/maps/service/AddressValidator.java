package com.udacity.boogle.maps.domain.maps.service;

import com.udacity.boogle.maps.domain.maps.repo.AddressRepository;
import com.udacity.boogle.maps.domain.maps.logic.AddressLogic;
import com.udacity.boogle.maps.domain.maps.model.Address;
import com.udacity.boogle.maps.lib.util.Util;

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
public class AddressValidator implements Validator {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return Address.class.equals(clazz);
    }

    @Override
    @Transactional
    public void validate(Object obj, Errors errors) {
        Address newAddress = (Address) obj;
        Optional<Address> currentAddress = Util.retrieveCurrentObject(entityManager.unwrap(Session.class), addressRepository, newAddress, Address::getId);

        AddressLogic.validate(errors, newAddress, (lat, lon) -> ((currentAddress.isPresent() && addressRepository.existsByLatAndLonAndIdNot(lat, lon, currentAddress.get().getId()))
          || (!currentAddress.isPresent() && addressRepository.existsByLatAndLon(lat, lon))));
    }

}
