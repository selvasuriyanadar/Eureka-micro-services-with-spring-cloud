package com.udacity.vehicles.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsInAnyOrder;

import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.domain.car.model.Condition;
import com.udacity.vehicles.domain.car.model.Location;
import com.udacity.vehicles.domain.car.model.Car;
import com.udacity.vehicles.domain.car.model.Details;
import com.udacity.vehicles.domain.manufacturer.model.Manufacturer;
import com.udacity.vehicles.domain.car.service.CarService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.web.servlet.MockMvc;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonSerializationContext;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.util.*;
import java.time.LocalDateTime;
import java.time.Instant;
import java.time.ZoneId;
import java.lang.reflect.Type;

/**
 * Implements testing of the CarController class.
 */
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class CarControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Car> json;

    @MockBean
    private CarService carService;

    @MockBean
    private PriceClient priceClient;

    @MockBean
    private MapsClient mapsClient;

    private static Gson gson;

    @BeforeAll
    public static void initialise() {
        gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                Instant instant = Instant.ofEpochMilli(json.getAsJsonPrimitive().getAsLong());
                return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            }
        }).registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public JsonElement serialize(LocalDateTime value, Type type, JsonSerializationContext jsonSerializationContext) {
                return new Gson().toJsonTree(value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
            }
        }).create();
    }

    /**
     * Creates pre-requisites for testing, such as an example car.
     */
    @BeforeEach
    public void setup() {
        Car car = getCar();
        car.setId(1L);
        given(carService.save(any())).willReturn(car);
        given(carService.findById(any())).willReturn(car);
        given(carService.list()).willReturn(Collections.singletonList(car));
    }

    /**
     * Tests for successful creation of new car in the system
     * @throws Exception when car creation fails in the system
     */
    @Test
    public void createCar() throws Exception {
        Car car = getCar();
        mvc.perform(
                post(new URI("/cars"))
                        .content(json.write(car).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isCreated());
    }

    /**
     * Tests if the read operation appropriately returns a list of vehicles.
     * @throws Exception if the read operation of the vehicle list fails
     */
    @Test
    public void listCars() throws Exception {
        Car car = getCar();
        car.setId(1L);
        JsonObject response = new JsonObject();
        JsonObject carList = new JsonObject();
        carList.add("carList", gson.toJsonTree(new ArrayList<>(Arrays.asList(car))));
        response.add("_embedded", carList);
        mvc.perform(
                get(new URI("/cars")))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(jsonPath("_embedded.carList").isArray())
                .andExpect(content().json(gson.toJson(response)));
    }

    /**
     * Tests the read operation for a single car by ID.
     * @throws Exception if the read operation for a single car fails
     */
    @Test
    public void findCar() throws Exception {
        Car car = getCar();
        car.setId(1L);
        mvc.perform(
                get(new URI("/cars/" + car.getId())))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(content().json(gson.toJson(car)));
    }

    /**
     * Tests the deletion of a single car by ID.
     * @throws Exception if the delete operation of a vehicle fails
     */
    @Test
    public void deleteCar() throws Exception {
        mvc.perform(
                delete(new URI("/cars/" + 1L)))
                .andExpect(status().isNoContent());
    }

    /**
     * Creates an example Car object for use in testing.
     * @return an example Car object
     */
    private Car getCar() {
        Car car = new Car();
        car.setLocation(new Location(40.730610, -73.935242));
        Details details = new Details();
        Manufacturer manufacturer = new Manufacturer(101, "Chevrolet");
        details.setManufacturer(manufacturer);
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
        car.setCondition(Condition.USED);
        return car;
    }
}
