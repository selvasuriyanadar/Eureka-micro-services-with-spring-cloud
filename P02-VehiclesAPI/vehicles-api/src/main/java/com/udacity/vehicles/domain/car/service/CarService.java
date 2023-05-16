package com.udacity.vehicles.domain.car.service;

import com.udacity.vehicles.client.prices.PriceClient;
import com.udacity.vehicles.client.maps.MapsClient;
import com.udacity.vehicles.domain.car.model.Car;
import com.udacity.vehicles.domain.car.repo.CarRepository;
import com.udacity.vehicles.domain.manufacturer.repo.ManufacturerRepository;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implements the car service create, read, update or delete
 * information about vehicles, as well as gather related
 * location and price data when desired.
 */
@Service
public class CarService {

    private final CarRepository repository;
    private final PriceClient priceClient;
    private final MapsClient mapsClient;
    private final ManufacturerRepository manufacturerRepository;
    private final ModelMapper modelMapper;

    public CarService(CarRepository repository, ManufacturerRepository manufacturerRepository, MapsClient mapsClient, PriceClient priceClient, ModelMapper modelMapper) {
        this.repository = repository;
        this.manufacturerRepository = manufacturerRepository;
        this.mapsClient = mapsClient;
        this.priceClient = priceClient;
        this.modelMapper = modelMapper;
    }

    /**
     * Gathers a list of all vehicles
     * @return a list of all vehicles in the CarRepository
     */
    public List<Car> list() {
        return repository.findAll();
    }

    /**
     * Gets car information by ID (or throws exception if non-existent)
     * @param id the ID number of the car to gather information on
     * @return the requested car's information, including location and price
     */
    public Car findById(Long id) {
        return repository.findById(id)
                .map(car -> {
                    mapsClient.fillAddressOptional(car.getLocation());
                    priceClient.fillPriceOptional(car, modelMapper);
                    return car;
                }).orElseThrow(CarNotFoundException::new);
    }

    /**
     * Either creates or updates a vehicle, based on prior existence of car
     * @param car A car object, which can be either new or existing
     * @return the new/updated car is stored in the repository
     */
    public Car save(Car car) {
        if (car.getId() != null) {
            return repository.findById(car.getId())
                    .map(carToBeUpdated -> {
                        carToBeUpdated.setDetails(car.getDetails());
                        carToBeUpdated.setLocation(car.getLocation());
                        return repository.save(carToBeUpdated);
                    }).orElseThrow(CarNotFoundException::new);
        }

        Car newlyCreatedCar = repository.save(car);
        priceClient.updateVehiclePrice(car.getId());
        return newlyCreatedCar;
    }

    /**
     * Deletes a given car by ID
     * @param id the ID number of the car to delete
     */
    public void delete(Long id) {
        if (!repository.findById(id).isPresent()) {
            throw new CarNotFoundException();
        }

        repository.deleteById(id);
    }

}
