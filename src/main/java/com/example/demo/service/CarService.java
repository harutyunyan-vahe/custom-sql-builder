package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.PageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CarService {

    private final CarNativeService carNativeService;


    public PageDTO getCars(CarFilter carFilter) {


        List<CarDTO> list = carNativeService.list(carFilter);
        long count = carNativeService.count(carFilter);

        PageDTO<CarDTO> carDTOPageDTO = new PageDTO<>();

        carDTOPageDTO.setPage(carFilter.getPage());
        carDTOPageDTO.setData(list);
        carDTOPageDTO.setTotalElements(count);

        return carDTOPageDTO;

    }

}
