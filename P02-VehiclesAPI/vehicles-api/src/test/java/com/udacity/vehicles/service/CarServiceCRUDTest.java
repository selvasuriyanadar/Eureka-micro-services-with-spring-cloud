package com.udacity.vehicles.service;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.model.Condition;
import com.udacity.vehicles.domain.car.model.Location;
import com.udacity.vehicles.domain.car.model.Car;
import com.udacity.vehicles.domain.car.model.Details;
import com.udacity.vehicles.domain.manufacturer.model.Manufacturer;
import com.udacity.vehicles.domain.car.service.CarService;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.*;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarServiceCRUDTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @Autowired
    private JacksonTester<CarList> carListJson;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    private ResultActions createCar(Car car) throws Exception {
        return mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private ResultActions editCar(long id, Car car) throws Exception {
        return mvc.perform(
                put(new URI("/cars/" + id))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private ResultActions getCar(long id) throws Exception {
        return mvc.perform(
                get(new URI("/cars/" + id))
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private ResultActions deleteCar(long id) throws Exception {
        return mvc.perform(
                delete(new URI("/cars/" + id))
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private static class CarList {
        public static class Embedded {
            public static class Car {
                public Long id;
                public LocalDateTime createdAt;
                public LocalDateTime modifiedAt;
            }
            public List<Car> carList;
        }
        public Embedded _embedded;
    }

    private List<CarList.Embedded.Car> fetchCars() throws Exception {
        CarList carList = carListJson.parseObject(mvc.perform(
                get(new URI("/cars"))
                        .accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse().getContentAsString());
        if (carList._embedded != null) {
            return carList._embedded.carList;
        }
        return new ArrayList<>();
    }

    private Car fetchCar(long id) throws Exception {
        return json.parseObject(mvc.perform(
                get(new URI("/cars/" + id))
                        .accept(MediaType.APPLICATION_JSON_UTF8))
            .andReturn().getResponse().getContentAsString());
    }

    /**
     * A car requires condition, details, location to be created.
     * Details requires body, model, manufacturerId fields.
     * It additionally includes numberOfDoors, fuelType, engine, mileage, modelYear, productionYear, externalColor fields.
     * Location requires lat and lon fields.
     * id, createdAt and modifiedAt are auto generated on creation.
     */
    @Test
    public void createCarTest() throws Exception {
        Car car = getCar();
        createCar(car)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("condition", is(car.getCondition().toString())))
                .andExpect(jsonPath("location.lat", is(car.getLocation().getLat())))
                .andExpect(jsonPath("location.lon", is(car.getLocation().getLon())))
                .andExpect(jsonPath("details.manufacturerId", is(car.getDetails().getManufacturerId())))
                .andExpect(jsonPath("details.model", is(car.getDetails().getModel())))
                .andExpect(jsonPath("details.mileage", is(car.getDetails().getMileage())))
                .andExpect(jsonPath("details.externalColor", is(car.getDetails().getExternalColor())))
                .andExpect(jsonPath("details.body", is(car.getDetails().getBody())))
                .andExpect(jsonPath("details.engine", is(car.getDetails().getEngine())))
                .andExpect(jsonPath("details.fuelType", is(car.getDetails().getFuelType())))
                .andExpect(jsonPath("details.modelYear", is(car.getDetails().getModelYear())))
                .andExpect(jsonPath("details.productionYear", is(car.getDetails().getProductionYear())))
                .andExpect(jsonPath("details.numberOfDoors", is(car.getDetails().getNumberOfDoors())))
                .andExpect(jsonPath("id", notNullValue()))
                .andExpect(jsonPath("createdAt", notNullValue()))
                .andExpect(jsonPath("modifiedAt", notNullValue()));
    }

    /**
     * all fields as in car creation can be edited.
     * modifiedAt is auto updated on edit.
     */
    @Test
    public void editCarTest() throws Exception {
        Car car = getCar();
        createCar(car)
                .andExpect(status().isCreated());

        CarList.Embedded.Car response = fetchCars().get(0);

        car.setCondition(Condition.NEW);
        car.setLocation(new Location(-13.807202, 81.035441));
        Details details = new Details();
        details.setManufacturerId(102);
        details.setModel("Jaguvar");
        details.setMileage(38900);
        details.setExternalColor("silver");
        details.setBody("vedan");
        details.setEngine("6.6L V11");
        details.setFuelType("Water");
        details.setModelYear(2099);
        details.setProductionYear(2098);
        details.setNumberOfDoors(5);
        car.setDetails(details);
        editCar(response.id, car)
                .andExpect(status().isOk())
                .andExpect(jsonPath("condition", is(car.getCondition().toString())))
                .andExpect(jsonPath("location.lat", is(car.getLocation().getLat())))
                .andExpect(jsonPath("location.lon", is(car.getLocation().getLon())))
                .andExpect(jsonPath("details.manufacturerId", is(car.getDetails().getManufacturerId())))
                .andExpect(jsonPath("details.model", is(car.getDetails().getModel())))
                .andExpect(jsonPath("details.mileage", is(car.getDetails().getMileage())))
                .andExpect(jsonPath("details.externalColor", is(car.getDetails().getExternalColor())))
                .andExpect(jsonPath("details.body", is(car.getDetails().getBody())))
                .andExpect(jsonPath("details.engine", is(car.getDetails().getEngine())))
                .andExpect(jsonPath("details.fuelType", is(car.getDetails().getFuelType())))
                .andExpect(jsonPath("details.modelYear", is(car.getDetails().getModelYear())))
                .andExpect(jsonPath("details.productionYear", is(car.getDetails().getProductionYear())))
                .andExpect(jsonPath("details.numberOfDoors", is(car.getDetails().getNumberOfDoors())))
                .andExpect(jsonPath("id", notNullValue()))
                .andExpect(jsonPath("createdAt", notNullValue()))
                .andExpect(jsonPath("modifiedAt", not(response.modifiedAt)));
    }

    /**
     * all fields as in car creation can be get in the get api call.
     */
    @Test
    public void getCarTest() throws Exception {
        Car car = getCar();
        createCar(car)
                .andExpect(status().isCreated());

        CarList.Embedded.Car response = fetchCars().get(0);

        getCar(response.id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("condition", is(car.getCondition().toString())))
                .andExpect(jsonPath("location.lat", is(car.getLocation().getLat())))
                .andExpect(jsonPath("location.lon", is(car.getLocation().getLon())))
                .andExpect(jsonPath("details.manufacturerId", is(car.getDetails().getManufacturerId())))
                .andExpect(jsonPath("details.model", is(car.getDetails().getModel())))
                .andExpect(jsonPath("details.mileage", is(car.getDetails().getMileage())))
                .andExpect(jsonPath("details.externalColor", is(car.getDetails().getExternalColor())))
                .andExpect(jsonPath("details.body", is(car.getDetails().getBody())))
                .andExpect(jsonPath("details.engine", is(car.getDetails().getEngine())))
                .andExpect(jsonPath("details.fuelType", is(car.getDetails().getFuelType())))
                .andExpect(jsonPath("details.modelYear", is(car.getDetails().getModelYear())))
                .andExpect(jsonPath("details.productionYear", is(car.getDetails().getProductionYear())))
                .andExpect(jsonPath("details.numberOfDoors", is(car.getDetails().getNumberOfDoors())))
                .andExpect(jsonPath("id", notNullValue()))
                .andExpect(jsonPath("createdAt", notNullValue()))
                .andExpect(jsonPath("modifiedAt", notNullValue()));
    }

    /**
     * the car is deleted after calling the delete car api
     */
    @Test
    public void deleteCarTest() throws Exception {
        Car car = getCar();
        createCar(car)
                .andExpect(status().isCreated());

        CarList.Embedded.Car response = fetchCars().get(0);

        deleteCar(response.id)
                .andExpect(status().isNoContent());

        Assertions.assertTrue(fetchCars().isEmpty());
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setCondition(Condition.USED);
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        details.setManufacturerId(101);
        details.setModel("Impala");
        details.setMileage(32280);
        details.setExternalColor("white");
        details.setBody("sedan");
        details.setEngine("3.6L V6");
        details.setFuelType("Gasoline");
        details.setModelYear(2018);
        details.setProductionYear(2018);
        details.setNumberOfDoors(4);
        car.setDetails(details);
        return car;
    }

}
