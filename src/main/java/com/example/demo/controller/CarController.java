package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.PageDTO;
import com.example.demo.service.CarFilter;
import com.example.demo.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public PageDTO<CarDTO> list(CarFilter carFilter) {

        return carService.getCars(carFilter);
    }
}
