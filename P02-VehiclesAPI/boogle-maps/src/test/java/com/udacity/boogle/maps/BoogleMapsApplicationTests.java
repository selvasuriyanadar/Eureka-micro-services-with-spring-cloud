package com.udacity.boogle.maps;

import com.udacity.boogle.maps.domain.maps.model.Address;

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
import jakarta.persistence.EntityManager;
import org.hibernate.Session;

import java.net.URI;
import java.util.*;
import java.time.LocalDateTime;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
public class BoogleMapsApplicationTests {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<Address> addressJson;

    @Autowired
    private JacksonTester<AddressDTO> addressDTOJson;

    private ResultActions findByVehicleId(double lat, double lon) throws Exception {
        return findByVehicleId(lat, lon, Optional.empty());
    }

    private ResultActions findByVehicleId(double lat, double lon, Optional<String> projection) throws Exception {
        return mvc.perform(
                get(new URI("/boogle-maps/addresses/search/findByLatAndLon?lat=" + lat + "&lon=" + lon + (projection.map(d -> ("&projection=" + d)).orElse(""))))
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private AddressDTO getAddress(double lat, double lon, Optional<String> projection) throws Exception {
        return addressDTOJson.parseObject(findByVehicleId(lat, lon, projection).andReturn().getResponse().getContentAsString());
    }

    private ResultActions existsByVehicleId(double lat, double lon) throws Exception {
        return mvc.perform(
                get(new URI("/boogle-maps/addresses/search/existsByLatAndLon?lat=" + lat + "&lon=" + lon))
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private ResultActions createAddress(Address address) throws Exception {
        return mvc.perform(
                post(new URI("/boogle-maps/addresses"))
                        .content(addressJson.write(address).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    private ResultActions editAddress(long addressId, Address address) throws Exception {
        return mvc.perform(
                put(new URI("/boogle-maps/addresses/" + addressId))
                        .content(addressJson.write(address).getJson())
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .accept(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void contextLoads() {
    }

    /**
     * an address requires latitude and longitude to be generated,
     * the fields address, city, state, zip are all auto generated.
     */
    @Test
    public void createAddress() throws Exception {
        Address address = new Address();
        address.setLat(34.567D);
        address.setLon(773.029D);
        createAddress(address)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("lat", is(address.getLat())))
                .andExpect(jsonPath("lon", is(address.getLon())))
                .andExpect(jsonPath("address", notNullValue()))
                .andExpect(jsonPath("city", notNullValue()))
                .andExpect(jsonPath("state", notNullValue()))
                .andExpect(jsonPath("zip", notNullValue()));

        address = new Address();
        address.setLon(773.029D);
        createAddress(address)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasItem(hasEntry("message", "Latitude is required."))));

        address = new Address();
        address.setLat(34.567D);
        createAddress(address)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasItem(hasEntry("message", "Longitude is required."))));
    }

    /**
     * when longitude and or latitude is changed the address, city, state and zip fields are updated.
     */
    @Test
    public void updateAddress() throws Exception {
        Address address = new Address();
        address.setLat(34.567D);
        address.setLon(773.029D);
        createAddress(address)
                .andExpect(status().isCreated());

        AddressDTO addressDTO = getAddress(address.getLat(), address.getLon(), Optional.of("addressFullResponse"));

        address = new Address();
        address.setLat(172.568D);
        address.setLon(773.029D);
        editAddress(addressDTO.getId(), address)
                .andExpect(status().isOk())
                .andExpect(jsonPath("address", not(addressDTO.getAddress())))
                .andExpect(jsonPath("city", not(addressDTO.getCity())))
                .andExpect(jsonPath("state", not(addressDTO.getState())))
                .andExpect(jsonPath("zip", not(addressDTO.getZip())));

        flushAndClearSession(entityManager);
        addressDTO = getAddress(address.getLat(), address.getLon(), Optional.of("addressFullResponse"));

        address = new Address();
        address.setLat(172.568D);
        address.setLon(894.17D);
        editAddress(addressDTO.getId(), address)
                .andExpect(status().isOk())
                .andExpect(jsonPath("address", not(addressDTO.getAddress())))
                .andExpect(jsonPath("city", not(addressDTO.getCity())))
                .andExpect(jsonPath("state", not(addressDTO.getState())))
                .andExpect(jsonPath("zip", not(addressDTO.getZip())));
    }

    public static void flushAndClearSession(EntityManager entityManager) {
        Session session = entityManager.unwrap(Session.class);
        session.flush();
        session.clear();
    }

}
