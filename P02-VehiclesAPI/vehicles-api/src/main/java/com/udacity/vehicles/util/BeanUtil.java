package com.udacity.vehicles.util;

import org.springframework.beans.BeanUtils;
import org.modelmapper.ModelMapper;
import org.modelmapper.Conditions;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.Function;
import java.util.concurrent.ConcurrentHashMap;

public class BeanUtil {

    public static ModelMapper getSimpleMapper() {
        return new ModelMapper();
    }

    public static ModelMapper getIgnoreNullMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setPropertyCondition(Conditions.isNotNull());
        return modelMapper;
    }

    public static <T> T transferIfNotNull(T source, T destination) {
        getIgnoreNullMapper().map(source, destination);
        return destination;
    }

}
