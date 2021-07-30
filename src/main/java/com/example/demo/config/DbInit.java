package com.example.demo.config;

import com.example.demo.entity.Car;
import com.example.demo.repo.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DbInit implements CommandLineRunner {

    private final CarRepository carRepository;

    @Override
    public void run(String... args) throws Exception {

        for (int i = 0; i < 1000; i++) {
            Car car = new Car();

            car.setName("bmw " + i);
            car.setYear(2000 + i);
            carRepository.save(car);
        }
    }
}
