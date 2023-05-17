package com.udacity.boogle.maps.domain.maps.config;

import com.udacity.boogle.maps.domain.maps.service.AddressValidator;

import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.event.ValidatingRepositoryEventListener;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.data.rest.core.mapping.ExposureConfiguration;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.http.HttpMethod;
import org.springframework.validation.Validator;

@Configuration
public class BoogleMapsConfig implements RepositoryRestConfigurer {

    @Autowired
    private AddressValidator addressValidator;

    @Override
    public void configureValidatingRepositoryEventListener(ValidatingRepositoryEventListener v) {
        v.addValidator("beforeCreate", addressValidator);
        v.addValidator("beforeSave", addressValidator);
    }

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        ExposureConfiguration exposure = config.getExposureConfiguration();
        exposure.disablePatchOnItemResources();
        exposure.disablePutForCreation();
    }

}
