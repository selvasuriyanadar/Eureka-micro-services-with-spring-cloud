package com.udacity.vehicles.domain.car.api;

import com.udacity.vehicles.domain.car.model.Car;
import com.udacity.vehicles.domain.car.model.Location;
import com.udacity.vehicles.domain.car.model.Details;
import com.udacity.vehicles.domain.car.service.CarService;
import com.udacity.vehicles.util.BeanUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implements a REST-based controller for the Vehicles API.
 */
@RestController
@RequestMapping("/cars")
class CarController {

    private final CarService carService;
    private final CarResourceAssembler assembler;

    CarController(CarService carService, CarResourceAssembler assembler) {
        this.carService = carService;
        this.assembler = assembler;
    }

    private Car complete(Car car) { car.getDetails().setManufacturerId(car.getDetails().getManufacturer().getCode());
        return car;
    }

    private Car mapCarRequestToModel(Car request, Car car) {
        Details detailsRequest = request.getDetails();
        request.setDetails(null);
        Location locationRequest = request.getLocation();
        request.setLocation(null);
        BeanUtil.transferIfNotNull(request, car);
        if (detailsRequest != null) {
            System.out.println(detailsRequest.getBody() + "------------" + detailsRequest.getModel());
            BeanUtil.transferIfNotNull(detailsRequest, car.getDetails());
            System.out.println(car.getDetails().getBody() + "------------" + car.getDetails().getModel());
        }
        if (locationRequest != null) {
            System.out.println(locationRequest.getLat() + "------------" + locationRequest.getLon());
            BeanUtil.transferIfNotNull(locationRequest, car.getLocation());
            System.out.println(car.getLocation().getLat() + "------------" + car.getLocation().getLon());
        }
        return car;
    }

    /**
     * Creates a list to store any vehicles.
     * @return list of vehicles
     */
    @Operation(summary = "Lists all vehicles.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns list of vehicles.",
            content = { @Content(mediaType = "application/hal+json",
            schema = @Schema(implementation = Car.class)) })
    })
    @GetMapping
    CollectionModel<EntityModel<Car>> list() {
        return assembler.toCollectionModel(carService.list().stream().map(car -> complete(car)).toList());
    }

    /**
     * Gets information of a specific car by ID.
     * @param id the id number of the given vehicle
     * @return all information for the requested vehicle
     */
    @Operation(summary = "Gets information of a specific car by ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns all information for the requested vehicle.",
            content = { @Content(mediaType = "application/hal+json",
            schema = @Schema(implementation = Car.class)) })
    })
    @GetMapping("/{id}")
    EntityModel<Car> get(@Parameter(description = "The id number of the given vehicle.") @PathVariable Long id) {
        return assembler.toModel(complete(carService.findById(id)));
    }

    /**
     * Posts information to create a new vehicle in the system.
     * @param car A new vehicle to add to the system.
     * @return response that the new vehicle was added to the system
     * @throws URISyntaxException if the request contains invalid fields or syntax
     */
    @Operation(summary = "Posts information to create a new vehicle in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Returns response that the new vehicle was added to the system.",
            content = { @Content(mediaType = "application/hal+json",
            schema = @Schema(implementation = Car.class)) })
    })
    @PostMapping
    ResponseEntity<?> post(@Parameter(description = "A new vehicle to add to the system.") @RequestBody Car car) throws URISyntaxException {
        EntityModel<Car> resource = assembler.toModel(complete(carService.save(car)));
        return ResponseEntity.created(new URI(resource.getLink(IanaLinkRelations.SELF).get().getHref())).body(resource);
    }

    /**
     * Updates the information of a vehicle in the system.
     * @param id The ID number for which to update vehicle information.
     * @param car The updated information about the related vehicle.
     * @return response that the vehicle was updated in the system
     */
    @Operation(summary = "Updates the information of a vehicle in the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Returns response that the vehicle was updated in the system.",
            content = { @Content(mediaType = "application/hal+json",
            schema = @Schema(implementation = Car.class)) })
    })
    @PutMapping("/{id}")
    ResponseEntity<?> put(@Parameter(description = "The ID number for which to update vehicle information.") @PathVariable("id") Car car,
            @Parameter(description = "The updated information about the related vehicle.") @RequestBody Car request) {
        EntityModel<Car> resource = assembler.toModel(complete(carService.save(mapCarRequestToModel(request, car))));
        return ResponseEntity.ok(resource);
    }

    /**
     * Removes a vehicle from the system.
     * @param id The ID number of the vehicle to remove.
     * @return response that the related vehicle is no longer in the system
     */
    @Operation(summary = "Removes a vehicle from the system.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Returns response that the related vehicle is no longer in the system.",
            content = @Content)
    })
    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@Parameter(description = "The ID number of the vehicle to remove.") @PathVariable Long id) {
        carService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
