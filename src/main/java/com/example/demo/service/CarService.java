package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.PageDTO;
import com.example.demo.service.nat.CarNativeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CarService {

    private final CarNativeService carNativeService;


    public PageDTO getCars(CarFilter carFilter) {


        PageDTO<CarDTO> page = carNativeService.getPage(carFilter);


        return page;

    }

}
