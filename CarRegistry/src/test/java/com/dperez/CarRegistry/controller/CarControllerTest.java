package com.dperez.CarRegistry.controller;


import com.dperez.CarRegistry.config.SecurityConfigTest;
import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.controller.mapper.CarDTOAndBrandMapper;
import com.dperez.CarRegistry.controller.mapper.CarDTOMapper;
import com.dperez.CarRegistry.filter.JwtAuthenticationFilter;
import com.dperez.CarRegistry.service.CarService;
import com.dperez.CarRegistry.service.impl.UserServiceImpl;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@Import(SecurityConfigTest.class)
@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CarController carController;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private UserServiceImpl userService;

    @MockBean
    private CarService carService;

    @MockBean
    private CarDTOMapper carDTOMapper;

    @MockBean
    private CarDTOAndBrandMapper carDTOAndBrandMapper;

    private Car carToyota, carHonda;
    private CarDTO carDTOToyota, carDTOHonda;
    private CarDTOAndBrand carDTOAndBrandToyota, carDTOAndBrandHonda;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        Brand brandToyota = Brand.builder().id(1).name("Toyota").build();
        Brand brandHonda = Brand.builder().id(2).name("Honda").build();

        BrandDTO brandDTOToyota = BrandDTO.builder().id(1).name("Toyota").build();
        BrandDTO brandDTOHonda = BrandDTO.builder().id(2).name("Honda").build();

        carDTOAndBrandToyota = new CarDTOAndBrand();
        carDTOAndBrandToyota.setBrand(brandDTOToyota);
        carDTOAndBrandToyota.setModel("Corolla");

        carDTOAndBrandHonda = new CarDTOAndBrand();
        carDTOAndBrandHonda.setBrand(brandDTOHonda);
        carDTOAndBrandHonda.setModel("Civic");


        carToyota = Car.builder().id(1).brand(brandToyota).model("Corolla").build();
        carDTOToyota = CarDTO.builder().id(1).brand("Toyota").model("Corolla").build();

        carHonda = Car.builder().id(2).brand(brandHonda).model("Civic").build();
        carDTOHonda = CarDTO.builder().id(2).brand("Honda").model("Civic").build();
    }

    // TESTS add-car
    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void addCar_Success() throws Exception {

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.addCar(any(Car.class))).thenReturn(carToyota);
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(any(Car.class))).thenReturn(carDTOAndBrandToyota);

        mockMvc.perform(post("/cars/add-car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOToyota)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand.name").value("Toyota"))
                .andExpect(jsonPath("$.model").value("Corolla"));

        verify(carService, times(1)).addCar(any(Car.class));

    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void addCar_IdAlreadyExists() throws Exception {

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.addCar(any(Car.class)))
                .thenThrow(new IllegalArgumentException("Car with this ID already exists"));

        mockMvc.perform(post("/cars/add-car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOToyota)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Car with this ID already exists"));

        verify(carService, times(1)).addCar(any(Car.class));
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void addCar_InternalServerError() throws Exception {

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.addCar(any(Car.class))).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(post("/cars/add-car")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOToyota)))
                .andExpect(status().isInternalServerError());

        verify(carService, times(1)).addCar(any(Car.class));
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void addBunchCars_Success() throws Exception {

        List<CarDTO> carDTOs = Arrays.asList(carDTOToyota, carDTOHonda);
        List<Car> cars = Arrays.asList(carToyota, carHonda);
        List<CarDTOAndBrand> carDTOAndBrands = Arrays.asList(carDTOAndBrandToyota, carDTOAndBrandHonda);

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenAnswer(invocation -> {
            CarDTO dto = invocation.getArgument(0);
            return dto.getId() == 1 ? carToyota : carHonda;
        });
        when(carService.addBunchCars(anyList())).thenReturn(CompletableFuture.completedFuture(cars));
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(any(Car.class))).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            return car.getId() == 1 ? carDTOAndBrandToyota : carDTOAndBrandHonda;
        });

        mockMvc.perform(post("/cars/add-bunch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOs)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].model").value("Corolla"))
                .andExpect(jsonPath("$[1].model").value("Civic"));


        verify(carService, times(1)).addBunchCars(anyList());
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void addBunchCars_ErrorInDataCars() throws Exception {

        List<CarDTO> carDTOs = Arrays.asList(carDTOToyota, carDTOHonda);
        List<Car> cars = Arrays.asList(carToyota, carHonda);

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenAnswer(invocation -> {
            CarDTO carDTO = invocation.getArgument(0);
            return carDTO.getId() == 1 ? carToyota : carHonda;
        });
        when(carService.addBunchCars(anyList())).thenReturn(
                CompletableFuture.failedFuture(new IllegalArgumentException("Error in data Cars"))
        );

        mockMvc.perform(post("/cars/add-bunch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOs)))
                .andExpect(status().isInternalServerError());

        verify(carService, times(1)).addBunchCars(anyList());
    }

    // TESTS updateCarById
    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateCarById_Found() throws Exception {

        Integer idSearched = 1;

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.updateCarById(eq(idSearched), any(Car.class))).thenReturn(carToyota);
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(any(Car.class))).thenReturn(carDTOAndBrandToyota);

        mockMvc.perform(put("/cars/update-car/" + idSearched)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOToyota)))
                .andExpect(jsonPath("$.model").value("Corolla"))
                .andExpect(status().isOk());

        verify(carService, times(1)).updateCarById(idSearched, carToyota);

    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateCarById_NotFound() throws Exception {

        Integer idSearched = 77;

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.updateCarById(eq(idSearched), any(Car.class)))
                .thenThrow(new IllegalArgumentException("Car not found"));

        mockMvc.perform(put("/cars/update-car/" + idSearched)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOToyota)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car not found"));

        verify(carDTOAndBrandMapper, never()).carToCarDTOAndBrand(any(Car.class));
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateCarById_InternalServerError() throws Exception {

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.updateCarById(eq(1), any(Car.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(put("/cars/update-car/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOToyota)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateBunch() throws Exception {

        List<CarDTO> carDTOs = Arrays.asList(carDTOToyota, carDTOHonda);
        List<Car> cars = Arrays.asList(carToyota, carHonda);

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenAnswer(invocation -> {
            CarDTO dto = invocation.getArgument(0);
            return dto.getId() == 1 ? carToyota : carHonda;
        });
        when(carService.updateBunchCars(anyList())).thenReturn(CompletableFuture.completedFuture(cars));
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(any(Car.class))).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            return car.getId() == 1 ? carDTOAndBrandToyota : carDTOAndBrandHonda;
        });

        mockMvc.perform(put("/cars/update-bunch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOs)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].model").value("Corolla"))
                .andExpect(jsonPath("$[1].model").value("Civic"));

        verify(carService, times(1)).updateBunchCars(anyList());
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateBunch_ErrorInDataCars() throws Exception {

        List<CarDTO> carDTOs = Arrays.asList(carDTOToyota, carDTOHonda);

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.updateBunchCars(anyList())).thenReturn(
                CompletableFuture.failedFuture(new IllegalArgumentException("Error in Data Cars"))
        );

        mockMvc.perform(put("/cars/update-bunch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOs)))
                .andExpect(status().isInternalServerError())
                .andReturn();

        verify(carService, times(1)).updateBunchCars(anyList());

    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateBunch_InternalServerError() throws Exception {

        List<CarDTO> carDTOs = Arrays.asList(carDTOToyota, carDTOHonda);

        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.updateBunchCars(anyList())).thenReturn(
                CompletableFuture.failedFuture(new RuntimeException("Unexpected error"))
        );

        mockMvc.perform(put("/cars/update-bunch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOs)))
                .andExpect(status().isInternalServerError());

        verify(carService, times(1)).updateBunchCars(anyList());
    }

    // TESTS getCarById
    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void getCarById_Found() throws Exception {

        when(carService.getCarById(1)).thenReturn(carToyota);
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(any(Car.class))).thenReturn(carDTOAndBrandToyota);

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/cars/get-car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Corolla"));
    }

    @Test
    @WithMockUser(username = "user@user.com", password = "userpass", roles = "CLIENT")
    void getCarById_NotFound() throws Exception {

        Integer idSearched = 77;

        when(carService.getCarById(idSearched)).thenReturn(null);

        mockMvc.perform(get("/cars/get-car/" + idSearched))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car not found"));

        verify(carService, times(1)).getCarById(idSearched);
    }

    @Test
    @WithMockUser(username = "user@user.com", password = "userpass", roles = "CLIENT")
    void getAll_Found() throws Exception {

        List<Car> cars = Arrays.asList(carToyota, carHonda);

        when(carService.getAllCars()).thenReturn(CompletableFuture.completedFuture(cars));

        when(carDTOAndBrandMapper.carToCarDTOAndBrand(any(Car.class))).thenAnswer(invocation -> {
            Car car = invocation.getArgument(0);
            return car.getId() == 1 ? carDTOAndBrandToyota : carDTOAndBrandHonda;
        });

        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/cars/get-all"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].model").value("Corolla"))
                .andExpect(jsonPath("$[1].model").value("Civic"));

        verify(carService, times(1)).getAllCars();
    }

    @Test
    @WithMockUser(username = "user@user.com", password = "userpass", roles = "CLIENT")
    void getAll_InernalServerError() throws Exception {

        when(carService.getAllCars())
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Unexpected error")));

        mockMvc.perform(get("/cars/get-all"))
                .andExpect(status().isInternalServerError());

        verify(carService, times(1)).getAllCars();
    }

    // TESTS Delete
    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void deleteCarById_Success() throws Exception {
        // Given
        Integer idSearched = 1;

        // When
        doNothing().when(carService).deleteCarById(idSearched);

        // Then
        mockMvc.perform(delete("/cars/delete-car/" + idSearched))
                .andExpect(status().isOk())
                .andExpect(content().string("Deleted Car with Id: " + idSearched));

        verify(carService, times(1)).deleteCarById(idSearched);
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void deleteCarById_NotFound() throws Exception {
        // Given
        Integer idSearched = 77;

        // When
        doThrow(new IllegalArgumentException("Car not found with id: " + idSearched))
                .when(carService).deleteCarById(idSearched);

        // Then
        mockMvc.perform(delete("/cars/delete-car/" + idSearched))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car not found with id: " + idSearched));

        verify(carService, times(1)).deleteCarById(idSearched);
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void deleteCarById_InternalServerError() throws Exception {
        // Given
        Integer idSearched = 1;

        // When
        doThrow(new RuntimeException("Unexpected error"))
                .when(carService).deleteCarById(idSearched);

        // Then
        mockMvc.perform(delete("/cars/delete-car/" + idSearched))
                .andExpect(status().isInternalServerError());

        verify(carService, times(1)).deleteCarById(idSearched);
    }

}