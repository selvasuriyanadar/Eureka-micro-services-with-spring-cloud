package com.udacity.vehicles.domain.car.converter;

import com.udacity.vehicles.domain.car.repo.CarRepository;
import com.udacity.vehicles.domain.car.model.Car;
import com.udacity.vehicles.domain.car.exception.CarNotFoundException;

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

public class LongToCarDeserializer extends StdDeserializer<Car> implements ResolvableDeserializer {

    private EntityManager entityManager;

    private CarRepository carRepository;

    private JsonDeserializer<?> defaultDeserializer;

    public LongToCarDeserializer(EntityManager entityManager, CarRepository carRepository, JsonDeserializer<?> defaultDeserializer) {
        super(Car.class);
        this.entityManager = entityManager;
        this.carRepository = carRepository;
        this.defaultDeserializer = defaultDeserializer;
    }

    @Override
    public Car deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        if (!p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return (Car) defaultDeserializer.deserialize(p, ctxt);
        }

        long id = p.getValueAsLong();
        if (!carRepository.existsById(id)) {
            throw new CarNotFoundException();
        }

        return entityManager.getReference(Car.class, id);
    }

    @Override
    public void resolve(DeserializationContext ctxt) throws JsonMappingException {
        ((ResolvableDeserializer) defaultDeserializer).resolve(ctxt);
    }

}
