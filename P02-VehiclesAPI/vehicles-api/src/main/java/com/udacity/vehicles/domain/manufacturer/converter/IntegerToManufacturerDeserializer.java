package com.udacity.vehicles.domain.manufacturer.converter;

import com.udacity.vehicles.domain.manufacturer.repo.ManufacturerRepository;
import com.udacity.vehicles.domain.manufacturer.model.Manufacturer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.BeanDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponent;
import jakarta.persistence.EntityManager;

import java.io.IOException;

public class IntegerToManufacturerDeserializer extends StdDeserializer<Manufacturer> implements ResolvableDeserializer {

    private EntityManager entityManager;

    private ManufacturerRepository manufacturerRepository;

    private JsonDeserializer<?> defaultDeserializer;

    public IntegerToManufacturerDeserializer(EntityManager entityManager, ManufacturerRepository manufacturerRepository, JsonDeserializer<?> defaultDeserializer) {
        super(Manufacturer.class);
        this.entityManager = entityManager;
        this.manufacturerRepository = manufacturerRepository;
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public Manufacturer deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        if (!p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return (Manufacturer) defaultDeserializer.deserialize(p, ctxt);
        }

        int id = p.getValueAsInt();
        if (!manufacturerRepository.existsById(id)) {
            throw new IllegalArgumentException("Manufacturer Not Found.");
        }

        return entityManager.getReference(Manufacturer.class, id);
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
    }

}
