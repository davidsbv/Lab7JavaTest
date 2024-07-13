package com.dperez.CarRegistry.controller;


import com.dperez.CarRegistry.config.PasswordConfig;
import com.dperez.CarRegistry.config.SecurityConfig;
import com.dperez.CarRegistry.config.SecurityConfigTest;
import com.dperez.CarRegistry.controller.dtos.CarDTOAndBrand;
import com.dperez.CarRegistry.controller.mapper.CarDTOAndBrandMapper;
import com.dperez.CarRegistry.controller.mapper.CarDTOMapper;
import com.dperez.CarRegistry.filter.JwtAuthenticationFilter;
import com.dperez.CarRegistry.service.CarService;
import com.dperez.CarRegistry.service.impl.JwtService;
import com.dperez.CarRegistry.service.impl.UserServiceImpl;
import com.dperez.CarRegistry.service.model.Brand;
import com.dperez.CarRegistry.service.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@Import({SecurityConfigTest.class, JwtAuthenticationFilter.class, JwtService.class, PasswordConfig.class})
@WebMvcTest(CarController.class)
class CarControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

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

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(username = "vendor@vendor.com", password = "vendorpass", roles = "VENDOR")
    void getCar_test() throws Exception {
        // Given
        Brand brand = Brand.builder().id(1).name("Toyota").build();
        CarDTOAndBrand  carDTOAndBrand = new CarDTOAndBrand();
        Car car = new Car();
        car.setModel("Corolla");
        car.setBrand(brand);
        carDTOAndBrand.setBrand(carDTOAndBrand.getBrand());
        carDTOAndBrand.setModel(car.getModel());

        // When
        when(carService.getCarById(1)).thenReturn(car);
        when(carDTOAndBrandMapper.carToCarDTOAndBrand(car)).thenReturn(carDTOAndBrand);

        // Then
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/cars/get-car/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model").value("Corolla"));
    }
}