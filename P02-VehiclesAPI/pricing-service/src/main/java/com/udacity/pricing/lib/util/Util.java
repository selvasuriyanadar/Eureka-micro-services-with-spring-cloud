package com.udacity.pricing.util;

import org.springframework.data.repository.Repository;
import org.springframework.data.rest.core.annotation.RestResource;
import org.hibernate.Session;
import jakarta.persistence.EntityManager;
import org.modelmapper.ModelMapper;
import org.modelmapper.Conditions;

import java.util.*;
import java.lang.reflect.InvocationTargetException;

public class Util {

    public static ModelMapper getSimpleMapper() {
        return new ModelMapper();
    }

    public static ModelMapper getIgnoreNullMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

    public static <D> D mapIfDestinationNull(D source, D destination, Class<D> typeClass) {
        D temp;
        try {
            temp = typeClass.getConstructor().newInstance();
        }
        catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Could not initialise temp.");
        }
        getSimpleMapper().map(source, temp);
        getIgnoreNullMapper().map(destination, temp);
        return temp;
    }

    public interface BaseRestRepository<D, ID> extends Repository<D, ID> {

        public D save(D price);

        @RestResource(exported = false)
        public Optional<D> findById(ID id);

        @RestResource(exported = false)
        public void deleteById(ID id);

        @RestResource(exported = false)
        public boolean existsById(ID id);

    }

    public interface GetId<D, ID> {
        public ID get(D data);
    }

    public static <D, ID> Optional<D> retrieveCurrentObject(Session session, BaseRestRepository<D, ID> baseRestRepository, D newObject, GetId<D, ID> getId) {
        if (!session.contains(newObject)) {
            return Optional.empty();
        }

        session.detach(newObject);
        Optional<D> current = baseRestRepository.findById(getId.get(newObject));
        if (current.isPresent()) {
            session.detach(current.get());
        }
        session.update(newObject);
        return current;
    }

}