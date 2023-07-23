package com.udacity.vehicles;

import com.udacity.vehicles.domain.car.repo.CarRepository;
import com.udacity.vehicles.domain.car.model.Car;
import com.udacity.vehicles.domain.car.converter.LongToCarDeserializer;
import com.udacity.vehicles.domain.manufacturer.repo.ManufacturerRepository;
import com.udacity.vehicles.domain.manufacturer.model.Manufacturer;
import com.udacity.vehicles.domain.manufacturer.converter.IntegerToManufacturerDeserializer;

import jakarta.persistence.EntityManager;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.FormatterRegistry;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;

import java.util.*;

@Configuration
public class VehiclesApiWebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private ManufacturerRepository manufacturerRepository;

    @Bean
    public Module carModule() {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier()
        {
            @Override public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
            {
                if (beanDesc.getBeanClass() == Car.class)
                    return new LongToCarDeserializer(entityManager, carRepository, deserializer);
                return deserializer;
            }
        });
        return module;
    }

    @Bean
    public Module manufacturerModule() {
        SimpleModule module = new SimpleModule();
        module.setDeserializerModifier(new BeanDeserializerModifier()
        {
            @Override public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer)
            {
                if (beanDesc.getBeanClass() == Manufacturer.class)
                    return new IntegerToManufacturerDeserializer(entityManager, manufacturerRepository, deserializer);
                return deserializer;
            }
        });
        return module;
    }

}
