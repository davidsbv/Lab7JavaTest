package com.dperez.CarRegistry.controller;


import com.dperez.CarRegistry.config.PasswordConfig;
import com.dperez.CarRegistry.config.SecurityConfigTest;
import com.dperez.CarRegistry.controller.dtos.BrandDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTO;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.controller.mapper.CarDTOAndBrandMapper;
import com.dperez.CarRegistry.controller.mapper.CarDTOMapper;
import com.dperez.CarRegistry.filter.JwtAuthenticationFilter;
import com.dperez.CarRegistry.service.CarService;
import com.dperez.CarRegistry.service.impl.JwtService;
import com.dperez.CarRegistry.service.impl.UserServiceImpl;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@Import({SecurityConfigTest.class, JwtAuthenticationFilter.class, JwtService.class, PasswordConfig.class})
@WebMvcTest(CarController.class)
class CarControllerTest{

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

    private Car carToyota;
    private Brand brandToyota;
    private BrandDTO brandDTOToyota;
    private CarDTO carDTOToyota;
    private CarDTOAndBrand carDTOAndBrandToyota;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        brandToyota = Brand.builder().id(1).name("Toyota").build();
        brandDTOToyota = BrandDTO.builder().id(1).name("Toyota").build();

        carDTOAndBrandToyota = new CarDTOAndBrand();
        carDTOAndBrandToyota.setBrand(brandDTOToyota);
        carDTOAndBrandToyota.setModel("Corolla");

        carToyota = Car.builder().id(1).brand(brandToyota).model("Corolla").build();
        carDTOToyota = CarDTO.builder().id(1).brand("Toyota").model("Corolla").build();
    }

    // TESTS getCarById
    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void getCarById_Found() throws Exception {

        // When
        when(carService.getCarById(1)).thenReturn(carToyota);
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(carToyota)).thenReturn(carDTOAndBrandToyota);

        // Then
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/cars/get-car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Corolla"));
    }

    @Test
    @WithMockUser(username = "user@user.com", password = "userpass", roles = "CLIENT")
    void getCarById_NotFound() throws Exception{
        // Given
        Integer idSearched = 77;

        // When
        when(carService.getCarById(idSearched)).thenReturn(null);

        // Then
        this.mockMvc
                .perform(get("/cars/get-car/" + idSearched))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car not found"));

        verify(carService, times(1)).getCarById(idSearched);
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateCarById_Found () throws Exception {
       // Given
        Integer idSearched = 1;

        // when
        when(carDTOMapper.carDTOToCar(any(CarDTO.class))).thenReturn(carToyota);
        when(carService.updateCarById(eq(idSearched), any(Car.class))).thenReturn(carToyota);
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(carToyota)).thenReturn(carDTOAndBrandToyota);

        // Then
        mockMvc.perform(put("/cars/update-car/" + idSearched)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDTOToyota)))
                        .andExpect(jsonPath("$.model").value("Corolla"))
                        .andExpect(status().isOk());

        verify(carService, times(1)).updateCarById(idSearched,carToyota);

    }


    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void updateCarById_NotFound () throws Exception {
        // Given
        Integer idSearched = 77;

        // When
        when(carDTOMapper.carDTOToCar(carDTOToyota)).thenReturn(carToyota);
        when(carService.updateCarById(eq(idSearched), any(Car.class)))
                .thenThrow(new IllegalArgumentException("Car not found"));

        // Then
        mockMvc.perform(put("/cars/update-car/" + idSearched)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDTOToyota)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Car not found"));

        verify(carDTOAndBrandMapper, never()).carToCarDTOAndBrand(any(Car.class));
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = {"VENDOR"})
    void testAddBunchCars_Successssss() throws Exception {
        String requestBody = "[{\"brand\":\"Toyota\",\"model\":\"Corolla\"}," +
                "{\"brand\":\"Honda\",\"model\":\"Civic\"}]";

        this.mockMvc.
                perform(post("/cars/add-bunch")
                        .header("Authorization", "Bearer mock-jwt-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

}